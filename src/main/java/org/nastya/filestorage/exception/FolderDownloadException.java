package org.nastya.filestorage.exception;

public class FolderDownloadException extends RuntimeException {
    public FolderDownloadException() {
        super("Error downloading the folder, try again");
    }
}
