package com.ai.agent.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface EtlPipelineService {

    List<Document> getDocsList(String resourceUrl);

}
