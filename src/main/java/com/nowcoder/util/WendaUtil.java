package com.nowcoder.util;

import com.alibaba.fastjson.JSONObject;

public class WendaUtil {

    public static int ANONYMOUS_USERID = 3;

    public static String getJSONString(int code) {
        JSONObject json = new JSONObject();
        // like map
        json.put("code", code);
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        JSONObject json = new JSONObject();
        // like map
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }
}
