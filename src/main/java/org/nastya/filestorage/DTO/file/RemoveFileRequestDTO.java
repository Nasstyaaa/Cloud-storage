package org.nastya.filestorage.DTO.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RemoveFileRequestDTO {
    private String nameFile;
    private String path;
}

