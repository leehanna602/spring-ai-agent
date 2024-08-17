package com.ai.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MyDataRequestClassifyResponse(@JsonProperty(required = true, value = "result") MyDataDocType result) {
}
