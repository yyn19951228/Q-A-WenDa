package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {
    // a collection of object that need to be rendered on the web
    private Map<String, Object> objs = new HashMap<>();

    public void set(String key, Object obj) {
        objs.put(key, obj);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
