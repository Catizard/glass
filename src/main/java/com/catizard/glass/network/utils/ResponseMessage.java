package com.catizard.glass.network.utils;

import com.catizard.glass.utils.RequestIdentify;

import java.util.HashMap;
import java.util.Map;

public class ResponseMessage {
    public int requestId;
    public Object responseResult;
    public Exception Err;

    public ResponseMessage(int requestId, Object responseResult, Exception err) {
        this.requestId = requestId;
        this.responseResult = responseResult;
        Err = err;
    }

    public static Map<Integer, Class<?>> responseResultType = new HashMap<>();
    static {
        
    }
}
