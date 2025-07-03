package com.shimi.gogoscrum.common.util;

import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.model.FileType;
import com.shimi.gsf.core.exception.BaseServiceException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * Utility class for file operations.
 * This class provides methods to manipulate file names and extensions.
 */
public class FileUtil {
    private static final Map<FileType, List<String>> contentTypes = Map.of(
        FileType.IMAGE, List.of("image/"),
        FileType.AUDIO, List.of("audio/"),
        FileType.VIDEO, List.of("video/"),
        FileType.PDF, List.of("application/pdf"),
        FileType.ZIP, List.of("application/zip"),
        FileType.RAR, List.of("application/vnd.rar", "application/x-rar"),
        FileType.WORD, List.of("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        FileType.EXCEL, List.of("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        FileType.PPT, List.of("application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
    );

    private FileUtil(){}

    public static String removeExt(String fileName) {
        if (fileName.lastIndexOf(".") > 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
    }
    
    public static String getExt(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");

        if(lastIndexOfDot >= 0 && fileName.substring(lastIndexOfDot).length() > 1 ) {
            return fileName.substring(lastIndexOfDot + 1);
        } else {
            throw new BaseServiceException("unknownFileType", "Unknown file type: " + fileName, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static String generateRandomFileName(String ext) {
        return RandomToolkit.getRandomString(24) + "." + ext;
    }

    public static FileType getFileType(String mimeType) {
        for (Map.Entry<FileType, List<String>> entry : contentTypes.entrySet()) {
            if (entry.getValue().stream().anyMatch(mimeType::startsWith)) {
                return entry.getKey();
            }
        }
        return FileType.OTHER;
    }
}
