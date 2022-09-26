package com.catizard.glass.center.utils.wrappers;

public class DefaultServiceNameFactory implements ServiceNameFactory {
    @Override
    public String ImplementationNameToServiceName(String rawName) {
        rawName = rawName.substring(0, rawName.indexOf("Impl"));
        rawName = rawName.replace('.', '/');
        return rawName;
    }

    @Override
    public String InterfaceNameToServiceName(String rawName) {
        rawName = rawName.replace('.', '/');
        return rawName;
    }
}
