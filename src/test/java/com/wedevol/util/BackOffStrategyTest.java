package com.wedevol.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import com.wedevol.util.BackOffStrategy;

/**
 * Test for the backoff strategy
 */

public class BackOffStrategyTest {

	public static final int DEFAULT_RETRIES = 3;
	private BackOffStrategy backoff;
	private BackEndSimulator backend;

	// Run once, e.g. Database connection, connection pool
	@BeforeClass
	public static void runOnceBeforeClass() {
	}

	// Run once, e.g close connection, cleanup
	@AfterClass
	public static void runOnceAfterClass() {
	}

	// e.g. Creating a similar object and share for all @Test
	@Before
	public void runBeforeTestMethod() {
		backoff = new BackOffStrategy();
		backend = new BackEndSimulator();
	}

	// Should rename to @AfterTestMethod
	@After
	public void runAfterTestMethod() {
	}

	@Test
	public void testSuccessfulRegistration() throws Exception {
		Boolean flag = backend.successfulRegistrationToDataBase();
		assertEquals("Successful call", true, flag);
	}

	@Test
	public void testMaximumUnsuccessfulCalls() {
		int counter = 0;
		while (backoff.shouldRetry()) {
			try {
				counter++;
				backend.unsuccessfulRegistrationToDataBase();
			} catch (Exception e) {
				try {
					backoff.errorOccured();
				} catch (Exception e2) {
				}
			}
		}
		assertEquals("Unsuccessful calls", DEFAULT_RETRIES, counter);
	}

	@Test
	public void testTimeIncreaseDuringUnsuccessfulCalls() {
		backoff.reset();
		long prev = backoff.getTimeToWait();
		assertEquals("Default time is 1 seg", 1000, prev);
		while (backoff.shouldRetry()) {
			try {
				backend.unsuccessfulRegistrationToDataBase();
			} catch (Exception e) {
				try {
					backoff.errorOccured();
					assertTrue("Time is increasing exponentially", backoff.getTimeToWait() > prev);
				} catch (Exception e2) {
				}
			}
		}
	}

	@Test(expected = Exception.class)
	public void testExceptionIsThrownWhenReachedMaximumUnsuccessfulCalls() throws Exception {
		backoff.reset();
		while (backoff.shouldRetry()) {
			try {
				backend.unsuccessfulRegistrationToDataBase();
			} catch (Exception e) {
				try {
					backoff.errorOccured();
				} catch (Exception e2) {
					System.out.println(e2);
					throw e2;
				}
			}
		}
	}

}

/**
 * Back off simulator class used for testing purposes
 */

class BackEndSimulator {

	public boolean successfulRegistrationToDataBase() throws Exception {
		return true;
	}

	public boolean unsuccessfulRegistrationToDataBase() throws Exception {
		throw new Exception("Something occured in the database");
	}

}
