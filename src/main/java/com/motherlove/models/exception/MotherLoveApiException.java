package com.motherlove.models.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MotherLoveApiException extends RuntimeException{
    private final HttpStatus status;
    private final String message;
    public MotherLoveApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
