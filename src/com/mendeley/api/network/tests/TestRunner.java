package com.mendeley.api.network.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Run the tests.
 * <p>
 * Usage: java -cp bin/classes.jar:libs/* com.mendeley.api.network.tests.TestRunner [-v]
 */
public class TestRunner {
	private static String VERBOSE_FLAG = "-v";
	
	private boolean verbose;
	
	public static void main(String[] args) {
		// Parse args
		boolean verbose = false;
		for (String arg : args) {
			if (arg.equals(VERBOSE_FLAG)) {
				verbose = true;
			}
		}
		// Run tests
		new TestRunner(verbose);
	}
	
	public TestRunner(boolean verbose) {
		this.verbose = verbose;
		doTests();
	}
	
	private void doTests() {
		runTest(NetworkConnectionTest.class);
		runTest(JsonParserTest.class);
		runTest(NetworkProviderTest.class);
		runTest(DocumentNetworkProviderTest.class);
		runTest(FileNetworkProviderTest.class);
		runTest(FolderNetworkProviderTest.class);		
	}
	
	private void runTest(Class<?> testClass) {
		Result result = JUnitCore.runClasses(testClass);
		if (verbose) {
			for (Failure failure : result.getFailures()) {
				System.out.println("Tests error: " + failure.toString() + "\n" + failure.getTrace());
			}
		}
		System.out.println(testClass.getSimpleName() + (result.wasSuccessful() ? " passed" : " FAILED"));
	}
}  