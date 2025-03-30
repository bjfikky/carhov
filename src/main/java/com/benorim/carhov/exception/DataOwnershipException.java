package com.benorim.carhov.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DataOwnershipException extends RuntimeException {
    public DataOwnershipException(String message) {
        super(message);
    }
}
