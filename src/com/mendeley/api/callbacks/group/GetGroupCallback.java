package com.mendeley.api.callbacks.group;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Group;


public interface GetGroupCallback {
    public void onGroupReceived(Group group);
    public void onGroupNotReceived(MendeleyException mendeleyException);
}
