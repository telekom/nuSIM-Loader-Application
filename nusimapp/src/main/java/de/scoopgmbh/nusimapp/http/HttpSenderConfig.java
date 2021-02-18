package de.scoopgmbh.nusimapp.http;

public class HttpSenderConfig {

    private String url;
    private SSLClientConfig ssl;
    private int maxContentSize;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SSLClientConfig getSsl() {
        return ssl;
    }

    public void setSsl(SSLClientConfig ssl) {
        this.ssl = ssl;
    }

    public int getMaxContentSize() {
        return maxContentSize;
    }

    public void setMaxContentSize(int maxContentSize) {
        this.maxContentSize = maxContentSize;
    }
}
