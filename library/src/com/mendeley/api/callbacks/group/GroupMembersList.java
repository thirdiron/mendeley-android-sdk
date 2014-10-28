package com.mendeley.api.callbacks.group;

import com.mendeley.api.model.Group;
import com.mendeley.api.model.UserRole;
import com.mendeley.api.params.Page;

import java.util.List;

public class GroupMembersList {
    public List<UserRole> userRoles;
    public Page next;
}
