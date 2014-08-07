package com.mendeley.api.network;

import android.os.AsyncTask;

import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.callbacks.group.GetGroupCallback;
import com.mendeley.api.callbacks.group.GetGroupMembersCallback;
import com.mendeley.api.callbacks.group.GetGroupsCallback;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.NoMorePagesException;
import com.mendeley.api.exceptions.UserCancelledException;
import com.mendeley.api.model.Group;
import com.mendeley.api.model.UserRole;
import com.mendeley.api.params.GroupRequestParameters;
import com.mendeley.api.params.Page;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import static com.mendeley.api.network.NetworkUtils.API_URL;
import static com.mendeley.api.network.NetworkUtils.getConnection;
import static com.mendeley.api.network.NetworkUtils.getErrorMessage;
import static com.mendeley.api.network.NetworkUtils.getJsonString;

/**
 * NetworkProvider class for Group API calls
 */

public class GroupNetworkProvider extends NetworkProvider{
    private static String groupsUrl = API_URL + "groups";

    /**
     * Getting the appropriate url string and executes the GetGroupsTask
     *
     * @param params group request parameters object
     * @param callback GetGroupsCallback callback object
     */
    public RequestHandle doGetGroups(GroupRequestParameters params, GetGroupsCallback callback) {
        String[] paramsArray = new String[] { getGetGroupsUrl(params) };
        GetGroupsTask getGroupsTask = new GetGroupsTask(callback);
        getGroupsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
        return getGroupsTask;
    }

    /**
     * Getting the appropriate url string and executes the GetGroupsTask
     *
     * @param next reference to next page
     */
    public RequestHandle doGetGroups(Page next, GetGroupsCallback callback) {
        if (Page.isValidPage(next)) {
            String[] paramsArray = new String[]{next.link};
            GetGroupsTask getGroupsTask = new GetGroupsTask(callback);
            new GetGroupsTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
            return getGroupsTask;
        } else {
            callback.onGroupsNotReceived(new NoMorePagesException());
            return NullRequest.get();
        }
    }

    /**
     * Getting the appropriate url string and executes the GetGroupTask
     *
     * @param groupId the group id to get
     */
    public void doGetGroup(String groupId, GetGroupCallback callback) {
        String[] paramsArray = new String[] { getGetGroupUrl(groupId) };
        new GetGroupTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
    }

    /**
     * Getting the appropriate url string and executes the GetGroupMembersTask
     *
     * @param groupId the group id
     */
    public void doGetGroupMembers(GroupRequestParameters params, String groupId, GetGroupMembersCallback callback) {
        String[] paramsArray = new String[] { getGetGroupsUrl(params, getGetGroupMembersUrl(groupId)) };
        new GetGroupMembersTask(callback, groupId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
    }

    /**
     * Getting the appropriate url string and executes the GetFolderDocumentIdsTask
     *
     * @param next reference to next page
     */
    public void doGetGroupMembers(Page next, String groupId, GetGroupMembersCallback callback) {
        if (Page.isValidPage(next)) {
            String[] paramsArray = new String[] { next.link };
            new GetGroupMembersTask(callback, groupId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
        } else {
            callback.onGroupMembersNotReceived(new NoMorePagesException());
        }
    }

    /* URLS */

    String getGetGroupsUrl(GroupRequestParameters params) {
        return getGetGroupsUrl(params, null);
    }

    /**
     * Building the url for get groups
     *
     * @param params group request parameters object
     * @return the url string
     */
    String getGetGroupsUrl(GroupRequestParameters params, String requestUrl) {
        StringBuilder url = new StringBuilder();

        url.append(requestUrl == null ? groupsUrl : requestUrl);

        if (params != null) {
            boolean firstParam = true;
            if (params.limit != null) {
                url.append(firstParam ? "?" : "&").append("limit=" + params.limit);
                firstParam = false;
            }
        }
        return url.toString();
    }

    /**
     * Building the url for get group
     *
     * @param groupId the group id to get
     * @return the url string
     */
    String getGetGroupUrl(String groupId) {
        return groupsUrl+"/"+groupId;
    }

    /**
     * Building the url for get group members
     *
     * @param groupId the group id
     * @return the url string
     */
    String getGetGroupMembersUrl(String groupId) {
        return groupsUrl + "/"+groupId + "/members";
    }

    /* TASKS */

    private class GetGroupsTask extends GetNetworkTask {
        private final GetGroupsCallback callback;

        List<Group> groups;

        private GetGroupsTask(GetGroupsCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            groups = JsonParser.parseGroupList(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-group.1+json";
        }

        @Override
        protected void onCancelled (MendeleyException result) {
            callback.onGroupsNotReceived(new UserCancelledException());
        }

        @Override
        protected void onSuccess() {
            callback.onGroupsReceived(groups, next);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onGroupsNotReceived(exception);
        }
    }

    private class GetGroupTask extends GetNetworkTask {
        private final GetGroupCallback callback;

        Group group;

        private GetGroupTask(GetGroupCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            group = JsonParser.parseGroup(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-group.1+json";
        }

        @Override
        protected void onSuccess() {
            callback.onGroupReceived(group);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onGroupNotReceived(exception);
        }
    }

    private class GetGroupMembersTask extends GetNetworkTask {
        private final GetGroupMembersCallback callback;

        private List<UserRole> userRoles;
        private final String groupId;

        private GetGroupMembersTask(GetGroupMembersCallback callback, String groupId) {
            this.callback = callback;
            this.groupId = groupId;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            userRoles = JsonParser.parseUserRoleList(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-membership.1+json";
        }

        @Override
        protected void onSuccess() {
            callback.onGroupMembersReceived(groupId, userRoles, next);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onGroupMembersNotReceived(exception);
        }
    }
}
