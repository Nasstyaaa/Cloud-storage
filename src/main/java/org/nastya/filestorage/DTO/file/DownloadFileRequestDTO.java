package org.nastya.filestorage.DTO.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadFileRequestDTO extends FileRequestDTO{
    private String nameFile;

    public DownloadFileRequestDTO(String nameFile, String path) {
        super(path);
        this.nameFile = nameFile;
    }
}
