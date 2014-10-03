package com.mendeley.api.network;

import android.util.Log;

import com.mendeley.api.auth.AccessTokenProvider;
import com.mendeley.api.callbacks.utils.GetImageCallback;
import com.mendeley.api.exceptions.MendeleyException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

public class UtilsNetworkProvider {

    private final Environment environment;
    private final AccessTokenProvider accessTokenProvider;

    public UtilsNetworkProvider(Environment environment, AccessTokenProvider accessTokenProvider) {
        this.environment = environment;
        this.accessTokenProvider = accessTokenProvider;
    }

    public void doGetImage(final String url, GetImageCallback callback) {
        final GetImageTask imageTask = new GetImageTask(callback);
        String[] params = new String[] {url};
        imageTask.executeOnExecutor(environment.getExecutor(), params);
    }

    private class GetImageTask extends NetworkTask {
        private final GetImageCallback callback;

        byte[] fileData;
        HttpURLConnection con = null;

        private GetImageTask(GetImageCallback callback) {
            this.callback = callback;
        }

        @Override
        protected int getExpectedResponse() {
            return 200;
        }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
        protected MendeleyException doInBackground(String... params) {

            String url = params[0];

            ByteArrayOutputStream bais = null;

            try {
                con = NetworkUtils.getHttpDownloadConnection(url, "GET");
                con.connect();

                int responseCode = con.getResponseCode();

                if (responseCode != getExpectedResponse()) {
                    Log.e("", "responseCode: " + responseCode);

                    return new MendeleyException(con.getResponseMessage());
                } else {

                    int fileLength = con.getContentLength();
                    is = con.getInputStream();
                    bais = new ByteArrayOutputStream();

                    byte data[] = new byte[256];
                    long total = 0;
                    int count;
                    while ((count = is.read(data)) != -1 && !isCancelled()) {
                        total += count;
                        if (fileLength > 0)
                            publishProgress((int) (total * 100 / fileLength));
                        bais.write(data, 0, count);
                    }

                    fileData = bais.toByteArray();
                    bais.close();

                    return null;
                }
            } catch (IOException e) {
                return new MendeleyException(e.getMessage());
            } finally {
                closeConnection();
                if (bais != null) {
                    try {
                        bais.close();
                    } catch (IOException e) {
                        return new MendeleyException(e.getMessage());
                    }
                }
            }
        }

        @Override
        protected void onCancelled (MendeleyException result) {
            fileData = null;
            super.onCancelled(result);
        }

        @Override
        protected void onSuccess() {
            callback.onImageReceived(fileData);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onImageNotReceived(exception);
        }
    }
}
