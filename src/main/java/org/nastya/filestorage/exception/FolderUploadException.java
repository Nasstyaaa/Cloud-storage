package org.nastya.filestorage.exception;

public class FolderUploadException extends RuntimeException {
    public FolderUploadException() {
        super("Error uploading the folder, try again");
    }
}
