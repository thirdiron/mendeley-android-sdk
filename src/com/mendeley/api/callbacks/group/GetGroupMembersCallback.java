package com.mendeley.api.callbacks.group;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.UserRole;
import com.mendeley.api.params.Page;

import java.util.List;

public interface GetGroupMembersCallback {
    public void onGroupMembersReceived(String groupId, List<UserRole> userRoles, Page next);
    public void onGroupMembersNotReceived(MendeleyException mendeleyException);
}
