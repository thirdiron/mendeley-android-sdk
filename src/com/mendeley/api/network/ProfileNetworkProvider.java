package com.mendeley.api.network;

import java.io.IOException;

import org.json.JSONException;

import android.os.AsyncTask;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Profile;
import com.mendeley.api.network.interfaces.MendeleyProfileInterface;

import static com.mendeley.api.network.NetworkUtils.*;

/**
 * NetworkProvider class for Profile API calls
 */
public class ProfileNetworkProvider extends NetworkProvider {
	private static String profilesUrl = API_URL + "profiles/";
	MendeleyProfileInterface appInterface;
	
	public ProfileNetworkProvider(MendeleyProfileInterface appInterface) {
		this.appInterface = appInterface;
	}
	
	/**
	 *  Executing GetProfileTask
	 */
    public void doGetMyProfile() {
		String[] paramsArray = new String[] { profilesUrl + "me" };
		new GetMyProfileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
	}
	
	/**
	 *  Executing GetProfileTask
	 * 
	 * @param profileId the profile id to get
	 */
    public void doGetProfile(String profileId) {
		String[] paramsArray = new String[] { profilesUrl + profileId };
		new GetProfileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
	}

    /* TASKS */

	private class GetMyProfileTask extends GetNetworkTask {
		Profile profile;

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
    		appInterface.onMyProfileReceived(profile);
		}
		
		@Override
		protected void onFailure(MendeleyException exception) {
    		appInterface.onMyProfileNotReceived(exception);
		}
	}

    private class GetProfileTask extends GetNetworkTask {
        Profile profile;

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
            appInterface.onProfileReceived(profile);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            appInterface.onProfileNotReceived(exception);
        }
    }
}
