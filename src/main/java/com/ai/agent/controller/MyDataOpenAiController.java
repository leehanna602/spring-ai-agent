package com.ai.agent.controller;

import com.ai.agent.dto.AiChatRequest;
import com.ai.agent.service.DocumentResourceReader;
import com.ai.agent.service.impl.MyDataEtlPipelineServiceImpl;
import com.ai.agent.util.EtlPipelineContext;
import com.ai.agent.util.OpenAiChatClientContext;
import com.ai.agent.service.impl.MyDataOpenAiChatServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MyDataOpenAiController {
    private final OpenAiChatClientContext openAiChatClientContext;
    private final EtlPipelineContext etlPipelineContext;
    private final ChatClient chatClient;
    private final MilvusVectorStore vectorStore;
    private final TextSplitter textSplitter;
    private final DocumentResourceReader documentResourceReader;

    /* ChatClient */
    @PostMapping(value = "/ai/chat/stream/my-data", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> completion(HttpSession session, @RequestBody AiChatRequest aiChatRequest) {
        openAiChatClientContext.setOpenAiChatService(new MyDataOpenAiChatServiceImpl(chatClient, vectorStore));
        String uuid = (String) session.getAttribute("uuid");
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            session.setAttribute("uuid", uuid);
        }
        return openAiChatClientContext.streamChatClient(uuid, aiChatRequest);
    }

    /* Embedding - Milvus */
    @PostMapping("/ai/embedding/my-data")
    public Object embedMyData() {
        etlPipelineContext.setEtlPipelineService(new MyDataEtlPipelineServiceImpl(textSplitter, documentResourceReader));

        try {
            String resourceUrlTechGuide = "classpath:rag/(221115 수정배포) (2022.10) 금융분야 마이데이터 기술 가이드라인.pdf";
            List<Document> documents = etlPipelineContext.getDocsList(resourceUrlTechGuide);

            log.info("[resourceUrlTechGuide] documents size : {}", documents.size());

            for (Document document : documents) {
                log.debug("[resourceUrlTechGuide] document: {}", document);
            }

            log.info("[resourceUrlTechGuide] start vector store add");
            vectorStore.accept(documents);
            log.info("[resourceUrlTechGuide] end vector store add");

            String resourceUrlApiGuide = "classpath:rag/(수정게시) 금융분야 마이데이터 표준 API 규격 v1.pdf";
            documents = etlPipelineContext.getDocsList(resourceUrlApiGuide);

            log.info("[resourceUrlApiGuide] documents size : {}", documents.size());

            for (Document document : documents) {
                log.debug("[resourceUrlApiGuide] document: {}", document);
            }

            log.info("[resourceUrlApiGuide] start vector store add");
            vectorStore.accept(documents);
            log.info("[resourceUrlApiGuide] end vector store add");

        } catch (Exception e) {
            log.error("embedMyData error : {}", e.getMessage());
            return "failed";
        }

        return "success";
    }

}
