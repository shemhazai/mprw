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

import com.github.shemhazai.mprw.data.DataAnalizer;
import com.github.shemhazai.mprw.data.DataCollector;
import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.User;
import com.github.shemhazai.mprw.repo.AppRepository;
import com.github.shemhazai.mprw.utils.MailSender;

@Configuration
public class AppInitializer implements WebApplicationInitializer {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private AppRepository repository;

	@Autowired
	private DataCollector collector;

	@Autowired
	private DataAnalizer analizer;

	@Autowired
	private MailSender mailSender;

	public AppInitializer() {

	}

	public AppRepository getRepository() {
		return repository;
	}

	public void setRepository(AppRepository repository) {
		this.repository = repository;
	}

	public DataCollector getCollector() {
		return collector;
	}

	public void setCollector(DataCollector collector) {
		this.collector = collector;
	}

	public DataAnalizer getAnalizer() {
		return analizer;
	}

	public void setAnalizer(DataAnalizer analizer) {
		this.analizer = analizer;
	}

	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
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
		repository.createRiverTableIfNotExists();
		repository.createRiverStatusTableIfNotExists();
		repository.createUserTableIfNotExists();
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
			mailSender.sendMailToAdmin("Errors when collecting data.", builder.toString());
		}
	}

	// TODO sprawdzanie czy powiadomienie zostało już wysłane
	// TODO sformatować wiadomość
	@Scheduled(fixedDelay = 1800000)
	public void analizeData() {
		boolean floodLevelReached = analizer.isFloodLevelReached();
		boolean alertLevelReached = analizer.isAlertLevelReached();

		if (floodLevelReached || alertLevelReached) {
			String title = "Ostrzeżenie o stanie " + (floodLevelReached ? "powodziowym." : "alarmowym.");
			String message = buildMessage(floodLevelReached);
			List<String> recipients = buildRecipients();

			logger.warn(message);
			mailSender.sendMailToMany(recipients, title, message);
		}
	}

	private String buildMessage(boolean flood) {
		StringBuilder builder = new StringBuilder();
		builder.append("Uwaga.\n");
		builder.append("Stan " + (flood ? "powodziowy" : "alarmowy") + " został osiągnięty w punktach:\n");

		List<River> listOfRiver = (flood ? analizer.getRiversWithFloodLevelReached()
				: analizer.getRiversWithAlertLevelReached());

		for (River river : listOfRiver) {
			builder.append(" * " + river.getDescription());
			builder.append(", poziom powodziowy: " + river.getFloodLevel() + "cm");
			builder.append(", poziom alarmowy: " + river.getAlertLevel() + "cm");
			builder.append(", obecny poziom: " + lastRiverLevel(river.getId()) + "cm.\n");
		}

		return builder.toString();
	}

	private int lastRiverLevel(int riverId) {
		return repository.selectLastRiverStatusByRiverId(riverId).getLevel();
	}

	private List<String> buildRecipients() {
		List<String> recipients = new ArrayList<>();
		for (User user : repository.selectAllUsers()) {
			if (user.isVerified())
				recipients.add(user.getEmail());
		}
		return recipients;
	}

}
