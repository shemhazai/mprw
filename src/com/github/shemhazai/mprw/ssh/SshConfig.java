package com.github.shemhazai.mprw.ssh;

public class SshConfig {

    private String remoteHost;
    private String localHost;
    private int sshPort;
    private int localPort;
    private int remotePort;
    private String username;
    private String password;

    public SshConfig() {

    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public int getSshPort() {
        return sshPort;
    }

    public void setSshPort(int sshPort) {
        this.sshPort = sshPort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SshConfig sshConfig = (SshConfig) o;

        if (sshPort != sshConfig.sshPort) return false;
        if (localPort != sshConfig.localPort) return false;
        if (remotePort != sshConfig.remotePort) return false;
        if (remoteHost != null ? !remoteHost.equals(sshConfig.remoteHost) : sshConfig.remoteHost != null) return false;
        if (localHost != null ? !localHost.equals(sshConfig.localHost) : sshConfig.localHost != null) return false;
        if (username != null ? !username.equals(sshConfig.username) : sshConfig.username != null) return false;
        return password != null ? password.equals(sshConfig.password) : sshConfig.password == null;
    }

    @Override
    public int hashCode() {
        int result = remoteHost != null ? remoteHost.hashCode() : 0;
        result = 31 * result + (localHost != null ? localHost.hashCode() : 0);
        result = 31 * result + sshPort;
        result = 31 * result + localPort;
        result = 31 * result + remotePort;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SshConfig{" +
                "remoteHost='" + remoteHost + '\'' +
                ", localHost='" + localHost + '\'' +
                ", sshPort=" + sshPort +
                ", localPort=" + localPort +
                ", remotePort=" + remotePort +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
