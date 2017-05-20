package com.github.shemhazai.mprw.ssh;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SshConnection {

    private Session session;
    private SshConfig config;

    public SshConnection() {

    }

    public void connect() throws Throwable {
        JSch jsch = new JSch();
        session = jsch.getSession(config.getUsername(),
                config.getRemoteHost(), config.getSshPort());
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(config.getPassword());
        session.setPortForwardingL(config.getLocalPort(),
                config.getLocalHost(), config.getRemotePort());
        session.connect(30000);
    }

    public void close() {
        if (session != null) {
            session.disconnect();
        }
    }

    public Session getSession() {
        return session;
    }

    public SshConfig getConfig() {
        return config;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setConfig(SshConfig config) {
        this.config = config;
    }

}
