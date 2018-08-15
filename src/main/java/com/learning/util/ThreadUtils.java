package com.learning.util;

import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    public static void simulateLongRunningJob(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
