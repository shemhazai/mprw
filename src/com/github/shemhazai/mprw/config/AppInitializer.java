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
import com.github.shemhazai.mprw.domain.HashedUser;
import com.github.shemhazai.mprw.notify.MailNotifier;
import com.github.shemhazai.mprw.repo.RiverRepository;
import com.github.shemhazai.mprw.repo.RiverStatusRepository;
import com.github.shemhazai.mprw.repo.HashedUserRepository;

@Configuration
public class AppInitializer implements WebApplicationInitializer {

	private final Logger logger = Logger.getLogger(getClass());

	@Autowired
	private RiverRepository riverRepository;
	@Autowired
	private RiverStatusRepository riverStatusRepository;
	@Autowired
	private HashedUserRepository userRepository;
	@Autowired
	private DataCollector collector;
	@Autowired
	private MailNotifier mailNotifier;

	public AppInitializer() {

	}

	public RiverRepository getRiverRepository() {
		return riverRepository;
	}

	public RiverStatusRepository getRiverStatusRepository() {
		return riverStatusRepository;
	}

	public HashedUserRepository getUserRepository() {
		return userRepository;
	}

	public DataCollector getCollector() {
		return collector;
	}

	public MailNotifier getMailNotifier() {
		return mailNotifier;
	}

	public void setRiverRepository(RiverRepository riverRepository) {
		this.riverRepository = riverRepository;
	}

	public void setRiverStatusRepository(RiverStatusRepository riverStatusRepository) {
		this.riverStatusRepository = riverStatusRepository;
	}

	public void setUserRepository(HashedUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setCollector(DataCollector collector) {
		this.collector = collector;
	}

	public void setMailNotifier(MailNotifier mailNotifier) {
		this.mailNotifier = mailNotifier;
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(AppConfig.class);
		servletContext.addListener(new ContextLoaderListener(context));
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet",
				new DispatcherServlet(context));
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
			mailNotifier.notifyOne(mailNotifier.getAdminEmail(), "Errors when collecting data.", builder.toString());
		}
	}

	@Scheduled(fixedDelay = 1800000)
	public void analizeData() {

		List<River> listOfRiver = riverRepository.selectRiversInDanger();

		if (!listOfRiver.isEmpty()) {
			String title = "Ostrzeżenie o zagrożeniu powodziowym.";

			StringBuilder builder = new StringBuilder();
			builder.append("Uwaga.\n");
			builder.append("Stan zagrożenia powodziowego na rzekach:\n");

			for (River river : listOfRiver) {
				builder.append(" * " + river.getDescription());
				builder.append(", obecny poziom: " + lastRiverLevel(river.getId()) + "cm");
				builder.append(", poziom powodziowy: " + river.getFloodLevel() + "cm");
				builder.append(", poziom alarmowy: " + river.getAlertLevel() + "cm.\n");
			}

			List<HashedUser> users = userRepository.selectUsersWithEmailAlert();
			List<String> contacts = new ArrayList<>();
			users.forEach((u) -> contacts.add(u.getEmail()));

			logger.warn(builder.toString());
			mailNotifier.notifyEveryone(contacts, title, builder.toString());
		}

	}

	private int lastRiverLevel(int riverId) {
		return riverStatusRepository.selectLastRiverStatusByRiverId(riverId).getLevel();
	}
}
