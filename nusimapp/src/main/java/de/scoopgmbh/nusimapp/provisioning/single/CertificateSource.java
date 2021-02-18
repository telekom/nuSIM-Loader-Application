package de.scoopgmbh.nusimapp.provisioning.single;

public enum CertificateSource {
    db("local Database"),
    nusim("nuSIM"),
    cm("CM")
    ;

    private final String humanReadable;

    CertificateSource(final String humanReadable){
        this.humanReadable = humanReadable;
    }

    @Override
    public String toString() {
        return humanReadable;
    }
}
