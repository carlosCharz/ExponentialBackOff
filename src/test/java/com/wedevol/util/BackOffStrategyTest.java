package com.wedevol.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the backoff strategy
 */

public class BackOffStrategyTest {

	private static final int SUCCESSFUL_ATTEMPT = 1;
	public static final int DEFAULT_RETRIES = 2;
	private static final long DEFAULT_WAIT_TIME_IN_MILLI = 500;
	private static BackEndSimulator backend;

	private BackOffStrategy backoff;

	// Run once, e.g. Database connection, connection pool
	@BeforeClass
	public static void runOnceBeforeClass() {
		backend = new BackEndSimulator();
	}

	// Run once, e.g close connection, cleanup
	@AfterClass
	public static void runOnceAfterClass() {
	}

	// e.g. Creating a similar object and share for all @Test
	@Before
	public void setUp() {
		backoff = new BackOffStrategy(DEFAULT_RETRIES, DEFAULT_WAIT_TIME_IN_MILLI);
	}

	// Should rename to @AfterTestMethod
	@After
	public void tearDown() {
	}

	@Test
	public void testSuccessfulRegistration() throws Exception {
		Boolean flag = backend.successfulRegistrationToDataBase();
		assertEquals("Successful call", true, flag);
	}

	@Test
	public void testSuccessfulCallNotContinueLooping() throws Exception {
		int counter = 0;
		while (backoff.shouldRetry()) {
			try {
				counter++;
				backend.successfulRegistrationToDataBase();
				backoff.doNotRetry();
			} catch (Exception e) {
				backoff.errorOccured();
			}
		}
		assertEquals("Successfull attempt", SUCCESSFUL_ATTEMPT, counter);
	}

	@Test
	public void testMaximumUnsuccessfulCallsWhenErrorWithExceptionType() {
		int counter = 0;
		while (backoff.shouldRetry()) {
			try {
				counter++;
				backend.unsuccessfulRegistrationToDataBase();
			} catch (Exception e) {
				try {
					backoff.errorOccured2();
				} catch (Exception e2) {
				}
			}
		}
		assertEquals("Unsuccessful calls", DEFAULT_RETRIES, counter);
	}

	@Test
	public void testMaximumUnsuccessfulCalls() {
		int counter = 0;
		while (backoff.shouldRetry()) {
			try {
				counter++;
				backend.unsuccessfulRegistrationToDataBase();
			} catch (Exception e) {
				backoff.errorOccured();
			}
		}
		assertEquals("Unsuccessful calls", DEFAULT_RETRIES, counter);
	}

	@Test
	public void testTimeIncreaseDuringUnsuccessfulCalls() {
		long prev = backoff.getTimeToWait();
		assertEquals("Default time is 1 seg", 500, prev);
		while (backoff.shouldRetry()) {
			try {
				backend.unsuccessfulRegistrationToDataBase();
			} catch (Exception e) {
				backoff.errorOccured();
				assertTrue("Time is increasing exponentially", backoff.getTimeToWait() > prev);
			}
		}
	}

	@Test(expected = Exception.class)
	public void testExceptionIsThrownWhenReachedMaximumUnsuccessfulCalls() throws Exception {
		while (backoff.shouldRetry()) {
			try {
				backend.unsuccessfulRegistrationToDataBase();
			} catch (Exception e) {
				try {
					System.out.println("Error!");
					backoff.errorOccured2();
				} catch (Exception e2) {
					System.out.println(e2);
					throw e2;
				}
			}
		}
	}

}

/**
 * Back end simulator class used for testing purposes
 */

class BackEndSimulator {

	public boolean successfulRegistrationToDataBase() throws Exception {
		return true;
	}

	public boolean unsuccessfulRegistrationToDataBase() throws Exception {
		throw new Exception("Something occured in the database");
	}

}
