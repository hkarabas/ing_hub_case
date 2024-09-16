package com.ing_hub_case.exception;

import org.springframework.security.core.AuthenticationException;

public class UserUnAuthorizedException extends AuthenticationException {

      public UserUnAuthorizedException(String msg) {
        super(msg);
    }
}
