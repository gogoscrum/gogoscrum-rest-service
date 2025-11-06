package com.shimi.gogoscrum.common.exception;

/**
 * ErrorCode is a class that defines various error codes used in the application.
 */
public class ErrorCode extends com.shimi.gsf.core.exception.ErrorCode {
    private ErrorCode(){
    }

    public static final String WRONG_PASSWORD = "wrongPassword";
    public static final String USER_NOT_FOUND = "userNotFound";
    public static final String USER_DISABLED = "userDisabled";
    public static final String INVALID_INVITATION = "invalidInvitation";
    public static final String ALREADY_IN_PROJECT = "alreadyInProject";
    public static final String CANNOT_BE_DELETED = "cannotDelete";
    public static final String DUPLICATED_USERNAME = "duplicatedUsername";
    public static final String DUPLICATED_GROUP = "duplicatedGroup";
    public static final String DUPLICATED_TAG = "duplicatedTag";
    public static final String HAS_CHILDREN = "hasChildren";
}
