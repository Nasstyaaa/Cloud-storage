package org.nastya.filestorage.exception;

public class EmptyFolderException extends RuntimeException {
    public EmptyFolderException() {
        super("Empty folder");
    }
}
