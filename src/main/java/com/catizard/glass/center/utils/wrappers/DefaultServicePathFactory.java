package com.catizard.glass.center.utils.wrappers;

public class DefaultServicePathFactory implements ServicePathFactory {
    @Override
    public String ServiceNameToServicePath(String serviceName) {
        return "/" + serviceName + "/server";
    }
}
