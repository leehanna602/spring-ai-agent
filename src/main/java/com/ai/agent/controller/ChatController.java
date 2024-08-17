package com.ai.agent.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class ChatController {

    @RequestMapping("/chat")
    public String chat() {
        return "chat";
    }

}
