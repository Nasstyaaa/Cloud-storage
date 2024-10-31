package org.nastya.filestorage.DTO.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFileRequestDTO extends FileRequestDTO{
    private String nameFile;
    private String newName;

    public RenameFileRequestDTO(String nameFile, String newName, String path) {
        super(path);
        this.nameFile = nameFile;
        this.newName = newName;
    }
}
