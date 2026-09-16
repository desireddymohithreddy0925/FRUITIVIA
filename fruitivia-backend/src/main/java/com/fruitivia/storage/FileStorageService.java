package com.fruitivia.storage;

import java.io.InputStream;

public interface FileStorageService {
    
    /**
     * Upload a file to storage.
     * @param key The unique key for the file
     * @param inputStream The input stream containing file data
     * @param contentType The MIME type
     * @param size The size of the file
     */
    void upload(String key, InputStream inputStream, String contentType, long size);

    /**
     * Download a file from storage.
     * @param key The unique key for the file
     * @return InputStream of the file data
     */
    InputStream download(String key);

    /**
     * Delete a file from storage.
     * @param key The unique key for the file
     */
    void delete(String key);

    /**
     * Generate a presigned URL for downloading a file securely.
     * @param key The unique key for the file
     * @param expirySeconds The number of seconds the URL should be valid
     * @return The presigned URL
     */
    String getPresignedUrl(String key, int expirySeconds);
}
