package com.github.shemhazai.mprw.notify;

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

public class MailNotifier implements Notifier {

	private Logger logger = Logger.getLogger(getClass());
	private Properties props;
	private String adminEmail;
	private String email;
	private String login;
	private String password;

	public MailNotifier() {

	}

	public Properties getProperties() {
		return props;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public String getEmail() {
		return email;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public void setProperties(Properties props) {
		this.props = props;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void notifyOne(String contact, String subject, String text) {
		try {
			Message message = createMessage(subject, text);
			InternetAddress address = new InternetAddress(contact);
			message.setRecipient(Message.RecipientType.TO, address);

			Transport.send(message);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void notifyAdmin(String subject, String message) {
		notifyOne(adminEmail, subject, message);
	}

	@Override
	public void notifyEveryone(List<String> contacts, String subject, String text) {
		try {
			Message message = createMessage(subject, text);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(adminEmail));

			for (String recipient : contacts) {
				InternetAddress address = new InternetAddress(recipient);
				message.addRecipient(RecipientType.CC, address);
			}

			Transport.send(message);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private Message createMessage(String subject, String text) throws Exception {
		Session session = createSession();
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(email));
		message.setSubject(subject);
		message.setText(text);
		return message;
	}

	private Session createSession() {
		return Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(login, password);
			}
		});
	}
}
