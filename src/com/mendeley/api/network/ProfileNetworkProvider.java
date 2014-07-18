package com.mendeley.api.network;

import org.json.JSONException;

import android.os.AsyncTask;

import com.mendeley.api.callbacks.profile.GetProfileCallback;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Profile;

import static com.mendeley.api.network.NetworkUtils.*;

/**
 * NetworkProvider class for Profile API calls
 */
public class ProfileNetworkProvider extends NetworkProvider {
	private static String profilesUrl = API_URL + "profiles/";

	/**
	 *  Executing GetMyProfileTask
	 */
    public void doGetMyProfile(GetProfileCallback callback) {
		String[] paramsArray = new String[] { profilesUrl + "me" };
        GetProfileTask task = new GetProfileTask(callback);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
	}
	
	/**
	 *  Executing GetProfileTask
	 * 
	 * @param profileId the profile id to get
	 */
    public void doGetProfile(String profileId, GetProfileCallback callback) {
		String[] paramsArray = new String[] { profilesUrl + profileId };
        GetProfileTask task = new GetProfileTask(callback);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
	}

    /* TASKS */

    private class GetProfileTask extends GetNetworkTask {
        private Profile profile;

        private final GetProfileCallback callback;

        private GetProfileTask(GetProfileCallback callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            profile = JsonParser.parseProfile(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-profiles.1+json";
        }

        @Override
        protected void onSuccess() {
            callback.onProfileReceived(profile);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onProfileNotReceived(exception);
        }
    }
}
