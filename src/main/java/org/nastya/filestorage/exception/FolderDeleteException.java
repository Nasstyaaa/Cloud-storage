package org.nastya.filestorage.exception;

public class FolderDeleteException extends RuntimeException {
    public FolderDeleteException() {
        super("Folder deletion error, try again");
    }
}
