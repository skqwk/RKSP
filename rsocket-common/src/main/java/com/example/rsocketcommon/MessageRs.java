package com.example.rsocketcommon;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record MessageRs(String author,
                        String body,
                        String createdAt) implements Serializable {
}
