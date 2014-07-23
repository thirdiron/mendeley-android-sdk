package com.mendeley.api.callbacks.file;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;
import com.mendeley.api.params.Page;

import java.util.List;

public interface GetFilesCallback {
    public void onFilesReceived(List<File> files, Page next);
    public void onFilesNotReceived(MendeleyException mendeleyException);
}
