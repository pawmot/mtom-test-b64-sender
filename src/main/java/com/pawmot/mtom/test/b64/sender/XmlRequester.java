package com.pawmot.mtom.test.b64.sender;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class XmlRequester {

    @SneakyThrows
    public long send(String b64, String fileName) {
        String url = "http://35.158.73.142:9191/Base64Upload_1_0";
        log.info("Connecting to {}", url);

        val conn = (HttpURLConnection) new URL(url).openConnection();

        val encoded = encodeContent(getContent(b64, fileName));
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setFixedLengthStreamingMode(encoded.length);
        conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
        conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        conn.setRequestProperty("SOAPAction", "\"http://www.pawmot.com/Base64Upload_1_0/base64Upload\"");
        conn.setRequestProperty("Host", "35.158.73.142:9191");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("User-Agent", "Apache-HttpClient/4.1.1 (java 1.5)");
        conn.connect();

        val os = conn.getOutputStream();
        val sw = new StopWatch();
        sw.start();
        os.write(encoded);
        os.flush();

        val status = conn.getResponseCode();
        sw.stop();
        long totalTimeMillis = sw.getTotalTimeMillis();
        conn.disconnect();
        log.info("Got status code {} (took {} ms)", status, totalTimeMillis);
        return totalTimeMillis;
    }

    @SneakyThrows
    private byte[] encodeContent(String content) {
        return content.getBytes("UTF-8");
    }

    private String getContent(String b64, String fileName) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:bas=\"http://www.pawmot.com/Base64Upload_1_0/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <bas:base64UploadRequest>\n" +
                "         <bas:filename>" + fileName + "</bas:filename>\n" +
                "         <bas:content>" + b64 + "</bas:content>" +
                "      </bas:base64UploadRequest>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }
}
