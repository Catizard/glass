package com.catizard.glass.network.utils;

import com.catizard.glass.utils.RequestIdentify;

import java.util.HashMap;
import java.util.Map;

public class RequestMessage {
    private int requestId;
    public Object requestParam;

    public RequestMessage(int requestId, Object requestParam) {
        this.requestId = requestId;
        this.requestParam = requestParam;
    }

    public static Map<Integer, Class<?>> requestParamTypes = new HashMap<>();
    static {
    }
}
