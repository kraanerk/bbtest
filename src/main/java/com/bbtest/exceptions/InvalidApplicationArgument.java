package com.bbtest.exceptions;

public class InvalidApplicationArgument extends RuntimeException {
  public InvalidApplicationArgument(String message) {
    super(message);
  }
}
