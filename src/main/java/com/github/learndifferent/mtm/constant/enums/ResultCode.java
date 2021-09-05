package com.github.learndifferent.mtm.constant.enums;

/**
 * 响应码
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum ResultCode {

    /**
     * Success
     */
    SUCCESS(200, "Success"),
    /**
     * Fail
     */
    FAILED(500, "Fail"),
    /**
     * Fail to save
     */
    SAVE_FAILED(2001, "Fail to save"),
    /**
     * Fail to update
     */
    UPDATE_FAILED(2002, "Fail to update"),
    /**
     * Fail to DELETE
     */
    DELETE_FAILED(2003, "Fail to DELETE"),
    /**
     * This username is already taken
     */
    USER_ALREADY_EXIST(2004, "This username is already taken"),
    /**
     * Please Login
     */
    NOT_LOGIN(2005, "Please Login"),
    /**
     * The username or password is incorrect
     */
    USER_LOGIN_FAIL(2006, "The username or password is incorrect"),
    /**
     * The verification code you entered is incorrect.
     */
    VERIFICATION_CODE_FAILED(2007, "The verification code you entered is incorrect."),
    /**
     * The verification code you entered is incorrect.
     */
    INVITATION_CODE_FAILED(2008, "The invitation code you entered is incorrect."),
    /**
     * Don't have permission to do it.
     */
    PERMISSION_DENIED(2009, "You don't have permission to do it."),
    /**
     * already marked it
     */
    ALREADY_MARKED(2010, "You have already MARKED it. Can't do it twice."),
    /**
     * enter a valid URL
     */
    URL_MALFORMED(2011, "Please enter a valid URL"),
    /**
     * Access denied for some reasons
     */
    URL_ACCESS_DENIED(2012, "Access denied for some reasons. Please try another link."),
    /**
     * no results
     */
    NO_RESULTS_FOUND(2013, "There are no results that match your search"),
    /**
     * Deleted
     */
    DELETE_SUCCESS(3001, "Deleted Successfully"),
    /**
     * Incorrect Password
     */
    PASSWORD_INCORRECT(3002, "Incorrect Password"),
    /**
     * Unable to connect to the server
     */
    CONNECTION_ERROR(5001, "Unable to connect to the server. Please try again later."),
    /**
     * Fail to validate
     */
    VALIDATE_FAILED(501, "Fail to validate"),
    /**
     * Unknown Error
     */
    ERROR(502, "Unknown Error");


    private final Integer value;

    private final String msg;

    private ResultCode(final Integer value, final String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer value() {
        return value;
    }

    public String msg() {
        return msg;
    }
}
