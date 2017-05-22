package com.pawmot.mtom.test.b64.sender;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Service
public class Base64Encoder {
    @SneakyThrows
    public String encode(InputStream is, int size) {
        byte[] bytes = new byte[size];
        int read = is.read(bytes, 0, size);
        if (read != size) {
            throw new IOException("Read bytes does not match size");
        }
        return new String(Base64.getEncoder().encode(bytes), "US-ASCII");
    }
}
