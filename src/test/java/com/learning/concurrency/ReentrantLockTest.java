package com.learning.concurrency;

import com.learning.util.ThreadUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.Before;
import org.junit.Test;

public class ReentrantLockTest {

    private Lock reentrantLock;
    private Condition conditionObject;
    private int[] amount;

    @Before
    public void setUp() {
        reentrantLock = new ReentrantLock();
        conditionObject = reentrantLock.newCondition();
        amount = new int[1];
    }

    @Test
    public void testReentrantLockThreadLock() throws InterruptedException {
        new Thread(this::testLockThread).start();

        new Thread(this::testLockThread).start();

        TimeUnit.SECONDS.sleep(8);
    }

    private void testLockThread() {
        reentrantLock.lock();
        try {
            System.out.println("The thread locked");
            ThreadUtils.simulateLongRunningJob(3);
        } finally {
            reentrantLock.unlock(); // the good practice to unlock thread into finally block
            System.out.println("The thread unlocked");
        }
    }

    @Test
    public void testLockBasedOnConditionObject() throws InterruptedException {
        for (int i = 1; i < 6; ++i) {
            final int number = i;
            amount[0] = i;

            new Thread(() -> {
                try {
                    testConditionObject(number);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            ThreadUtils.simulateLongRunningJob(1);
        }

        TimeUnit.SECONDS.sleep(10);
    }

    private void testConditionObject(int number) throws InterruptedException {
        System.out.printf("The thread #%d is processing\n", number);
        reentrantLock.lock();
        try {
            while (amount[0] < 5) {
                System.out.printf("The thread #%d is awaiting\n", number);
                conditionObject.await();
            }

            System.out.printf("The thread #%d is continue processing\n", number);
            ThreadUtils.simulateLongRunningJob(1);

            // Here signal() method is present too. If the randomly chosen thread finds that it still cannot proceed, it
            // becomes blocked again. If no other thread calls signal again, then the system deadlock
            conditionObject.signalAll();
        } finally {
            reentrantLock.unlock();
        }
    }
}
