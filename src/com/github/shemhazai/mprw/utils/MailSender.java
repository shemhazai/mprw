package com.github.shemhazai.mprw.utils;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.log4j.Logger;

public class MailSender {

	private Logger logger = Logger.getLogger(getClass());
	private Properties props;

	public MailSender(Properties props) {
		this.props = props;
	}

	public MailSender() {

	}

	public Properties getProperties() {
		return props;
	}

	public void setProperties(Properties props) {
		this.props = props;
	}

	public void sendMailToAdmin(String subject, String text) {
		try {
			Transport.send(createMessage(subject, text));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void sendMailToMany(List<String> recipients, String subject, String text) {
		try {
			Message message = createMessage(subject, text);
			for (String recipient : recipients)
				message.addRecipient(RecipientType.CC, new InternetAddress(recipient));
			Transport.send(message);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private Message createMessage(String subject, String text) throws Exception {
		Session session = createSession();
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(props.getProperty("email")));
		InternetAddress admin = new InternetAddress(props.getProperty("admin"));
		message.setRecipient(Message.RecipientType.TO, admin);
		message.setSubject(subject);
		message.setText(text);
		return message;
	}

	private Session createSession() {
		return Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(props.getProperty("login"), props.getProperty("password"));
			}
		});
	}
}
