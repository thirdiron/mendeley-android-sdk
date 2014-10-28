package com.mendeley.api.callbacks.document;

import com.mendeley.api.model.Document;
import com.mendeley.api.params.Page;

import java.util.Date;
import java.util.List;

public class DocumentList {
    public final List<Document> documents;
    public final Page next;
    public final Date serverDate;

    public DocumentList(List<Document> documents, Page next, Date serverDate) {
        this.documents = documents;
        this.next = next;
        this.serverDate = serverDate;
    }
}
