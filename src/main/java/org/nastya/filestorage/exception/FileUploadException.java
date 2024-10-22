package org.nastya.filestorage.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException() {
        super("Error uploading the file, try again");
    }
}
