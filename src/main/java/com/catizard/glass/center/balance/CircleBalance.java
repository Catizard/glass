package com.catizard.glass.center.balance;

import java.util.List;

public class CircleBalance implements Balance {
    @Override
    public String processBeforeProvide(String data) {
        return null;
    }

    @Override
    public void processAfterProvide() {

    }

    @Override
    public void processBeforeFetch() {

    }

    @Override
    public String processAfterFetch(List<String> list) {
        return null;
    }
}
