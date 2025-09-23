package com.shimi.gogoscrum.file.service;

import com.shimi.gogoscrum.file.model.FileUploadToken;
import org.pf4j.ExtensionPoint;

import java.io.InputStream;

/**
 * Interface for file storage services. The implementation of this interface can be either a local file storage service
 * or a cloud object storage service, e.g. Aliyun OSS or AWS S3.
 * This interface is a PF4J extension point, so different implementations can be plugged in and selected at runtime.
 */
public interface FileStorage extends ExtensionPoint {
    /**
     * The upload token will be expired after the specified seconds. Each underlying storage service implementation need to
     * make sure this rule is implemented correctly, otherwise security issue may be caused.
     */
    long TOKEN_EXPIRE_IN_SECONDS = 30;

    /**
     * The maximum file size allowed for upload, in bytes. This is set to 100 MB.
     */
    long MAX_FILE_SIZE_IN_BYTES = 100 * 1024 * 1024;

    /**
     * Generate a file upload token for the specified file.
     * @param originalFileName the original file name of the file to be uploaded.
     * @param path the path where the file will be stored, only include the relative path, not the full URL.
     * @param fileName the file name to be used for the uploaded file.
     * @return a FileUploadToken containing information needed for uploading the file.
     */
    FileUploadToken generateUploadToken(String originalFileName, String path, String fileName);

    /**
     * Upload a file to the storage system.
     * @param inputStream the input stream of the file to be uploaded.
     * @param path the path where the file will be stored, only include the relative path, not the full URL.
     *                 The underlying storage service will determine the final full URL, i.e. the base upload path
     *                 plus this relative path.
     * @param fileName the file name
     * @return the URL of the uploaded file, which could be a local path or a remote URL. If a cloud service is used,
     * this method will return a full URL of the uploaded file. If a local file storage service is used, this method
     * will return a relative path to the file.
     */
    String upload(InputStream inputStream, String path, String fileName);

    /**
     * Delete a file from the storage system.
     * @param filePath the path of the file to be deleted.
     */
    void delete(String filePath);

    /**
     * Get the provider of the file storage service.
     * @return the provider name of the file storage service, e.g. LOCAL, ALIYUN, etc.
     */
    String getProvider();
}
