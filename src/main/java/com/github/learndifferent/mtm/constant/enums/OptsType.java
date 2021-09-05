package com.github.learndifferent.mtm.constant.enums;

/**
 * 日志相关操作
 *
 * @author zhou
 */
public enum OptsType {
    /**
     * 创建
     */
    CREATE(0),
    /**
     * 读
     */
    READ(1),
    /**
     * 更新
     */
    UPDATE(2),
    /**
     * 删除
     */
    DELETE(3),
    /**
     * 其他
     */
    OTHER(4);


    private final Integer value;

    private OptsType(final Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
