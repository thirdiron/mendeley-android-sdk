package com.mendeley.api.callbacks.document;

import com.mendeley.api.model.Document;
import com.mendeley.api.params.Page;

import java.util.Date;
import java.util.List;

public class DocumentList {
    public List<Document> documents;
    public Page next;
    public Date serverDate;
}
