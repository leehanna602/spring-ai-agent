package com.ai.agent.util;

import com.ai.agent.dto.AiChatRequest;
import com.ai.agent.service.OpenAiChatService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class OpenAiChatClientContext {

    private OpenAiChatService openAiChatService;

    public String streamAiChatClient(String uuid, AiChatRequest aiChatRequest) {
        return this.openAiChatService.streamAiChatClient(uuid, aiChatRequest);
    }

}
