package de.scoopgmbh.nusimapp.server.api.provisioning;

import java.time.Instant;

public class ReferenceIdRecord {
    public String referenceId;
    public String refInfo1;
    public String refInfo2;
    public String refInfo3;
    public Instant requestedAt;
    public int requestCount;
    public Instant availableAt;
}
