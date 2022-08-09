package com.catizard.glass.utils;

import io.netty.util.concurrent.Promise;

import java.util.HashMap;
import java.util.Map;

public class RequestPromiseChannel {
    public static Map<RequestIdentify, Promise<Object>> waitCh = new HashMap<>();
}
