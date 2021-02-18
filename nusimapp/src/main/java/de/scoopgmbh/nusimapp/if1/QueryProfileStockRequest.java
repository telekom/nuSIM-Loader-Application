package de.scoopgmbh.nusimapp.if1;

import java.util.Optional;

public class QueryProfileStockRequest {
    private String reqVersion = "1.0";
    private String refInfo1;
    private String refInfo2;
    private String refInfo3;

    public QueryProfileStockRequest() {
    }

    public QueryProfileStockRequest(String refInfo1, String refInfo2, String refInfo3) {
        this.refInfo1 = Optional.ofNullable(refInfo1).orElse("");
        this.refInfo2 = Optional.ofNullable(refInfo2).orElse("");
        this.refInfo3 = Optional.ofNullable(refInfo3).orElse("");
    }

    public String getReqVersion() {
        return reqVersion;
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
