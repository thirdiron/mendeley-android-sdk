package com.mendeley.api.network.provider;

import android.os.AsyncTask;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.mendeley.api.auth.AccessTokenProvider;
import com.mendeley.api.network.Environment;
import com.mendeley.api.network.NetworkUtils;
import com.mendeley.api.params.FileRequestParameters;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.concurrent.Executor;

public class FileNetworkProviderTest extends AndroidTestCase {
	private FileNetworkProvider provider;
    private String filesUrl;
    private final String fileId = "test-file_id";
	
	@Override
	protected void setUp() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Environment environment = new Environment() {
            @Override
            public Executor getExecutor() {
                return AsyncTask.SERIAL_EXECUTOR;
            }
        };
        final AccessTokenProvider accessTokenProvider = new AccessTokenProvider() {
            @Override
            public String getAccessToken() {
                return null;
            }
        };
        provider = new FileNetworkProvider(environment, accessTokenProvider);
		
		String apiUrl = NetworkUtils.API_URL;
		
		filesUrl = apiUrl+"files";
    }
	
	@SmallTest
	public void test_getGetFilesUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, UnsupportedEncodingException {
		
		String documentId = "test-document_id";
		String groupId = "test-group_id";
		String addedSince = "2014-02-28T11:52:30.000Z";
		String deletedSince = "2014-01-21T11:52:30.000Z";

		String paramsString = "?document_id=" + documentId +
				"&group_id=" + groupId +
				"&added_since=" +  URLEncoder.encode(addedSince, "ISO-8859-1") + 
				"&deleted_since=" + URLEncoder.encode(deletedSince, "ISO-8859-1");
		
		String expectedUrl = filesUrl+paramsString;

		FileRequestParameters params = new FileRequestParameters();
		params.documentId = documentId;		
		params.groupId = groupId;
		params.addedSince = addedSince;
		params.deletedSince = deletedSince;

		String url = FileNetworkProvider.getGetFilesUrl(params);
		
		assertEquals("Get files url with parameters is wrong", expectedUrl, url);
		
		expectedUrl = filesUrl;
		params = new FileRequestParameters();
		url = FileNetworkProvider.getGetFilesUrl(params);
		
		assertEquals("Get files url without parameters is wrong", expectedUrl, url);
	}
	
	@SmallTest
	public void test_getGetFileUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = filesUrl+"/"+fileId;		
		String url = provider.getGetFileUrl(fileId);
		
		assertEquals("Get file url is wrong", expectedUrl, url);
	}
	
	@SmallTest
	public void test_getDeleteFileUrl() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		String expectedUrl = filesUrl+"/"+fileId;		
		String url = provider.getDeleteFileUrl(fileId);
		
		assertEquals("Delete file url is wrong", expectedUrl, url);
	}
}
