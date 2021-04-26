/*
Copyright 2020 HiSilicon (Shanghai) Limited

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */
package de.scoopgmbh.nusimapp.nusimsim.adapter.huawei;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import de.scoopgmbh.nusimapp.nusimsim.adapter.NoEidAvailableException;
import de.scoopgmbh.nusimapp.nusimsim.adapter.ProfileLoadingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.startsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class HuaweiNusimSimAdapterTest {

    private final int commandRetries = 3; // must be higher than 1
    private ISerialPortHandler serialPortHandler;
    private Supplier<ISerialPortHandler> serialPortHandlerSupplier;
    private Config adapterConfig;

    @Before
    public void setup() {
        serialPortHandler = mock(ISerialPortHandler.class);
        serialPortHandlerSupplier = () -> serialPortHandler;

        adapterConfig = ConfigFactory.empty()
                .withValue("serialPort", ConfigValueFactory.fromAnyRef("DUMMY-UNUSED"))
                .withValue("baudRate", ConfigValueFactory.fromAnyRef(1))
                .withValue("commandRetries", ConfigValueFactory.fromAnyRef(commandRetries))
                .withValue("commandTimeoutMs", ConfigValueFactory.fromAnyRef(20))
                .withValue("commandRetryDelayMs", ConfigValueFactory.fromAnyRef(100));
    }

    @After
    public void tearDown() {
        verify(serialPortHandler, times(1)).close();
        verifyNoMoreInteractions(serialPortHandler);
    }

    @Test
    public void testGetEID() throws Exception {
        when(serialPortHandler.sendCommand(eq("AT+NSESIM=GETEID")))
                .thenReturn(completedFuture("some output before  \r\n  \r\n+GETEID:1234567890ABCDEF\r\n  \r\nother output afterwards"));

        HuaweiNusimSimAdapter subject = new HuaweiNusimSimAdapter(adapterConfig, serialPortHandlerSupplier);
        String eid = subject.getEID();

        assertEquals("1234567890ABCDEF", eid);

        verify(serialPortHandler, times(1)).sendCommand("AT+NSESIM=GETEID");
    }

    @Test
    public void testGetEIDWithOneRetry() throws Exception {
        when(serialPortHandler.sendCommand(eq("AT+NSESIM=GETEID")))
                .thenReturn(completedFutureWithException(new RuntimeException("boo")))
                .thenReturn(completedFuture("+GETEID:1234567890ABCDEF"));

        HuaweiNusimSimAdapter subject = new HuaweiNusimSimAdapter(adapterConfig, serialPortHandlerSupplier);
        String eid = subject.getEID();

        assertEquals("1234567890ABCDEF", eid);

        verify(serialPortHandler, times(2)).sendCommand("AT+NSESIM=GETEID");
    }

    @Test
    public void testGetEIDErroneousOutput() {
        when(serialPortHandler.sendCommand(eq("AT+NSESIM=GETEID")))
                .thenReturn(completedFuture("some weird output before  \r\n  \r\n ... but no +GETEID answer\r\n"));

        HuaweiNusimSimAdapter subject = new HuaweiNusimSimAdapter(adapterConfig, serialPortHandlerSupplier);

        try {
            subject.getEID();
            fail("expected NoEidAvailableException");
        } catch (NoEidAvailableException ex) {
            verify(serialPortHandler, times(commandRetries)).sendCommand("AT+NSESIM=GETEID");
        }
    }

    @Test
    public void testGetEIDException() {
        when(serialPortHandler.sendCommand(eq("AT+NSESIM=GETEID")))
                .thenReturn(completedFutureWithException(new SerialPortErroneousOutputException("some error output")));

        HuaweiNusimSimAdapter subject = new HuaweiNusimSimAdapter(adapterConfig, serialPortHandlerSupplier);

        try {
            subject.getEID();
            fail("expected NoEidAvailableException");
        } catch (NoEidAvailableException ex) {
            verify(serialPortHandler, times(commandRetries)).sendCommand("AT+NSESIM=GETEID");
        }
    }

    @Test
    public void testGetNusimCapabilities() {
        when(serialPortHandler.sendCommand(eq("AT+NSESIM=GETCAPABILITIES")))
                .thenReturn(completedFuture("some output before  \r\n  \r\n+GETCAPABILITIES:09,03\r\n  \r\nother output afterwards"));

        HuaweiNusimSimAdapter subject = new HuaweiNusimSimAdapter(adapterConfig, serialPortHandlerSupplier);
        String nusimCapabilities = subject.getNusimCapabilities();

        assertEquals("03", nusimCapabilities);

        verify(serialPortHandler, times(1)).sendCommand("AT+NSESIM=GETCAPABILITIES");
    }

    @Test
    public void testGetCertNusim() {
        // test vectors taken from Eagle_Provisioning_Spec_V12.pdf, page 71, chapter A.4.2 "CERT.EAGLE.ECDSA"
        when(serialPortHandler.sendCommand(eq("AT+NSESIM=GETCERTIFICATE")))
                .thenReturn(completedFuture("some output before  \r\n  \r\n+GETCERTIFICATE:" +
                        "30820206308201aca003020102021012345678000000001234567811223344300a06" +
                        "082a8648ce3d0403023038310b30090603550406130244453111300f060355040a0c" +
                        "084561676c6520434d3116301406035504030c0d4561676c6520434d205465737430" +
                        "1e170d3138303230373132343634385a170d3238303230353132343634385a306031" +
                        "0b300906035504061302444531133011060355040a0c0a546573745f4561676c6531" +
                        "29302706035504051320313233343536373830303030303030303132333435363738" +
                        "31313232333334343111300f06035504030c084561676c6553494d305a301406072a" +
                        "8648ce3d020106092b24030302080101070342000452e0384954ed5a69802561c887" +
                        "1c59bc1e58e7878d4367de1ac7a277ce09d9763d46042c879f18e2e0a9130d089fce" +
                        "8c24edc5e8512d53c7dd721aa256e83f38a36f306d301f0603551d23041830168014" +
                        "c8650a03d7c197ce125ac86e72b0c977ca277d9e301d0603551d0e041604143721f9" +
                        "12551976af97f0ab1a04be87dba6252702300e0603551d0f0101ff04040302078030" +
                        "1b0603551d200101ff0411300f300d060b2b06010401bd470d280103300a06082a86" +
                        "48ce3d0403020348003045022100a8127d6b93015b9fac7c708b732d449d0dfe6a0e" +
                        "80533d6a885e9242dd62f4eb02206e95eac30c7e9c398118f279dcd42db1aec5977f" +
                        "613911a74cc9058b0d235a9c\r\n  \r\nother output afterwards"));

        HuaweiNusimSimAdapter subject = new HuaweiNusimSimAdapter(adapterConfig, serialPortHandlerSupplier);
        String certNusim = subject.getCertNusim();

        assertEquals("-----BEGIN CERTIFICATE-----\n" +
                "MIICBjCCAaygAwIBAgIQEjRWeAAAAAASNFZ4ESIzRDAKBggqhkjOPQQDAjA4MQswCQY\n" +
                "DVQQGEwJERTERMA8GA1UECgwIRWFnbGUgQ00xFjAUBgNVBAMMDUVhZ2xlIENNIFRlc3\n" +
                "QwHhcNMTgwMjA3MTI0NjQ4WhcNMjgwMjA1MTI0NjQ4WjBgMQswCQYDVQQGEwJERTETM\n" +
                "BEGA1UECgwKVGVzdF9FYWdsZTEpMCcGA1UEBRMgMTIzNDU2NzgwMDAwMDAwMDEyMzQ1\n" +
                "Njc4MTEyMjMzNDQxETAPBgNVBAMMCEVhZ2xlU0lNMFowFAYHKoZIzj0CAQYJKyQDAwI\n" +
                "IAQEHA0IABFLgOElU7VppgCVhyIccWbweWOeHjUNn3hrHonfOCdl2PUYELIefGOLgqR\n" +
                "MNCJ/OjCTtxehRLVPH3XIaolboPzijbzBtMB8GA1UdIwQYMBaAFMhlCgPXwZfOElrIb\n" +
                "nKwyXfKJ32eMB0GA1UdDgQWBBQ3IfkSVRl2r5fwqxoEvofbpiUnAjAOBgNVHQ8BAf8E\n" +
                "BAMCB4AwGwYDVR0gAQH/BBEwDzANBgsrBgEEAb1HDSgBAzAKBggqhkjOPQQDAgNIADB\n" +
                "FAiEAqBJ9a5MBW5+sfHCLcy1EnQ3+ag6AUz1qiF6SQt1i9OsCIG6V6sMMfpw5gRjyed\n" +
                "zULbGuxZd/YTkRp0zJBYsNI1qc\n" +
                "-----END CERTIFICATE-----\n", certNusim);

        verify(serialPortHandler, times(1)).sendCommand("AT+NSESIM=GETCERTIFICATE");
    }

    @Test
    public void testLoadProfile() throws Exception {
        // test vectors taken from Eagle_Provisioning_Spec_V12.pdf, page 67, chapter A.1 "Keys"
        String mac = "D2B5E42DB1579FC7D215CF172C0D98FA";
        String eKPubDP = "04A47A63C15060F1B524C58082A2956C2139B2FD8688274CD5D756189B84F94E400CD691ED9BE1F0F8A2D206850D08328426FA0C092BCE39B98A50ED0B2ED871DE";
        String sigEKPubDP = "84016A0A2A314C557F7AD5CF35A48D5872F495902494F967643E8BBB46567BFD89165D2AA825A5BBD57A715A4B6332332A42EE8F64A9068B62A1C730598CCF72";
        String kPubDP = "041B7CC46825B5EBF4CBA03D4DBFDC1DDC2EB32F10F72CA52B64F9C58EDFECE3668376C741D31598E38C1C22E873BAD20DA1506BCF9004C305DCBE8782835D58FA";
        String sigKPubDP = "6E13ECF5E7A7C948CE85A38024F19272A84006D3F233931AA3AE7A45457A66108E447B66B4C05287593086CD586C07CB8DD5372BCB640F3D4AC838A87632CA91";
        String kPubCI = "04451669370A39C14AA166048A939C6061DC6066418E885FB71B4D595647E02DA287553A538A081B2CBCE2EEC79043F4DC674371456FBA5D818BE58B35393B3BF4";
        byte[] encP = "profile data".getBytes(StandardCharsets.US_ASCII);

        when(serialPortHandler.sendCommand(startsWith("AT+NSESIM=SETPROFILE"), any(), any())).thenReturn(completedFuture("first \n some \n arbitrary \n output \n then \n+SETPROFILE: 123456789012\n plus \n some \n more \n output\n"));

        HuaweiNusimSimAdapter subject = new HuaweiNusimSimAdapter(adapterConfig, serialPortHandlerSupplier);
        String iccid = subject.loadProfile("eid gets ignored",
                mac,
                BaseEncoding.base64().encode(encP),
                BaseEncoding.base64().encode(BaseEncoding.base16().upperCase().decode(eKPubDP)),
                BaseEncoding.base64().encode(BaseEncoding.base16().upperCase().decode(sigEKPubDP)),
                BaseEncoding.base64().encode(BaseEncoding.base16().upperCase().decode(kPubDP)),
                BaseEncoding.base64().encode(BaseEncoding.base16().upperCase().decode(sigKPubDP)),
                BaseEncoding.base64().encode(BaseEncoding.base16().upperCase().decode(kPubCI)));

        assertEquals("123456789012", iccid);

        int expectedLength = 65 /*eKPubDP*/ + 16 /*mac*/ + 64 /*sigEKPubDP*/ + 65 /*kPubDP*/ + 64 /*sigKPubDP*/ + 65 /*kPubCI*/ + encP.length;
        String expectedSetProfileCommand = "AT+NSESIM=SETPROFILE," + expectedLength + "," + eKPubDP + mac + sigEKPubDP + kPubDP + sigKPubDP + kPubCI + BaseEncoding.base16().upperCase().encode(encP);
        verify(serialPortHandler, times(1)).sendCommand(eq(expectedSetProfileCommand), any(), any());
    }

    @Test
    public void testLoadProfileNoDPAuth() throws Exception {
        // test vectors taken from Eagle_Provisioning_Spec_V12.pdf, page 67, chapter A.1 "Keys"
        String mac = "D2B5E42DB1579FC7D215CF172C0D98FA";
        String eKPubDP = "04A47A63C15060F1B524C58082A2956C2139B2FD8688274CD5D756189B84F94E400CD691ED9BE1F0F8A2D206850D08328426FA0C092BCE39B98A50ED0B2ED871DE";
        byte[] encP = "profile data".getBytes(StandardCharsets.US_ASCII);

        when(serialPortHandler.sendCommand(startsWith("AT+NSESIM=SETPROFILE"), any(), any())).thenReturn(completedFuture("first \n some \n arbitrary \n output \n then \n+SETPROFILE: 12345678901234\n plus \n some \n more \n output\n"));

        HuaweiNusimSimAdapter subject = new HuaweiNusimSimAdapter(adapterConfig, serialPortHandlerSupplier);
        String iccid = subject.loadProfile("eid gets ignored",
                mac,
                BaseEncoding.base64().encode(encP),
                BaseEncoding.base64().encode(BaseEncoding.base16().upperCase().decode(eKPubDP)),
                null,
                null,
                null,
                null);

        assertEquals("12345678901234", iccid);

        int expectedLength = 65 /*eKPubDP*/ + 16 /*mac*/ + 64 /*sigEKPubDP*/ + 65 /*kPubDP*/ + 64 /*sigKPubDP*/ + 65 /*kPubCI*/ + encP.length;
        String nullsForEmptyDP = Strings.repeat("00", 64 + 65 + 64 + 65);
        String expectedSetProfileCommand = "AT+NSESIM=SETPROFILE," + expectedLength + "," + eKPubDP + mac + nullsForEmptyDP + BaseEncoding.base16().upperCase().encode(encP);
        verify(serialPortHandler, times(1)).sendCommand(eq(expectedSetProfileCommand), any(), any());
    }

    @Test
    public void testLoadProfileError() {
        // test vectors taken from Eagle_Provisioning_Spec_V12.pdf, page 67, chapter A.1 "Keys"
        String mac = "D2B5E42DB1579FC7D215CF172C0D98FA";
        String eKPubDP = "04A47A63C15060F1B524C58082A2956C2139B2FD8688274CD5D756189B84F94E400CD691ED9BE1F0F8A2D206850D08328426FA0C092BCE39B98A50ED0B2ED871DE";
        byte[] encP = "profile data".getBytes(StandardCharsets.US_ASCII);

        when(serialPortHandler.sendCommand(startsWith("AT+NSESIM=SETPROFILE"), any(), any())).thenReturn(completedFutureWithException(new SerialPortErroneousOutputException("first \n some \n arbitrary \n output \n then an error: \n+SETPROFILE: 541 \n plus \n some \n more \n output\n")));

        HuaweiNusimSimAdapter subject = new HuaweiNusimSimAdapter(adapterConfig, serialPortHandlerSupplier);
        try {
            subject.loadProfile("eid gets ignored",
                    mac,
                    BaseEncoding.base64().encode(encP),
                    BaseEncoding.base64().encode(BaseEncoding.base16().upperCase().decode(eKPubDP)),
                    null,
                    null,
                    null,
                    null);
        } catch (ProfileLoadingException ex) {
            assertEquals("error 541: invalid KpubCI (does not match stored KpubCI)", ex.getMessage());
        }

        int expectedLength = 65 /*eKPubDP*/ + 16 /*mac*/ + 64 /*sigEKPubDP*/ + 65 /*kPubDP*/ + 64 /*sigKPubDP*/ + 65 /*kPubCI*/ + encP.length;
        String nullsForEmptyDP = Strings.repeat("00", 64 + 65 + 64 + 65);
        String expectedSetProfileCommand = "AT+NSESIM=SETPROFILE," + expectedLength + "," + eKPubDP + mac + nullsForEmptyDP + BaseEncoding.base16().upperCase().encode(encP);
        verify(serialPortHandler, times(commandRetries)).sendCommand(eq(expectedSetProfileCommand), any(), any());
    }

    private static <T> CompletableFuture<T> completedFutureWithException(Throwable ex) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(ex);
        return completableFuture;
    }
}
