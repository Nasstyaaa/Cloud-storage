package org.nastya.filestorage.DTO.folder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DownloadFolderRequestDTO {
    private String nameFolder;
    private String path;
}

