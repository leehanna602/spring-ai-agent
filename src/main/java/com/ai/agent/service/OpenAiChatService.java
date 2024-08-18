package com.ai.agent.service;

import com.ai.agent.dto.AiChatRequest;

public interface OpenAiChatService {

    String streamAiChatClient(String uuid, AiChatRequest aiChatRequest);

}
