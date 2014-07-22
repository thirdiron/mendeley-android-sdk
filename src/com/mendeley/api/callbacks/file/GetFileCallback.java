package com.mendeley.api.callbacks.file;

import com.mendeley.api.exceptions.MendeleyException;

public interface GetFileCallback {
    public void onFileDownloadProgress(String fileId, String documentId, int progress);
    public void onFileReceived(String fileName, String fileId);
    public void onFileNotReceived(MendeleyException mendeleyException);
}
