package com.ai.agent.util;

import com.ai.agent.dto.AiChatRequest;
import com.ai.agent.service.OpenAiChatService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Setter
@Getter
@Component
public class OpenAiChatClientContext {

    private OpenAiChatService openAiChatService;

    public Flux<String>  streamChatClient(String uuid, AiChatRequest aiChatRequest) {
        return this.openAiChatService.streamChatClient(uuid, aiChatRequest);
    }

}
