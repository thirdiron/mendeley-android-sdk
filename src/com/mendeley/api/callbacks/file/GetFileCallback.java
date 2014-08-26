package com.mendeley.api.callbacks.file;

import com.mendeley.api.exceptions.MendeleyException;

public interface GetFileCallback {
    public void onFileDownloadProgress(String fileId, String documentId, int progress);
    public void onFileReceived(String fileId, String documentId, String fileName);
    public void onFileNotReceived(String fileId, String documentId, MendeleyException mendeleyException);
}
