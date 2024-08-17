package com.ai.agent.service.impl;

import com.ai.agent.service.DocumentResourceReader;
import com.ai.agent.service.EtlPipelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class MyDataEtlPipelineServiceImpl implements EtlPipelineService {

    private final TextSplitter textSplitter;
    private final DocumentResourceReader documentResourceReader;

    @Autowired
    public MyDataEtlPipelineServiceImpl(TextSplitter textSplitter, DocumentResourceReader documentResourceReader) {
        this.textSplitter = textSplitter;
        this.documentResourceReader = documentResourceReader;
    }

    @Override
    public List<Document> getDocsList(String resourceUrl) {
        /* Extract */
        List<Document> docsFromTikaDocumentReader = documentResourceReader.getDocsFromTikaDocumentReader(resourceUrl);

        /* Transform */
        List<Document> apply = textSplitter.apply(docsFromTikaDocumentReader);

        return apply;
    }


}
