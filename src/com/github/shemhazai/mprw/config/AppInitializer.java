package com.github.shemhazai.mprw.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.github.shemhazai.mprw.data.DataCollector;
import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.RiverStatus;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.notify.Notifier;
import com.github.shemhazai.mprw.repo.RiverRepository;
import com.github.shemhazai.mprw.repo.RiverStatusRepository;
import com.github.shemhazai.mprw.repo.UserRepository;

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

	public void setNotifiers(List<Notifier> notifiers) {
		this.notifiers = notifiers;
	}

	public void addNotifier(Notifier notifier) {
		if (notifiers == null)
			notifiers = new ArrayList<>();
		notifiers.add(notifier);
	}

	public void removeNotifier(Notifier notifier) {
		if (notifiers != null)
			notifiers.remove(notifier);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(AppConfig.class);
		servletContext.addListener(new ContextLoaderListener(context));
		ServletRegistration.Dynamic dispatcher = servletContext
				.addServlet("DispatcherServlet", new DispatcherServlet(context));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/*");
	}

	@PostConstruct
	public void postConstruct() {
		riverRepository.createRiverTableIfNotExists();
		riverStatusRepository.createRiverStatusTableIfNotExists();
		userRepository.createUserTableIfNotExists();
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
	public void analizeData() {

		List<River> listOfRiver = riverRepository.selectRiversInDanger();

		if (!listOfRiver.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append("Uwaga.\n");
			builder.append("Stan zagrożenia powodziowego na rzekach:\n");

			for (River river : listOfRiver) {
				builder.append(" * " + river.getDescription());
				builder.append(
						", obecny poziom: " + lastRiverLevel(river.getId()) + "cm");
				builder.append(", poziom powodziowy: " + river.getFloodLevel() + "cm");
				builder.append(", poziom alarmowy: " + river.getAlertLevel() + "cm.\n");
			}

			List<User> users = userRepository.selectUsersWithEmailAlert();
			List<String> contacts = new ArrayList<>();
			users.forEach((u) -> contacts.add(u.getEmail()));

			String subject = "Ostrzeżenie o zagrożeniu powodziowym.";
			String message = builder.toString();

			logger.warn(message);
			for (Notifier notifier : notifiers)
				notifier.notifyEveryone(contacts, subject, message);
		}

	}

	private int lastRiverLevel(int riverId) {
		List<RiverStatus> list = riverStatusRepository
				.selectLastRiverStatusesByRiverIdLimit(riverId, 1);
		return list.get(0).getLevel();
	}
}
