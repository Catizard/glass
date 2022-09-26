package com.catizard.glass.center.utils.wrappers;

public interface ServiceNameFactory {
    String ImplementationNameToServiceName(String rawName);
    String InterfaceNameToServiceName(String rawName);
}
