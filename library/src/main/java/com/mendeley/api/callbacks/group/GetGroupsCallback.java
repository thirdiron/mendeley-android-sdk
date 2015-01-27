package com.mendeley.api.callbacks.group;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Group;
import com.mendeley.api.params.Page;

import java.util.List;

public interface GetGroupsCallback {
    public void onGroupsReceived(List<Group> groups, Page next);
    public void onGroupsNotReceived(MendeleyException mendeleyException);
}
