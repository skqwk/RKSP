package com.example.rsocketclient.cmd;

import java.util.HashMap;
import java.util.Map;

public class Params {
    private static final Params EMPTY = new Params();

    Map<String, Object> params = new HashMap<>();

    private <T> void add(String name, T value) {
        params.put(name, value);
    }

    public <T> T get(String name) {
        return (T) params.get(name);
    }

    public static ParamsBuilder builder() {
        return new ParamsBuilder();
    }

    public static Params empty() {
        return EMPTY;
    }

    public static class ParamsBuilder {
        private Params params = new Params();

        public ParamsBuilder param(String name, Object value) {
            params.add(name, value);
            return this;
        }

        public Params build() {
            return this.params;
        }

    }
}
