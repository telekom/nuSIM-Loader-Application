package de.scoopgmbh.nusimapp.if1;

public class GetKPubDPRequest {
    private String reqVersion = "1.0";
    private String nusimVersion = "1.0";
    private String rootKeyID;

    public GetKPubDPRequest() {
    }

    public GetKPubDPRequest(String rootKeyID) {
        this.rootKeyID = rootKeyID;
    }

    public String getReqVersion() {
        return reqVersion;
    }

    public String getNusimVersion() {
        return nusimVersion;
    }

    public String getRootKeyID() {
        return rootKeyID;
    }
}
