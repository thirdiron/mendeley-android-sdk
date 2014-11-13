package com.mendeley.api.network.task;

import android.util.Log;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.UserCancelledException;

import org.json.JSONException;

import java.io.IOException;

import static com.mendeley.api.network.NetworkUtils.getConnection;
import static com.mendeley.api.network.NetworkUtils.getErrorMessage;
import static com.mendeley.api.network.NetworkUtils.getJsonString;

/**
 * A NetworkTask specialised for making HTTP GET requests.
 */
public abstract class GetNetworkTask extends NetworkTask {
    private static final String TAG = GetNetworkTask.class.getSimpleName();

    private static final Integer MAX_RETRIES = 3;

    @Override
    protected int getExpectedResponse() {
        return 200;
    }

    @Override
    protected MendeleyException doInBackground(String... params) {
        try {
            executeRequest(params[0], 0);
            return null;
        } catch (MendeleyException me) {
            return me;
        }
    }

    private void executeRequest(final String url, final int currentRetry) throws MendeleyException {
        String responseBody = null;
        try {
            con = getConnection(url, "GET", getAccessTokenProvider());
            con.addRequestProperty("Content-type", getContentType());
            con.connect();

            getResponseHeaders();

            final int responseCode = con.getResponseCode();
            if (responseCode != getExpectedResponse()) {
                throw  new HttpResponseException(responseCode, getErrorMessage(con));
            }

            if (isCancelled()) {
                throw new UserCancelledException();
            }

            is = con.getInputStream();
            responseBody = getJsonString(is);
            processJsonString(responseBody);
        } catch (MendeleyException me) {
            throw me;
        } catch (IOException ioe) {
            // If the issue is due to IOException, retry up to MAX_RETRIES times
            // TODO: move this logic to the blocking version and delegate into it
            if (currentRetry <  MAX_RETRIES) {
                Log.w(TAG, "Problem connecting to " + url + ": " + ioe.getMessage() + ". Retrying (" + (currentRetry + 1) + "/" + MAX_RETRIES + ")");
                executeRequest(url, currentRetry + 1);
            } else {
                throw new MendeleyException("Error reading server response: " + ioe.toString(), ioe);
            }
        } catch (JSONException e) {
            throw new JsonParsingException("Error parsing server response: " + e.toString() + ". Response was: " + responseBody, e);
        } catch (Exception e) {
            throw new MendeleyException("Error reading server response: " + e.toString(), e);
        } finally {
            closeConnection();
        }
    }

    protected abstract void processJsonString(String jsonString) throws JSONException;

    protected abstract String getContentType();
}
