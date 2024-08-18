package com.ai.agent.service.impl;

import com.ai.agent.dto.AiChatRequest;
import com.ai.agent.dto.MyDataDocType;
import com.ai.agent.dto.MyDataRequestClassifyResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyDataOpenAiChatServiceImplTest {

    @Autowired
    private MyDataOpenAiChatServiceImpl myDataOpenAiChatService;

    @Test
    @DisplayName("마이데이터 스트림 멀티턴 채팅")
    void streamChatClient_success_chat() {
        // given
        String uuid = UUID.randomUUID().toString();
        AiChatRequest aiChatRequestFirst = new AiChatRequest("API 스펙 중 aNS는 어떤 것 뜻하나요?");AiChatRequest aiChatRequestSecond = new AiChatRequest("예시를 알려주세요.");

        // when
        Flux<String> stringFluxFirst = myDataOpenAiChatService.streamChatClient(uuid, aiChatRequestFirst);
        String contentFirst = stringFluxFirst.collectList().block().stream().collect(Collectors.joining());
        Flux<String> stringFluxSecond = myDataOpenAiChatService.streamChatClient(uuid, aiChatRequestSecond);
        String contentSecond = stringFluxSecond.collectList().block().stream().collect(Collectors.joining());

        // then
        assertThat(contentFirst).isNotNull();
        assertThat(contentSecond).isNotNull();
    }

    @Test
    @DisplayName("마이데이터 사용자 의도 분류 - MY_DATA_GUIDE")
    void myDataRequestClassifyResponse_success_MY_DATA_GUIDE() {
        // given
        AiChatRequest aiChatRequest = new AiChatRequest("정보 전송 요구 연장은 언제 가능한가요?");

        // when
        MyDataRequestClassifyResponse myDataRequestClassifyResponse = myDataOpenAiChatService.myDataRequestClassifyResponse(aiChatRequest);

        // then
        assertThat(myDataRequestClassifyResponse.result()).isEqualTo(MyDataDocType.MY_DATA_GUIDE);
    }

    @Test
    @DisplayName("마이데이터 사용자 의도 분류 - MY_DATA_API")
    void myDataRequestClassifyResponse_success_MY_DATA_API() {
        // given
        AiChatRequest aiChatRequest = new AiChatRequest("API 스펙 중 aNS는 어떤 것 뜻하나요?");

        // when
        MyDataRequestClassifyResponse myDataRequestClassifyResponse = myDataOpenAiChatService.myDataRequestClassifyResponse(aiChatRequest);

        // then
        assertThat(myDataRequestClassifyResponse.result()).isEqualTo(MyDataDocType.MY_DATA_API);
    }

    @Test
    @DisplayName("마이데이터 사용자 의도 분류 - COMMON")
    void myDataRequestClassifyResponse_success_COMMON() {
        // given
        AiChatRequest aiChatRequest = new AiChatRequest("안녕");

        // when
        MyDataRequestClassifyResponse myDataRequestClassifyResponse = myDataOpenAiChatService.myDataRequestClassifyResponse(aiChatRequest);

        // then
        assertThat(myDataRequestClassifyResponse.result()).isEqualTo(MyDataDocType.COMMON);
    }

    @Test
    @DisplayName("마이데이터 사용자 의도 분류 - UNCERTAIN")
    void myDataRequestClassifyResponse_success_UNCERTAIN() {
        // given
        AiChatRequest aiChatRequest = new AiChatRequest("보안과 api 에 대해 알려줄래?");

        // when
        MyDataRequestClassifyResponse myDataRequestClassifyResponse = myDataOpenAiChatService.myDataRequestClassifyResponse(aiChatRequest);

        // then
        System.out.println(myDataRequestClassifyResponse.result().toString());
        assertThat(myDataRequestClassifyResponse.result()).isEqualTo(MyDataDocType.UNCERTAIN);
    }

    @Test
    @DisplayName("마이데이터 사용자 의도에 따른 검색")
    void searchDocs_success_MY_DATA_GUIDE() {

    }



}