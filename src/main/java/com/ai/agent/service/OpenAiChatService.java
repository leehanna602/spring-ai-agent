package com.ai.agent.service;

import com.ai.agent.dto.AiChatRequest;
import reactor.core.publisher.Flux;

public interface OpenAiChatService {

    Flux<String> streamChatClient(String uuid, AiChatRequest aiChatRequest);

}
