package com.mendeley.api.network;

import com.mendeley.api.MendeleySDK;
import com.mendeley.api.auth.AccessTokenProvider;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.callbacks.file.DeleteFileCallback;
import com.mendeley.api.callbacks.file.GetFileCallback;
import com.mendeley.api.callbacks.file.GetFilesCallback;
import com.mendeley.api.callbacks.file.PostFileCallback;
import com.mendeley.api.exceptions.FileDownloadException;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.NoMorePagesException;
import com.mendeley.api.exceptions.UserCancelledException;
import com.mendeley.api.model.File;
import com.mendeley.api.params.FileRequestParameters;
import com.mendeley.api.params.Page;
import com.mendeley.api.util.Utils;

import org.json.JSONException;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mendeley.api.network.NetworkUtils.API_URL;
import static com.mendeley.api.network.NetworkUtils.getConnection;
import static com.mendeley.api.network.NetworkUtils.getDownloadConnection;
import static com.mendeley.api.network.NetworkUtils.getErrorMessage;
import static com.mendeley.api.network.NetworkUtils.getJsonString;

/**
 * NetworkProvider class for Files API calls
 */
public class FileNetworkProvider {
	private Map<String, NetworkTask> fileTaskMap = new HashMap<String, NetworkTask>();

	private static String filesUrl = API_URL + "files";
	private static final String TAG = FileNetworkProvider.class.getSimpleName();

    private final Environment environment;
    private final AccessTokenProvider accessTokenProvider;

    public FileNetworkProvider(Environment environment, AccessTokenProvider accessTokenProvider) {
        this.environment = environment;
        this.accessTokenProvider = accessTokenProvider;
    }

    /**
	 * Getting the appropriate url string and executes the GetFilesTask
	 *
     * @param params the file request parameters
     * @param callback
     */
    public RequestHandle doGetFiles(FileRequestParameters params, GetFilesCallback callback) {
		try {
            String[] paramsArray = new String[] { getGetFilesUrl(params) };
			GetFilesTask getFilesTask = new GetFilesTask(callback);
			getFilesTask.executeOnExecutor(environment.getExecutor(), paramsArray);
            return getFilesTask;
		}
		catch (UnsupportedEncodingException e) {
            callback.onFilesNotReceived(new MendeleyException(e.getMessage()));
            return NullRequest.get();
		}
	}

    /**
     * Getting the appropriate url string and executes the GetFilesTask
     *
     * @param next reference to next page
     * @param callback
     */
    public RequestHandle doGetFiles(Page next, GetFilesCallback callback) {
        if (Page.isValidPage(next)) {
        	String[] paramsArray = new String[] { next.link };
            GetFilesTask getFilesTask = new GetFilesTask(callback);
            getFilesTask.executeOnExecutor(environment.getExecutor(), paramsArray);
            return getFilesTask;
        } else {
            callback.onFilesNotReceived(new NoMorePagesException());
            return NullRequest.get();
        }
    }

	/**
	 *  Getting the appropriate url string and executes the GetFileTask
	 *  @param fileId the id of the file to get
	 * @param folderPath the path in which to save the file
     * @param callback
     */
    public void doGetFile(final String fileId, final String documentId, final String folderPath, GetFileCallback callback) {
		final GetFileTask fileTask = new GetFileTask(callback);
		fileTaskMap.put(fileId, fileTask);
		String[] params = new String[] { getGetFileUrl(fileId), folderPath, fileId, documentId };
		fileTask.executeOnExecutor(environment.getExecutor(), params);
	}

    /**
     *  Building the url string with the parameters and executes the PostFileTask
     *  @param contentType content type of the file
     * @param documentId the id of the document the file is related to
     * @param filePath the absolute file path
     * @param callback
     */
    public void doPostFile(String contentType, String documentId, String filePath, PostFileCallback callback) {
        String fileName = filePath.substring(filePath.lastIndexOf(java.io.File.separator) + 1);
        String[] paramsArray = new String[] { contentType, documentId, fileName };

        java.io.File sourceFile = new java.io.File(filePath);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
        } catch (FileNotFoundException e) {
            callback.onFileNotPosted(new MendeleyException("File " + filePath + " not found"));
            return;
        }
        new PostFileTask(callback, inputStream).executeOnExecutor(environment.getExecutor(), paramsArray);
    }

    /**
     *  Building the url string with the parameters and executes the PostFileTask
     *  @param contentType content type of the file
     * @param documentId the id of the document the file is related to
     * @param inputStream provides the data to upload
     * @param callback
     */
    public void doPostFile(String contentType, String documentId, InputStream inputStream, String fileName, PostFileCallback callback) {
        String[] paramsArray = new String[] { contentType, documentId, fileName };
        new PostFileTask(callback, inputStream).executeOnExecutor(environment.getExecutor(), paramsArray);
    }

    /**
	 * Getting the appropriate url string and executes the DeleteFileTask
	 *
     * @param fileId the id of the file to delete
     * @param callback
     */
    public void doDeleteFile(String fileId, DeleteFileCallback callback) {
		String[] paramsArray = new String[]{ getDeleteFileUrl(fileId) };
		new DeleteFileTask(callback, fileId).executeOnExecutor(environment.getExecutor(), paramsArray);
	}
	
    /**
     * Cancelling the NetworkTask that is currently download the file with the given fileId.
     * @param fileId the id of the file
     */
    public void cancelDownload(String fileId) {
        GetFileTask task = (GetFileTask) fileTaskMap.get(fileId);
        if (task != null) {
            task.cancel(true);
        }
    }

    /* URLS */

    /**
     * Building the url for get files
     *
     * @param params the file request parameters
     * @return the url string
     * @throws UnsupportedEncodingException
     */
    String getGetFilesUrl(FileRequestParameters params) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder();
        url.append(filesUrl);

        if (params != null) {
            boolean firstParam = true;
            if (params.documentId != null) {
                url.append(firstParam?"?":"&").append("document_id="+params.documentId);
                firstParam = false;
            }
            if (params.groupId != null) {
                url.append(firstParam?"?":"&").append("group_id="+params.groupId);
                firstParam = false;
            }
            if (params.addedSince != null) {
                url.append(firstParam?"?":"&").append("added_since="+URLEncoder.encode(params.addedSince, "ISO-8859-1"));
                firstParam = false;
            }
            if (params.deletedSince != null) {
                url.append(firstParam?"?":"&").append("deleted_since="+URLEncoder.encode(params.deletedSince, "ISO-8859-1"));
            }
            if (params.limit != null) {
                url.append(firstParam?"?":"&").append("limit="+params.limit);
                firstParam = false;
            }
            if (params.catalogId != null) {
                url.append(firstParam?"?":"&").append("catalog_id="+params.catalogId);
                firstParam = false;
            }
        }

        return url.toString();
    }

    /**
     * Building the url for get files
     *
     * @param fileId the id of the file to get
     * @return the url string
     */
    String getGetFileUrl(String fileId) {
        return filesUrl+"/"+fileId;
    }

    /**
     * Building the url for delete files
     *
     * @param fileId the id of the file to delete
     * @return the url string
     */
    String getDeleteFileUrl(String fileId) {
        return filesUrl + "/" + fileId;
    }

    /* TASKS */

	private class GetFilesTask extends GetNetworkTask {
        private final GetFilesCallback callback;

		private List<File> files;

        private GetFilesTask(GetFilesCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            files = JsonParser.parseFileList(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-file.1+json";
        }

        @Override
	    protected void onCancelled (MendeleyException result) {
	    	callback.onFilesNotReceived(new UserCancelledException());
	    }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override

		protected void onSuccess() {
            callback.onFilesReceived(files, next, serverDate);
		}

		@Override
		protected void onFailure(MendeleyException exception) {		
			callback.onFilesNotReceived(exception);
		}
    }
	
	/**
	 * Executing the api call for getting a file in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * and send the data to the relevant callback method in the MendeleyFileInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	private class GetFileTask extends NetworkTask {
        private final GetFileCallback callback;

		List<File> files;
		byte[] fileData;
		String fileName;
		String fileId;
		String documentId;
		String filePath;

        private GetFileTask(GetFileCallback callback) {
            this.callback = callback;
        }

        @Override
		protected int getExpectedResponse() {
			return 303;
		}


        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String folderPath = params[1];
			fileId = params[2];
			documentId = params[3];

			FileOutputStream fileOutputStream = null;

			try {
				con = getConnection(url, "GET", getAccessTokenProvider());
				con.setInstanceFollowRedirects(false);
				con.connect();
				
				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
					return new FileDownloadException(getErrorMessage(con), fileId);
				} else {		
					con.disconnect();
					
					con = getDownloadConnection(location, "GET");
					con.connect();
					
					int responseCode = con.getResponseCode();
					
					if (responseCode != 200) {
						return new FileDownloadException(getErrorMessage(con), fileId);
					} else {
						String content = con.getHeaderFields().get("Content-Disposition").get(0);
						fileName = content.substring(content.indexOf("\"")+1, content.lastIndexOf("\""));
						
						int fileLength = con.getContentLength();
						is = con.getInputStream();			
						filePath = folderPath+java.io.File.separator+fileName;
						fileOutputStream = new FileOutputStream(new java.io.File(filePath));
						
						byte data[] = new byte[1024];
			            long total = 0;
			            int count;
			            while ((count = is.read(data)) != -1 && !isCancelled()) {
			                total += count;
			                if (fileLength > 0) 
			                    publishProgress((int) (total * 100 / fileLength));
			                fileOutputStream.write(data, 0, count);
			            }
					    fileOutputStream.close();
						
						return null;
					}
				}
			}	catch (IOException e) {
				return new FileDownloadException(e.getMessage(), fileId);
			} finally {
				closeConnection();
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
						fileOutputStream = null;
					} catch (IOException e) {
						return new FileDownloadException(e.getMessage(), fileId);
					}
				}
			}
		} 
		
	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	    	callback.onFileDownloadProgress(fileId, documentId, progress[0]);
	    }
	    
	    @Override
	    protected void onCancelled (MendeleyException result) {
	    	fileTaskMap.remove(fileId);
	    	
	    	if (filePath != null) {
		    	java.io.File file = new java.io.File(filePath);
		    	file.delete();
	    	}
	    }
	    
		@Override
		protected void onSuccess() {		
			fileTaskMap.remove(fileId);
			callback.onFileReceived(fileId, documentId, fileName);
		}

		@Override
		protected void onFailure(MendeleyException exception) {		
			fileTaskMap.remove(fileId);
			callback.onFileNotReceived(fileId, documentId, exception);
		}
	}

    /**
     * Executing the api call for posting file in the background.
     * Calling the appropriate JsonParser method to parse the json string to objects
     * and send the data to the relevant callback method in the MendeleyFileInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class PostFileTask extends NetworkTask {
        private final PostFileCallback callback;
        private final InputStream inputStream;

        private File file;

        public PostFileTask(PostFileCallback callback, InputStream inputStream) {
            super();
            this.callback = callback;
            this.inputStream = inputStream;
        }

        @Override
        protected int getExpectedResponse() {
            return 201;
        }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
        protected MendeleyException doInBackground(String... params) {
            String contentType = params[0];
            String documentId = params[1];
            String fileName = params[2];

            String contentDisposition = "attachment; filename*=UTF-8\'\'"+fileName;
            String link = "<https://api.mendeley.com/documents/"+documentId+">; rel=\"document\"";

            try {
                int bytesAvailable;
                final int MAX_BUF_SIZE = 65536;
                int bufferSize;
                final byte[] buffer = new byte[MAX_BUF_SIZE];
                int bytesRead;

                con = getConnection(filesUrl, "POST", getAccessTokenProvider());
                con.addRequestProperty("Content-Disposition", contentDisposition);
                con.addRequestProperty("Content-type", contentType);
                con.addRequestProperty("Link", link);

                os = new DataOutputStream(con.getOutputStream());

                bytesAvailable = inputStream.available();
                bufferSize = Math.min(bytesAvailable, MAX_BUF_SIZE);
                bytesRead = inputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    os.write(buffer, 0, bufferSize);
                    bytesAvailable = inputStream.available();
                    bufferSize = Math.min(bytesAvailable, MAX_BUF_SIZE);
                    bytesRead = inputStream.read(buffer, 0, bufferSize);
                }

                os.close();
                inputStream.close();
                con.connect();

                getResponseHeaders();

                final int responseCode = con.getResponseCode();
                if (responseCode != getExpectedResponse()) {
                    return new HttpResponseException(responseCode, getErrorMessage(con));
                } else {

                    is = con.getInputStream();
                    String jsonString = getJsonString(is);
                    is.close();

                    file = JsonParser.parseFile(jsonString);

                    return null;
                }
            } catch (IOException | JSONException e) {
                return new JsonParsingException(e.getMessage());
            } catch (NullPointerException e) {
                return new MendeleyException(e.getMessage());
            }
            finally {
                closeConnection();
                Utils.closeQuietly(inputStream);
            }
        }

        @Override
        protected void onSuccess() {
            callback.onFilePosted(file);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onFileNotPosted(exception);
        }
    }

    /**
	 * Executing the api call for deleting a file in the background.
	 * and send the data to the relevant callback method in the MendeleyFileInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	private class DeleteFileTask extends DeleteNetworkTask {
        private final DeleteFileCallback callback;
		private final String fileId;

        public DeleteFileTask(DeleteFileCallback callback, String fileId) {
            this.callback = callback;
            this.fileId = fileId;
        }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
		protected void onSuccess() {	
			callback.onFileDeleted(fileId);
		}
		
		@Override
		protected void onFailure(MendeleyException exception) {	
			callback.onFileNotDeleted(exception);
		}
	}
}
