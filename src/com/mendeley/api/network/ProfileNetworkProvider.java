package com.mendeley.api.network;

import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;

import android.os.AsyncTask;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Profile;
import com.mendeley.api.network.components.MendeleyResponse;
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
	protected class GetProfileTask extends AsyncTask<String, Void, MendeleyException> {

		Profile profile;
		MendeleyResponse response = new MendeleyResponse();
		int expectedResponse = 200;
		String id;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			id = params[1];
			
			url += id;

			HttpsURLConnection con = null;

			InputStream is = null;
			try {
				
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-profiles.1+json");
				con.connect();

				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);				

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();
			
					JasonParser parser = new JasonParser();
					profile = parser.parseProfile(jsonString);
					
					return null;
				}
				 
			}	catch (IOException | JSONException e) {			
				return new JsonParsingException(e.getMessage());
			} finally {
				if (is != null) {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
						return new JsonParsingException(e.getMessage());
					}
				}
				if (con != null) {
					con.disconnect();
				}			if (con != null) {
					con.disconnect();
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {		
			response.mendeleyException = result;
			if (id.equals("me")) {
				appInterface.onMyProfileReceived(profile, response);	
			} else {
				appInterface.onProfileReceived(profile, response);	
			}		
		}
	}
}
