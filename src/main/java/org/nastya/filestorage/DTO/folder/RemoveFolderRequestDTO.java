package org.nastya.filestorage.DTO.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveFolderRequestDTO extends FolderRequestDTO{
    private String nameFolder;

    public RemoveFolderRequestDTO(String nameFolder, String path) {
        super(path);
        this.nameFolder = nameFolder;
    }
}
