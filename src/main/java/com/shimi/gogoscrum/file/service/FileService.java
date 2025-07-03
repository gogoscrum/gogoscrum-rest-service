package com.shimi.gogoscrum.file.service;

import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.model.FileFilter;
import com.shimi.gogoscrum.file.model.FileUploadToken;
import com.shimi.gogoscrum.file.model.TargetType;
import com.shimi.gsf.core.service.GeneralService;
import org.springframework.web.multipart.MultipartFile;

public interface FileService extends GeneralService<File, FileFilter> {
    FileUploadToken generateUploadToken(String originalFileName, Long projectId, TargetType targetType);
    String upload(MultipartFile multipartFile, String path, String targetFileName);
    void deleteByPath(String path);
}
