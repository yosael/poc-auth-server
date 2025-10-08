package com.yosael.pocauthserver.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
  public UsernameAlreadyExistsException(String msg) { super(msg); }
}
