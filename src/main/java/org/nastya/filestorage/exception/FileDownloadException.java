package org.nastya.filestorage.exception;

public class FileDownloadException extends RuntimeException {
    public FileDownloadException() {
        super("Error downloading the file, try again");
    }
}
