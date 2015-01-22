package com.mendeley.api.callbacks.file;

import com.mendeley.api.model.Document;
import com.mendeley.api.model.File;
import com.mendeley.api.params.Page;

import java.util.Date;
import java.util.List;

public class FileList {
    public final List<File> files;
    public final Page next;
    public final Date serverDate;

    public FileList(List<File> files, Page next, Date serverDate) {
        this.files = files;
        this.next = next;
        this.serverDate = serverDate;
    }
}
