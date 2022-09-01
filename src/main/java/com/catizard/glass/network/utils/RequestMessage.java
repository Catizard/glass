package com.catizard.glass.network.utils;

import com.catizard.glass.utils.RequestIdentify;

public class RequestMessage<T> {
    private RequestIdentify id;
    public T requestParam;
}
