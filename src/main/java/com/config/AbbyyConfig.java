package com.config;

import com.service.AbbyyService;
import com.service.AbbyyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AbbyyProperties.class)
@ConditionalOnClass(value = {AbbyyService.class})
public class AbbyyConfig {

    @Autowired
    private AbbyyProperties abbyyProperties;

    @Bean
    @ConditionalOnMissingBean
    public AbbyyService getAbbyyService() {
        return new AbbyyServiceImpl(abbyyProperties);
    }

}
