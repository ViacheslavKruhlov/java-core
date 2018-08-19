package com.learning.concurrency;

import com.learning.util.ThreadUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.Before;
import org.junit.Test;

public class SynchronizedTest {

    private int[] amount;

    @Before
    public void setUp() {
        amount = new int[1];
    }

    @Test
    public void testSynchronizedWaitNotifyAll() throws InterruptedException {
        for (int i = 1; i < 6; ++i) {
            final int number = i;
            amount[0] = i;

            new Thread(() -> {
                try {
                    testSynchronizedConditions(number);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            ThreadUtils.simulateLongRunningJob(1);
        }

        TimeUnit.SECONDS.sleep(10);
    }

    private void testSynchronizedConditions(int number) throws InterruptedException {
        System.out.printf("The thread #%d is processing\n", number);
        synchronized (this) {
            while (amount[0] < 5) {
                System.out.printf("The thread #%d is waiting\n", number);
                this.wait();
            }

            System.out.printf("The thread #%d is continue processing\n", number);
            ThreadUtils.simulateLongRunningJob(1);

            // Here notify() method is present too. If the randomly chosen thread finds that it still cannot proceed, it
            // becomes blocked again. If no other thread calls signal again, then the system deadlock
            this.notifyAll();
        }
    }
}
