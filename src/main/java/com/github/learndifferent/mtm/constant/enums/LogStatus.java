package com.github.learndifferent.mtm.constant.enums;

public enum LogStatus {

    /**
     * 正常
     */
    NORMAL("Normal"),
    /**
     * 错误
     */
    ERROR("Error");

    private final String status;

    private LogStatus(final String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }
}
