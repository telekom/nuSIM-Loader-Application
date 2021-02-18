package de.scoopgmbh.nusimapp.if1;

import java.util.Optional;

public class GetEncProfileRequest {
    private String reqVersion = "1.0";
    private String nusimVersion = "1.0";
    private String rootKeyID;
    private String eid;
    private String authFlag;
    private String certNUSIM;
    private String certCM;
    private String refInfo1;
    private String refInfo2;
    private String refInfo3;

    public GetEncProfileRequest() {
    }

    public GetEncProfileRequest(String rootKeyID, String eid, String authFlag, String certNUSIM, String certCM, String refInfo1, String refInfo2, String refInfo3) {
        this.rootKeyID = rootKeyID;
        this.eid = eid;
        this.authFlag = authFlag;
        this.certNUSIM = certNUSIM;
        this.certCM = certCM;
        this.refInfo1 = Optional.ofNullable(refInfo1).orElse("");
        this.refInfo2 = Optional.ofNullable(refInfo2).orElse("");
        this.refInfo3 = Optional.ofNullable(refInfo3).orElse("");
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

    public String getEid() {
        return eid;
    }

    public String getAuthFlag() {
        return authFlag;
    }

    public String getCertNUSIM() {
        return certNUSIM;
    }

    public String getCertCM() {
        return certCM;
    }

    public String getRefInfo1() {
        return refInfo1;
    }

    public String getRefInfo2() {
        return refInfo2;
    }

    public String getRefInfo3() {
        return refInfo3;
    }
}
