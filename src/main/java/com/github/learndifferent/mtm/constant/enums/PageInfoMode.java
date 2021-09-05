package com.github.learndifferent.mtm.constant.enums;

/**
 * 传入的值 num，表示 Current Page 或者 from
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum PageInfoMode {

    /**
     * 传入的值 num，表示 Current Page
     */
    CURRENT_PAGE_MODE(1),
    /**
     * 传入的值 num，表示 from
     */
    FROM_MODE(2);

    private final int value;

    private PageInfoMode(final int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
