package com.catizard.glass.center.balance;

import java.util.List;

public interface Balance {
    String processBeforeProvide(String data);
    void processAfterProvide();
    void processBeforeFetch();
    String processAfterFetch(List<String> list);
}
