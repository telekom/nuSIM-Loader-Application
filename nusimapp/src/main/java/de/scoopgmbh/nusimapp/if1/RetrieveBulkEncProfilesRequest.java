package de.scoopgmbh.nusimapp.if1;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RetrieveBulkEncProfilesRequest {
    private String reqVersion = "1.0";

    @JsonProperty("referenceID")
    private String referenceId;

    public RetrieveBulkEncProfilesRequest() {
    }

    public RetrieveBulkEncProfilesRequest(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReqVersion() {
        return reqVersion;
    }

    public String getReferenceId() {
        return referenceId;
    }
}
