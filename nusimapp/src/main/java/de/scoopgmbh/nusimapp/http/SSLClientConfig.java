package de.scoopgmbh.nusimapp.http;

public class SSLClientConfig {
    private String certDir;
    private String password;

    public String getCertDir() {
        return certDir;
    }

    public void setCertDir(String certDir) {
        this.certDir = certDir;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
