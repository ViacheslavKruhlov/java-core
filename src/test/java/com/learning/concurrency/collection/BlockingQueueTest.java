package com.learning.concurrency.collection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Better to use `put()` and `take()` instead of `add()`, `remove()` and `element()` because the second are throws
 * exception. In a multithreaded programs need to use `offer()`, `poll()` and `peek()`.
 */
public class BlockingQueueTest {

    private static final int FILE_QUEUE_SIZE = 10;
    private static final int SEARCH_THREADS = 100;
    private static final File DUMMY = new File(""); // signal that no more file available and program can be terminated
    private BlockingQueue<File> filesQueue = new ArrayBlockingQueue<>(FILE_QUEUE_SIZE);

    /**
     * Program that read each file in separate thread and find there a keyword.
     */
    @Test
    public void testBlockingQueue() throws InterruptedException {
        // fill the queue
        new Thread(() -> {
            try {
                enumerate(new File("src/test/resources/concurrent_package_files"));
                filesQueue.put(DUMMY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // search the keyword
        String keyword = "volatile";
        for (int i = 1; i <= SEARCH_THREADS; ++i) {
            new Thread(() -> {
                try {
                    boolean isFinished = false;
                    while (!isFinished) {
                        File file = filesQueue.take();
                        if (file == DUMMY) {
                            isFinished = true;
                        } else {
                            search(file, keyword);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * Recursively enumerates all files in a given directory and its subdirectories.
     * @param directory the directory in which to start
     */
    private void enumerate(final File directory) throws InterruptedException {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                enumerate(file);
            } else {
                filesQueue.put(file);
            }
        }
    }

    /**
     * Searches a file for a given keyword and prints all matching lines.
     * @param file the file to search
     * @param keyword the keyword to search for
     */
    private void search(File file, String keyword) {
        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            int lineNumber = 0;

            while (scanner.hasNextLine()) {
                ++lineNumber;
                String line = scanner.nextLine();

                if (line.contains(keyword)) {
                    System.out.printf("%s:%d:%s%n", file.getPath(), lineNumber, line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
