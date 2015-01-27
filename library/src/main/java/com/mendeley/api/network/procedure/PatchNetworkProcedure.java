package com.mendeley.api.network.procedure;

import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.network.NetworkUtils;
import com.mendeley.api.network.task.NetworkTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import static com.mendeley.api.network.NetworkUtils.getHttpPatch;

public class PatchNetworkProcedure extends NetworkProcedure<Void> {
    private final String url;
    private final String contentType;
    private final String json;
    private final String date;

    public PatchNetworkProcedure(String url, String contentType, String json, String date,
                                 AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.url = url;
        this.contentType = contentType;
        this.json = json;
        this.date = date;
    }

    @Override
    protected int getExpectedResponse() {
        return 200;
    }

    @Override
    protected Void run() throws MendeleyException {
        HttpClient httpclient = new DefaultHttpClient();
        NetworkUtils.HttpPatch httpPatch = getHttpPatch(url, date, contentType, authenticationManager);

        try {
            httpPatch.setEntity(new StringEntity(json));
            HttpResponse response = httpclient.execute(httpPatch);

            final int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != getExpectedResponse()) {
                throw new HttpResponseException(url, responseCode, NetworkUtils.getErrorMessage(response));
            }
        } catch (IOException e) {
            throw new JsonParsingException(e.getMessage());
        } finally {
            closeConnection();
        }
        return null;
    }
}
