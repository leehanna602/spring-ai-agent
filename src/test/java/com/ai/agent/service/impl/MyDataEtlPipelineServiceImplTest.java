package com.ai.agent.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyDataEtlPipelineServiceImplTest {

    @Autowired
    private MyDataEtlPipelineServiceImpl myDataEtlPipelineService;

    @Test
    @DisplayName("pdf 파일 텍스트 추출 및 정제 - (221115 수정배포) (2022.10) 금융분야 마이데이터 기술 가이드라인.pdf")
    void getDocsList_success_resourceUrlTechGuide() throws Exception {
        // given
        String resourceUrlTechGuide = "classpath:rag/(221115 수정배포) (2022.10) 금융분야 마이데이터 기술 가이드라인.pdf";

        // when
        List<Document> docList = myDataEtlPipelineService.getDocsList(resourceUrlTechGuide);

        // then
        assertThat(docList).isNotNull();
    }

    @Test
    @DisplayName("pdf 파일 텍스트 추출 및 정제 - (수정게시) 금융분야 마이데이터 표준 API 규격 v1.pdf")
    void getDocsList_success_resourceUrlApiGuide() throws Exception {
        // given
        String resourceUrlApiGuide = "classpath:rag/(수정게시) 금융분야 마이데이터 표준 API 규격 v1.pdf";

        // when
        List<Document> docList = myDataEtlPipelineService.getDocsList(resourceUrlApiGuide);

        // then
        assertThat(docList).isNotNull();
    }

}