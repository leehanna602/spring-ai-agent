package com.ai.agent.service.impl;

import com.ai.agent.dto.AiChatRequest;
import com.ai.agent.dto.MyDataDocType;
import com.ai.agent.dto.MyDataRequestClassifyResponse;
import com.ai.agent.prompt.MyDataPrompt;
import com.ai.agent.service.OpenAiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MyDataOpenAiChatServiceImpl implements OpenAiChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Autowired
    public MyDataOpenAiChatServiceImpl(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }


    private static final Map<String, List<Message>> chatHistoryLog = new ConcurrentHashMap<>();
    private static final Integer tokK = 4;
    private static final double thresHold = 0.3;


    /**
     * Streaming Chat
     * @param uuid String
     * @param aiChatRequest AiChatRequest
     * @return Flux<String>
     */
    @Override
    public Flux<String> streamChatClient(String uuid, AiChatRequest aiChatRequest) {
        // uuid로 대화 이력 확인
        if (!chatHistoryLog.containsKey(uuid)) {
            chatHistoryLog.put(uuid, new ArrayList<>());
        }
        log.info("chatHistoryLog : {}", chatHistoryLog);

        // 대화 이력 조회
        String history;
        List<Message> messages = chatHistoryLog.get(uuid);
        if (messages == null || messages.isEmpty()) {
            history = "";
        } else {
            history = messages.stream()
                    .map(m -> m.getMessageType().name() + ": " + m.getContent() + "\n")
                    .collect(Collectors.joining(System.lineSeparator()));
        }

        // 사용자 의도 분류
        MyDataRequestClassifyResponse myDataRequestClassifyResponse = myDataRequestClassifyResponse(aiChatRequest);
        log.info("myDataRequestClassifyResponse : {}", myDataRequestClassifyResponse);
        String fileName = myDataRequestClassifyResponse.result().getFileName();

        if (!fileName.equals(MyDataDocType.MY_DATA_GUIDE.getFileName()) && !fileName.equals(MyDataDocType.MY_DATA_API.getFileName())) {
            fileName = null;
        }

        // 문서 검색
        List<Document> docs = this.searchDocs(aiChatRequest, fileName);
        log.info("user message: {}, docs: {}", aiChatRequest.message(), docs);
        List<String> contentList = docs.stream()
                .map(Document::getContent)
                .toList();

        String[] fullContent = {""};
        return chatClient.prompt()
                .system(system -> system
                        .text(MyDataPrompt.systemPrompt)
                        .param("docs", contentList.toString())
                        .param("convLog", history))
                .user(aiChatRequest.message())
                .stream()
                .content()
                .bufferTimeout(10, Duration.ofMillis(100))
                .map(chunks -> String.join("", chunks))
                .doOnNext(contentText -> {
                    log.info("Buffered content: {}", contentText);
                    fullContent[0] = fullContent[0] +  contentText;
                })
                .doOnComplete(() -> {
                    assert messages != null;
                    messages.add(new UserMessage(aiChatRequest.message()));
                    messages.add(new AssistantMessage(fullContent[0]));
                    chatHistoryLog.put(uuid, messages);
                    log.info("uuid: {}, messages: {}", uuid, messages);
                });
    }


    /**
     * 사용자 메시지의 의도 기반 검색할 문서 분류
     * @param aiChatRequest AiChatRequest
     * @return MyDataRequestClassifyResponse
     */
    public MyDataRequestClassifyResponse myDataRequestClassifyResponse(AiChatRequest aiChatRequest) {
        return chatClient.prompt()
                .system(MyDataPrompt.classifySystemPrompt)
                .user(aiChatRequest.message())
                .call()
                .entity(MyDataRequestClassifyResponse.class);
    }


    /**
     * VectorStore 문서 검색
     * @param aiChatRequest AiChatRequest
     * @param fileName String
     * @return List<Document>
     */
    private List<Document> searchDocs(AiChatRequest aiChatRequest, String fileName) {
        if (!fileName.isEmpty()) {
            return vectorStore.similaritySearch(
                    SearchRequest.query(aiChatRequest.message())
                            .withTopK(tokK)
                            .withSimilarityThreshold(thresHold)
                            .withFilterExpression("source == '" + fileName + "'")
            );
        }
        return vectorStore.similaritySearch(
                SearchRequest.query(aiChatRequest.message())
                        .withTopK(tokK)
                        .withSimilarityThreshold(thresHold)
        );

    }

}
