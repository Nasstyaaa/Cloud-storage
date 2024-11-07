package org.nastya.filestorage.DTO.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFileRequestDTO extends FileRequestDTO{
    private String nameFile;
    private String newPath;

    public RenameFileRequestDTO(String nameFile, String newPath, String path) {
        super(path);
        this.nameFile = nameFile;
        this.newPath = newPath;
    }
}
