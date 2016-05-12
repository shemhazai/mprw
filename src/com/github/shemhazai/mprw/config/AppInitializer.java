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

		List<River> listOfRiver = selectRiversInDanger();

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

			List<String> recipients = selectVerifiedRecipients();

			logger.warn(builder.toString());
			mailSender.sendMailToMany(recipients, title, builder.toString());
		}
	}

	private List<River> selectRiversInDanger() {
		List<River> listOfRiver = new ArrayList<>();
		for (River river : repository.selectAllRivers()) {
			RiverStatus riverStatus = repository.selectLastRiverStatusByRiverId(river.getId());
			if (riverStatus == null)
				continue;

			int level = riverStatus.getLevel();
			int floodLevel = river.getFloodLevel();
			int alertLevel = river.getAlertLevel();
			if (floodLevel <= level || alertLevel <= level)
				listOfRiver.add(river);
		}
		return listOfRiver;
	}

	private int lastRiverLevel(int riverId) {
		return repository.selectLastRiverStatusByRiverId(riverId).getLevel();
	}

	private List<String> selectVerifiedRecipients() {
		List<String> recipients = new ArrayList<>();
		for (User user : repository.selectAllUsers()) {
			if (user.isVerified())
				recipients.add(user.getEmail());
		}
		return recipients;
	}

}
