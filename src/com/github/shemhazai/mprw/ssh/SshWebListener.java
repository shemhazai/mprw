package com.github.shemhazai.mprw.ssh;

import com.github.shemhazai.mprw.config.Beans;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
@Component
public class SshWebListener implements ServletContextListener {

    private SshConnection sshConnection = Beans.sshConnection();

    public SshWebListener() {
        super();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
           sshConnection.connect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sshConnection.close();
    }

}
