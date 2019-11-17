package com.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "abbyylib", ignoreUnknownFields = false)
public class AbbyyProperties {
    private String abbyyServerUrl;// = "http://cloud-eu.ocrsdk.com";
    private String abbyyApplicationId;
    private String abbyyPassword;

    public String getAbbyyApplicationId() {
        return abbyyApplicationId;
    }

    public void setAbbyyApplicationId(String abbyyApplicationId) {
        this.abbyyApplicationId = abbyyApplicationId;
    }

    public String getAbbyyPassword() {
        return abbyyPassword;
    }

    public void setAbbyyPassword(String abbyyPassword) {
        this.abbyyPassword = abbyyPassword;
    }

    public String getAbbyyServerUrl() {
        return abbyyServerUrl;
    }

    public void setAbbyyServerUrl(String abbyyServerUrl) {
        this.abbyyServerUrl = abbyyServerUrl;
    }
}
