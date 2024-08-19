package com.snap.wallet.demo.wallet_demo.constant;

public class Constant {
    public static final String REGISTERED="registered";
    public static final String PAYMENT="payment";
    public static final String APPROVED="approved";
    public static final String REJECT="reject";

    public static final String USER_AUTHORITIES = "document:create, document: read, document: update, document:delete";
    public static final String PROVIDER_AUTHORITIES = "user:create, user: read, user:update, document: create, document: read, document:update, document:delete";
    public static final String ADMIN_AUTHORITIES = "user:create, user: read, user:update, user: delete, document: create, document: read, document:update, document:delete";

    public static final String JWT_TYPE = "JWT";

    public static final String TYPE = "typ";
    public static final String GET_ARRAYS_LLC = "GET_ARRAYS_LLC";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String EMPTY_VALUE = "empty";
    public static final String AUTHORITY_DELIMITER = ",";
    public static final String AUTHORITIES = "authorities";
    public static final String ROLE = "role";
    public static final String SUPER_ADMIN_AUTHORITIES = "user:create, user: read, user:update, user: delete, document: create, document: read, document:update, document:delete";
    public static final String MANAGER_AUTHORITIES = "document: create, document: read, document:update, document:delete";
}
