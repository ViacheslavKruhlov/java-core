package com.learning.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CompletableFutureTest {

    /**
     * The get() method blocks until the Future is complete. So, the following call will block forever because the
     * Future is never completed and the TimeoutException will be thrown. The code execution will be stuck on get()
     * method.
     */
    @Test
    public void testGivenCompletableFutureWithoutTask_WhenTryGetFutureResult_ThenThrowTimeoutException() {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        assertThatThrownBy(() -> completableFuture.get(5, TimeUnit.SECONDS))
                .isInstanceOf(TimeoutException.class);
    }


    /**
     * Manually complete a Future with specified result. All the clients waiting for this Future will get the specified
     * result. Subsequent calls to completableFuture.complete() will be ignored.
     */
    @Test
    public void testGivenCompletableFutureWithoutTask_WhenManuallyComplete_ThenGetSpecifiedResult()
            throws ExecutionException, InterruptedException {
        String result = "Result";
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        completableFuture.complete(result);

        assertThat(completableFuture.get()).isEqualTo(result);
    }
}
