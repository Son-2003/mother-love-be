package com.motherlove.models.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MotherLoveApiException extends RuntimeException{
    @Getter
    private final HttpStatus status;

    @Getter
    private final String message;

    public MotherLoveApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
