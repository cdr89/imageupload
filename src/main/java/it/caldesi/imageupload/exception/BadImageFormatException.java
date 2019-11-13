package it.caldesi.imageupload.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus (value = BAD_REQUEST)
public class BadImageFormatException extends RuntimeException {

    public BadImageFormatException() {
        super();
    }

}
