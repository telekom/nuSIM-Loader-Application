package de.scoopgmbh.nusimapp.if3;

public class GetCertFromCMResponse {

    private String eid;
    private String cert;

    public GetCertFromCMResponse() {
    }

    public GetCertFromCMResponse(String eid, String cert) {
        this.eid = eid;
        this.cert = cert;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }
}
