package com.pawmot.mtom.test.b64.sender;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Component
@AllArgsConstructor
@Slf4j
public class FileUploader implements CommandLineRunner {

    private final Base64Encoder encoder;
    private final XmlRequester requester;

    @Override
    public void run(String... args) throws Exception {
        int n = Integer.parseInt(args[1]);
        val path = Paths.get(args[0]);
        val size = Files.size(path);
        val is = Files.newInputStream(path);
        val b64 = encoder.encode(is, (int) size);
        List<Long> list = newArrayList();
        for (int i = 0; i < n; i++) {
            list.add(requester.send(b64, path.getFileName().toString()));
        }

        val sum = list.stream().reduce(0L, (l1, l2) -> l1 + l2);
        val mean = ((double) sum)/n;
        val min = list.stream().min(Long::compareTo).get();
        val max = list.stream().max(Long::compareTo).get();

        log.info("Performed {} requests.", n);
        log.info("Mean time: {}; Min time: {}; Max time: {}", mean, min, max);
    }
}
