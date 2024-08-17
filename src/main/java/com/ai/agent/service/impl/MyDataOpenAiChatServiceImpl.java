package com.ai.agent.service.impl;

import com.ai.agent.dto.AiChatRequest;
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
    private static final double thresHold = 0.0;
    private static final String systemPrompt = "당신은 마이데이터 공식 문서를 기반으로 정책과 기술 사양 등을 답변해주는 에이전트로서 사용자의 질문에 답해야합니다. 만약, 주어진 정보를 기반으로 답변할 수 없다면, 사용자에게 주어진 질문에 답변할 수 없다고 해야합니다. \n다음 주어진 문서와 대화이력을 참고해 대답하세요. \n#문서#\n{docs} \n#대화이력#\n{convLog}";

    @Override
    public Flux<String> streamChatClient(String uuid, AiChatRequest aiChatRequest) {
        if (!chatHistoryLog.containsKey(uuid)) {
            chatHistoryLog.put(uuid, new ArrayList<>());
        }

        log.info("chatHistoryLog : {}", chatHistoryLog);
        String history;
        List<Message> messages = chatHistoryLog.get(uuid);
        if (messages == null || messages.isEmpty()) {
            history = "";
        } else {
            history = messages.stream()
                    .map(m -> m.getMessageType().name() + ": " + m.getContent() + "\n")
                    .collect(Collectors.joining(System.lineSeparator()));
        }

        List<Document> docs = this.searchDocs(aiChatRequest);
        log.info("user message: {}, docs: {}", aiChatRequest.message(), docs);
        List<String> contentList = docs.stream()
                .map(Document::getContent)
                .toList();

        String[] fullContent = {""};
        return chatClient.prompt()
                .system(system -> system.text(systemPrompt)
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

    private List<Document> searchDocs(AiChatRequest aiChatRequest) {
        return vectorStore.similaritySearch(
                SearchRequest.query(aiChatRequest.message())
                        .withTopK(tokK)
                        .withSimilarityThreshold(thresHold)
        );
    }

}
