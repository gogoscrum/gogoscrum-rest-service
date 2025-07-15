package com.shimi.gogoscrum.file.service;

import com.shimi.gogoscrum.file.model.FileUploadToken;
import com.shimi.gsf.core.exception.BaseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Local file storage implementation for saving files to the local filesystem.
 */
@Service
public class LocalFileStorageImpl implements FileStorage {
    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageImpl.class);
    private static final String UPLOAD_ACTION_URL = "/api/files/upload";
    private static final String STORAGE_PROVIDER_LOCAL = "LOCAL";

    @Value("${file.local.dir.base}")
    private String baseDir;

    @Value("${file.local.url.prefix}")
    private String urlPrefix;

    public LocalFileStorageImpl() {
        if (log.isDebugEnabled()) {
            log.debug("LocalFileStorageImpl initialized");
        }
    }

    @Override
    public FileUploadToken generateUploadToken(String originalFileName, String path, String fileName) {
        FileUploadToken token = new FileUploadToken();

        token.setProvider(this.getProvider());
        token.setUploadActionUrl(UPLOAD_ACTION_URL);
        token.setUrlPrefix(urlPrefix);

        if (log.isTraceEnabled()) {
            log.trace("Generated upload token: {}", token);
        }

        return token;
    }

    @Override
    public String upload(InputStream inputStream, String path, String fileName) {
        String dirPath = StringUtils.hasText(path) ? baseDir + path : baseDir;
        String filePath = StringUtils.hasText(path) ? path + "/" + fileName : fileName;
        try {
            Files.createDirectories(Paths.get(dirPath));
            Path fileFullPath = Path.of(dirPath, fileName);
            Files.copy(inputStream, fileFullPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved to local path: {}", fileFullPath);
            return filePath;
        } catch (IOException e) {
            throw new BaseServiceException("internalError", "Failed to save file: " + filePath, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public void delete(String filePath) {
        Path fullPath = Path.of(baseDir, filePath);
        try {
            boolean success = Files.deleteIfExists(fullPath);
            if (success) {
                log.info("File deleted from local path: {}", fullPath);
            } else {
                log.warn("File not found for deletion: {}", fullPath);
            }
        } catch (IOException e) {
            throw new BaseServiceException("internalError", "Failed to delete file: " + filePath, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public String getProvider() {
        return STORAGE_PROVIDER_LOCAL;
    }
}
