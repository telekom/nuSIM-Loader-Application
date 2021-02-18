package de.scoopgmbh.nusimapp.if3;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetCertFromCMRequest {
    private String eid;
    @JsonProperty("rootKeyID")
    private String rootKeyId;

    public GetCertFromCMRequest() {
    }

    public GetCertFromCMRequest(String eid, String rootKeyId) {
        this.eid = eid;
        this.rootKeyId = rootKeyId;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getRootKeyId() {
        return rootKeyId;
    }

    public void setRootKeyId(String rootKeyId) {
        this.rootKeyId = rootKeyId;
    }
}
