package com.mendeley.api.callbacks.folder;

import com.mendeley.api.model.Folder;
import com.mendeley.api.model.Group;
import com.mendeley.api.params.Page;

import java.util.List;

public class FolderList {
    public final List<Folder> folders;
    public final Page next;

    public FolderList(List<Folder> folders, Page next) {
        this.folders = folders;
        this.next = next;
    }
}
