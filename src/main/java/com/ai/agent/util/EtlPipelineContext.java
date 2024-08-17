package com.ai.agent.util;

import com.ai.agent.service.EtlPipelineService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
public class EtlPipelineContext {

    private EtlPipelineService etlPipelineService;

    public List<Document> getDocsList (String resourceUrl) {
        return this.etlPipelineService.getDocsList(resourceUrl);
    }

}
