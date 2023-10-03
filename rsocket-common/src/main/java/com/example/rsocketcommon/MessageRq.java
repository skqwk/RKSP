package com.example.rsocketcommon;

import java.io.Serializable;

public record MessageRq(String author,
                        String body,
                        String channel) implements Serializable {
}
