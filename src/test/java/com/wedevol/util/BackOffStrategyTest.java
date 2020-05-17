package com.wedevol.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    @BeforeAll
    public static void initAll() {
        backend = new BackEndSimulator();
    }

    // Run once, e.g close connection, cleanup
    @AfterAll
    public static void tearDownAll() {}

    // e.g. Creating a similar object and share for all @Test
    @BeforeEach
    public void setUp() {
        backoff = new BackOffStrategy(DEFAULT_RETRIES, DEFAULT_WAIT_TIME_IN_MILLI);
    }

    @AfterEach
    public void tearDown() {}

    @Test
    @DisplayName("Test successful registration")
    public void testSuccessfulRegistration() throws Exception {
        Boolean flag = backend.successfulRegistrationToDataBase();
        Assertions.assertTrue(flag, "Successful call");
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
        Assertions.assertEquals(SUCCESSFUL_ATTEMPT, counter, "Successfull attempt");
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
        Assertions.assertEquals(DEFAULT_RETRIES, counter, "Unsuccessful calls");
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
        Assertions.assertEquals(DEFAULT_RETRIES, counter, "Unsuccessful calls");
    }

    @Test
    public void testTimeIncreaseDuringUnsuccessfulCalls() {
        long prev = backoff.getTimeToWait();
        Assertions.assertEquals(500, prev, "Default time is 1 seg");
        while (backoff.shouldRetry()) {
            try {
                backend.unsuccessfulRegistrationToDataBase();
            } catch (Exception e) {
                backoff.errorOccured();
                Assertions.assertTrue(backoff.getTimeToWait() > prev, "Time is increasing exponentially");
            }
        }
    }

    @Test
    public void testExceptionIsThrownWhenReachedMaximumUnsuccessfulCalls() throws Exception {
        Assertions.assertThrows(Exception.class, () -> {
            while (backoff.shouldRetry()) {
                try {
                    backend.unsuccessfulRegistrationToDataBase();
                } catch (Exception e) {
                    System.out.println("Error!");
                    backoff.errorOccured2();
                }
            }
        });
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
