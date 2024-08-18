package com.ai.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public enum MyDataDocType {
    MY_DATA_GUIDE("(221115 수정배포) (2022.10) 금융분야 마이데이터 기술 가이드라인.pdf"),
    MY_DATA_API("(수정게시) 금융분야 마이데이터 표준 API 규격 v1.pdf"),
    UNCERTAIN(null),
    COMMON(null);

    private String fileName;
}
