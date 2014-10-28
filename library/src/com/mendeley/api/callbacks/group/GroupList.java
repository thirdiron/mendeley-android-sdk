package com.mendeley.api.callbacks.group;

import com.mendeley.api.model.Document;
import com.mendeley.api.model.Group;
import com.mendeley.api.params.Page;

import java.util.Date;
import java.util.List;

public class GroupList {
    public List<Group> groups;
    public Page next;
}
