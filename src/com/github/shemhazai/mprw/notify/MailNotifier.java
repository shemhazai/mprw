package com.github.shemhazai.mprw.notify;

import com.github.shemhazai.mprw.domain.River;
import com.github.shemhazai.mprw.domain.UserContact;
import com.github.shemhazai.mprw.utils.ResourceLoader;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.util.List;
import java.util.Properties;

public class MailNotifier implements Notifier {

    private Logger logger = Logger.getLogger(getClass());
    private Properties props;
    private String adminEmail;
    private String email;
    private String login;
    private String password;

    public MailNotifier() {

    }

    private Session createSession() {
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(login, password);
            }
        });
    }

    @Override
    public void notifyAdmin(String subject, String text) {
        try {
            Session session = createSession();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setSubject(subject);
            message.setText(text);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(adminEmail));

            Transport.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void warnAboutFlood(List<String> contacts, List<River> rivers) {
        try {
            Session session = createSession();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setSubject("Ostrzeżenie o zagrożeniu powodziowym");

            String text = new ResourceLoader().readFile("warnAboutFlood.html");
            message.setText(text, "utf-8", "html");

            for (String recipient : contacts) {
                InternetAddress address = new InternetAddress(recipient);
                message.addRecipient(RecipientType.CC, address);
            }

            Transport.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void sendVerifyLink(String contact, String link) {
        try {
            Session session = createSession();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setSubject("Aktywacja konta w mprw.pl");
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(contact));

            String text = new ResourceLoader().readFile("sendVerifyLink.html");
            text = text.replace("###LINK###", link);
            message.setText(text, "utf-8", "html");

            Transport.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public boolean contact(UserContact contact) {
        try {
            Session session = createSession();
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(email));
            mimeMessage.setSubject("Wiadomość od " + contact.getName());
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(adminEmail));

            String text = new ResourceLoader().readFile("contact.html");
            text = text.replace("###NAME###", contact.getName());
            text = text.replace("###EMAIL###", contact.getEmail());
            text = text.replaceAll("###MESSAGE###", contact.getMessage());
            mimeMessage.setText(text, "utf-8", "html");

            Transport.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
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

}
