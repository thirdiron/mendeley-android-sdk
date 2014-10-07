package com.mendeley.api.network.task;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.network.NetworkUtils;
import com.mendeley.api.network.task.NetworkTask;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static com.mendeley.api.network.NetworkUtils.getConnection;
import static com.mendeley.api.network.NetworkUtils.getErrorMessage;
import static com.mendeley.api.network.NetworkUtils.getJsonString;

public abstract class PostNetworkTask extends NetworkTask {
    @Override
    protected int getExpectedResponse() {
        return 201;
    }

    @Override
    protected MendeleyException doInBackground(String... params) {
        String url = params[0];
        String jsonString = params[1];

        try {
            con = getConnection(url, "POST", getAccessTokenProvider());
            con.addRequestProperty("Content-type", getContentType());
            con.setFixedLengthStreamingMode(jsonString.getBytes().length);
            con.connect();

            os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonString);
            writer.flush();
            writer.close();
            os.close();

            getResponseHeaders();

            final int responseCode = con.getResponseCode();
            if (responseCode != getExpectedResponse()) {
                return new HttpResponseException(responseCode, NetworkUtils.getErrorMessage(con));
            } else {

                is = con.getInputStream();
                String responseString = getJsonString(is);

                processJsonString(responseString);

                return null;
            }

        }	catch (IOException | JSONException e) {
            return new JsonParsingException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    protected abstract void processJsonString(String jsonString) throws JSONException;

    protected abstract String getContentType();
}
