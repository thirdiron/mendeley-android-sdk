package com.mendeley.api.network;

import java.io.IOException;
import org.json.JSONException;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Profile;
import com.mendeley.api.network.interfaces.MendeleyProfileInterface;

/**
 * NetworkProvider class for Profile API calls
 *
 */
public class ProfileNetworkProvider extends NetworkProvider {

	private static String profilesUrl = apiUrl + "profiles/";
	MendeleyProfileInterface appInterface;
	
	ProfileNetworkProvider(MendeleyProfileInterface appInterface) {
		this.appInterface = appInterface;
	}
	
	/**
	 *  Executing GetProfileTask
	 * 
	 */
	protected void doGetMyProfile() {
		new GetProfileTask().execute(profilesUrl, "me");		  
	}
	
	/**
	 *  Executing GetProfileTask
	 * 
	 * @param profileId the profile id to get
	 */
	protected void doGetProfile(String profileId) {		
		new GetProfileTask().execute(profilesUrl, profileId);		  
	}
	
	/**
	 * Executing the api call for getting the user profile in the background.
	 * Calling the appropriate JsonParser method to parse the json string to Profile object 
	 * and send the data to the relevant callback method in the MendeleyProfileInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class GetProfileTask extends NetworkTask {

		Profile profile;
		String id;

		@Override
		protected int getExpectedResponse() {
			return 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			id = params[1];			
			url += id;

			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-profiles.1+json");
				con.connect();

				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response, next);

				if (response.responseCode != getExpectedResponse()) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
			
					JsonParser parser = new JsonParser();
					profile = parser.parseProfile(jsonString);
					
					return null;
				}
			}	catch (IOException | JSONException e) {			
				return new JsonParsingException(e.getMessage());
			} finally {
				closeConnection();
			}
		}
		
		@Override
		protected void onSuccess() {
			if (id.equals("me")) {
				appInterface.onMyProfileReceived(profile);
			} else {
				appInterface.onProfileReceived(profile);
			}		
		}
		
		@Override
		protected void onFailure(MendeleyException exception) {
			if (id.equals("me")) {
				appInterface.onMyProfileNotReceived(exception);
			} else {
				appInterface.onProfileNotReceived(exception);
			}		
		}
	}
}
