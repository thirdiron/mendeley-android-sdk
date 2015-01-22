package com.mendeley.api.callbacks.file;

import com.mendeley.api.exceptions.MendeleyException;

public interface DeleteFileCallback {
    public void onFileDeleted(String fileId);
    public void onFileNotDeleted(MendeleyException mendeleyException);
}
