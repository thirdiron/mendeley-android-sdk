package com.mendeley.api.callbacks.file;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;

public interface PostFileCallback {
    public void onFilePosted(File file);
    public void onFileNotPosted(MendeleyException mendeleyException);
}
