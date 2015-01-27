package com.mendeley.api.callbacks.group;

import com.mendeley.api.model.Document;
import com.mendeley.api.model.Group;
import com.mendeley.api.params.Page;

import java.util.Date;
import java.util.List;

public class GroupList {
    public final List<Group> groups;
    public final Page next;

    public GroupList(List<Group> groups, Page next) {
        this.groups = groups;
        this.next = next;
    }
}
