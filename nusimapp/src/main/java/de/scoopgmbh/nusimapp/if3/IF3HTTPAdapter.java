package de.scoopgmbh.nusimapp.if3;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import de.scoopgmbh.nusimapp.http.HttpSender;
import de.scoopgmbh.nusimapp.http.HttpSenderConfig;

public class IF3HTTPAdapter extends IF3Adapter {
    private HttpSender sender;

    public IF3HTTPAdapter(Config config) throws Exception {
        super(config);
        HttpSenderConfig httpSenderConfig = ConfigBeanFactory.create(config, HttpSenderConfig.class);
        sender = new HttpSender(httpSenderConfig, "CM connector");
        sender.start();
    }

    @Override
    public String getCertificate(String eid, String rootKeyId) throws IF3Exception {
        try {
            return sender.send("GetCertFromCM", new GetCertFromCMRequest(eid, rootKeyId), GetCertFromCMResponse.class).getCert();
        } catch (Exception e) {
            throw new IF3Exception(e);
        }
    }
}
