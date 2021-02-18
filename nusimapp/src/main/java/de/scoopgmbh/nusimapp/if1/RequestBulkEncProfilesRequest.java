package de.scoopgmbh.nusimapp.if1;

import java.util.List;
import java.util.Optional;

public class RequestBulkEncProfilesRequest {
    private String reqVersion = "1.0";
    private String nusimVersion = "1.0";
    private String rootKeyID;
    private String authFlag;
    private String certCM;
    private String refInfo1;
    private String refInfo2;
    private String refInfo3;
    private String num;
    private List<Eid> esList;

    public static final class Eid {
        public String eid;
        public String certNUSIM;

        public Eid() {
        }

        public Eid(String eid, String certNUSIM) {
            this.eid = eid;
            this.certNUSIM = certNUSIM;
        }

        public String getEid() {
            return eid;
        }

        public String getCertNUSIM() {
            return certNUSIM;
        }
    }

    public RequestBulkEncProfilesRequest() {
    }

    public RequestBulkEncProfilesRequest(String rootKeyID, String authFlag, String certCM, String refInfo1, String refInfo2, String refInfo3, String num, List<Eid> esList) {
        this.rootKeyID = rootKeyID;
        this.authFlag = authFlag;
        this.certCM = certCM;
        this.refInfo1 = Optional.ofNullable(refInfo1).orElse("");
        this.refInfo2 = Optional.ofNullable(refInfo2).orElse("");
        this.refInfo3 = Optional.ofNullable(refInfo3).orElse("");
        this.num = num;
        this.esList = esList;
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

    public String getAuthFlag() {
        return authFlag;
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

    public String getNum() {
        return num;
    }

    public List<Eid> getEsList() {
        return esList;
    }
}
