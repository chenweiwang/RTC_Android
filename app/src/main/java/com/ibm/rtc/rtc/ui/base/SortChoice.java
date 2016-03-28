package com.ibm.rtc.rtc.ui.base;

/**
 * Created by v-wajie on 1/8/2016.
 */
public enum SortChoice {
    ByIdInc("By Id Increase"),
    ByIdDesc("By Id Decrease"),
    ByName("By Title"),
    ByCreatedTime("By Created Time"),
    ByLastModefiedTime("By Last Modified Time"),
    Unhandled("");

    private String name;
    private SortChoice(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
