package com.mendeley.api.callbacks.folder;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Folder;

public interface PostFolderCallback {
    public void onFolderPosted(Folder folder);
    public void onFolderNotPosted(MendeleyException mendeleyException);
}
