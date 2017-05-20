package com.github.shemhazai.mprw.config;

import com.github.shemhazai.mprw.data.DataCollector;
import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.notify.Notifier;
import com.github.shemhazai.mprw.repo.RiverRepository;
import com.github.shemhazai.mprw.repo.RiverStatusRepository;
import com.github.shemhazai.mprw.repo.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class AppInitializer implements WebApplicationInitializer {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private RiverRepository riverRepository;
    @Autowired
    private RiverStatusRepository riverStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DataCollector collector;
    @Autowired
    private List<Notifier> notifiers;

    public AppInitializer() {
        notifiers = new ArrayList<>();
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        servletContext.addListener(new ContextLoaderListener(context));
        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("DispatcherServlet", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
    }

    @Scheduled(fixedDelay = 600000)
    public void collectData() {
        try {
            collector.collect();
        } catch (IOException | ParseException e) {
            StringBuilder builder = new StringBuilder();
            builder.append("Errors occured.\n");
            builder.append("Exception: " + e.getClass() + "\n");
            builder.append("Message: " + e.getMessage() + "\n");
            builder.append("Date: " + new Date() + "\n");
            builder.append("\nStackTrace:\n");

            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            builder.append(stackTrace + "\n");

            logger.error(e.getMessage(), e);

            String subject = "Errors when collecting data.";
            String message = builder.toString();
            for (Notifier notifier : notifiers)
                notifier.notifyAdmin(subject, message);
        }
    }

    @Scheduled(fixedDelay = 1800000)
    public void analyzeData() {
        List<River> riversInDanger = riverRepository.selectRiversInDanger();
        if (!riversInDanger.isEmpty()) {
            List<User> users = userRepository.selectUsersWithEmailAlert();
            List<String> contacts = new ArrayList<>();
            users.forEach((u) -> contacts.add(u.getEmail()));

            logger.warn("Rivers in danger: " + riversInDanger);
            for (Notifier notifier : notifiers)
                notifier.warnAboutFlood(contacts, riversInDanger);
        }

    }

    public RiverRepository getRiverRepository() {
        return riverRepository;
    }

    public RiverStatusRepository getRiverStatusRepository() {
        return riverStatusRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public DataCollector getCollector() {
        return collector;
    }

    public List<Notifier> getNotifiers() {
        return notifiers;
    }

    public void setRiverRepository(RiverRepository riverRepository) {
        this.riverRepository = riverRepository;
    }

    public void setRiverStatusRepository(RiverStatusRepository riverStatusRepository) {
        this.riverStatusRepository = riverStatusRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setCollector(DataCollector collector) {
        this.collector = collector;
    }

    public void addNotifier(Notifier notifier) {
        notifiers.add(notifier);
    }

    public void removeNotifier(Notifier notifier) {
        notifiers.remove(notifier);
    }

}
