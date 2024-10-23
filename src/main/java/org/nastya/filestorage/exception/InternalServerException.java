package org.nastya.filestorage.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException() {
        super("Internal server error, try again later");
    }
}
