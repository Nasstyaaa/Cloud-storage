package org.nastya.filestorage.DTO.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class FolderRequestDTO {
    protected String path;
}
