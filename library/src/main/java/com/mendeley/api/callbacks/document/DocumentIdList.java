package com.mendeley.api.callbacks.document;

import com.mendeley.api.model.DocumentId;
import com.mendeley.api.params.Page;

import java.util.Date;
import java.util.List;

public class DocumentIdList {
    public final List<DocumentId> documentIds;
    public final Page next;
    public final Date serverDate;

    public DocumentIdList(List<DocumentId> documentIds, Page next, Date serverDate) {
        this.documentIds = documentIds;
        this.next = next;
        this.serverDate = serverDate;
    }
}
