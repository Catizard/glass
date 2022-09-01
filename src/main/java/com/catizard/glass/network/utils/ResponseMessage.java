package com.catizard.glass.network.utils;

import com.catizard.glass.utils.RequestIdentify;

public class ResponseMessage<T> {
    private RequestIdentify id;
    public T responseResult;
    public Exception Err;
}
