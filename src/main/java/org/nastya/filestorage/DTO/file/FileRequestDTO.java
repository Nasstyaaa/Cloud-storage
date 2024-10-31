package org.nastya.filestorage.DTO.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class FileRequestDTO {
    protected String path;
}
