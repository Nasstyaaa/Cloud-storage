package org.nastya.filestorage.exception;

public class SearchFileError extends RuntimeException {
    public SearchFileError() {
        super("File search error, try again");
    }
}
