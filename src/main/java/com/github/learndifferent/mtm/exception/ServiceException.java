package com.github.learndifferent.mtm.exception;

import com.github.learndifferent.mtm.constant.enums.ResultCode;

/**
 * 可以根据 ResultCode 自定义的服务错误
 *
 * @author zhou
 */
public class ServiceException extends BaseException {

    private ResultCode resultCode;

    public ServiceException(final String message) {
        super(message);
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ServiceException(final ResultCode resultCode, final String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public ServiceException(final ResultCode resultCode) {
        super(resultCode.msg());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return this.resultCode;
    }
}
