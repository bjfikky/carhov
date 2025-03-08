package com.benorim.carhov.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
public class Version {

    @Value("${spring.application.version}")
    private String projectVersion;

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("/version")
    public Map<String, String> getProjectVersion() {
        Map<String, String> versionInfo = new HashMap<>();
        versionInfo.put("application", applicationName);
        versionInfo.put("version", projectVersion);
        log.info("versionInfo: {}", versionInfo);
        return versionInfo;
    }
}
