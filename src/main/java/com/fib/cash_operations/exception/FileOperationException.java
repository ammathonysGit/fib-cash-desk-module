package com.fib.cash_operations.exception;

public class FileOperationException extends RuntimeException {

    public FileOperationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
