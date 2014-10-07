package com.mendeley.api.callbacks.file;

import com.mendeley.api.exceptions.MendeleyException;

public interface GetFileCallback {
    public void onFileDownloadProgress(String fileId, int progress);
    public void onFileReceived(String fileId, String fileName);
    public void onFileNotReceived(String fileId, MendeleyException mendeleyException);
}
