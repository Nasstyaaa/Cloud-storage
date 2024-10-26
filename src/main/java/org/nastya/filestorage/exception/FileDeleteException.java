package org.nastya.filestorage.exception;

public class FileDeleteException extends RuntimeException {
    public FileDeleteException() {
        super("File deletion error, try again");
    }
}
