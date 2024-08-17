package com.ai.agent.config;

import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtlPipelineConfig {

    @Bean
    TextSplitter textSplitter() {
        return new TokenTextSplitter();
    }

}
