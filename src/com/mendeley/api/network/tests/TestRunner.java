package com.mendeley.api.network.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.mendeley.api.network.FileNetworkProvider;
import com.mendeley.api.network.FolderNetworkProvider;

public class TestRunner {
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(NetworkConnectionTest.class);		
//		for (Failure failure : result.getFailures()) {
//			System.out.println("Tests error: "+failure.toString() + "\n"+failure.getTrace());
//		}
		System.out.println("Network connection passed: "+result.wasSuccessful());
		
		result = JUnitCore.runClasses(JsonParserTest.class);		
		System.out.println("JsonParser passed: "+result.wasSuccessful());
	
		result = JUnitCore.runClasses(NetworkProviderTest.class);		
		System.out.println("NetworkProvider passed: "+result.wasSuccessful());
		
		result = JUnitCore.runClasses(DocumentNetworkProviderTest.class);		
		System.out.println("DocumentNetworkProvider passed: "+result.wasSuccessful());
		
		result = JUnitCore.runClasses(FileNetworkProviderTest.class);		
		System.out.println("FileNetworkProvider passed: "+result.wasSuccessful());
		
		result = JUnitCore.runClasses(FolderNetworkProviderTest.class);		
		System.out.println("FolderNetworkProvider passed: "+result.wasSuccessful());
	}
}  