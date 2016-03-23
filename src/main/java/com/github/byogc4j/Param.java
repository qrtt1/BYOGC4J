package com.github.byogc4j;

import java.util.Map;

import com.google.api.client.util.Maps;

public class Param {

    Map<String, Object> map = Maps.newHashMap();

    public Param(String name, Object value) {
        map.put(name, value);
    }

    public static Param create(String name, Object value) {
        return new Param(name, value);
    }

    public Param add(String name, Object value) {
        map.put(name, value);
        return this;
    }

    public Map<String, Object> getMap() {
        return map;
    }

}
