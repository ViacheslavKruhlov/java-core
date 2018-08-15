package com.learning.concurrency;

import com.learning.model.User;
import com.learning.service.CreditService;
import com.learning.service.UserService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CompletableFutureTest {

    /**
     * The get() method blocks continuous code execution until the Future is complete. So, the following call will block
     * forever because the* Future is never completed and the TimeoutException will be thrown. The code execution will
     * be stuck on get() method.
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

    /**
     * CompletableFuture.runAsync(Runnable) - static method that run some background task asynchronously and don’t
     * return anything from the task. By default tasks are executing into ForkJoinPool.commonPool().
     */
    @Test
    public void testGivenCompletableFuture_WhenCallRunAsyncMethod_ThenTaskCompletedButNothingReturn()
            throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            simulateLongRunningJob(2);

            System.out.println(Thread.currentThread().getName());
        });

        // Block and wait for the future to complete
        completableFuture.get();
    }

    /**
     * CompletableFuture.supplyAsync(Supplier<U>) - static method that run some background task asynchronously and
     * return some result from the task. By default tasks are executing into ForkJoinPool.commonPool().
     */
    @Test
    public void testGivenCompletableFuture_WhenCallSupplyAsyncMethod_ThenTaskCompletedAndReturnExpectedResult()
            throws ExecutionException, InterruptedException {
        String expectedResult = "The task execution result";

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            simulateLongRunningJob(2);

            return expectedResult;
        });

        // Block and wait for the future to complete
        final String actualResult = completableFuture.get();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    /**
     * Run task into custom thread pool.
     */
    @Test
    public void testGivenCompletableFuture_WhenCallSupplyAsyncMethodIntoCustomThreadPool_ThenTaskCompletedAndReturnExpectedResult()
            throws ExecutionException, InterruptedException {
        String expectedResult = "The task execution result";
        final ExecutorService executorService = Executors.newFixedThreadPool(5);

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            simulateLongRunningJob(2);

            return expectedResult;
        }, executorService);

        // Block and wait for the future to complete
        final String actualResult = completableFuture.get();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    /**
     * thenApply(Function<T, R>) - callback method for processing and transformation the result of a CompletableFuture
     * when it arrives. All callback methods execute in the same thread that CompletableFuture or in the main thread.
     * The difference between async callback methods that async methods will be executed into another thread, but
     * execution chain and the result will be similar.
     */
    @Test
    public void testGivenCompletableFuture_WhenTaskComplete_ThenTransformTheResult()
            throws ExecutionException, InterruptedException {
        int customAge = 18;
        String expectedResult = String.format("How old are you? - I'm %d years old.", customAge);

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());

            simulateLongRunningJob(2);

            return customAge;
        })
                .thenApplyAsync(age -> {
                    System.out.println(Thread.currentThread().getName());

                    return String.format("I'm %d years old.", age);
                }, Executors.newFixedThreadPool(5))
                .thenApply(answer -> {
                    System.out.println(Thread.currentThread().getName());

                    return "How old are you? - " + answer;
                });

        // Block and wait for the future to complete
        final String actualResult = completableFuture.get();

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    /**
     * thenAccept(Consumer<? super T>) - callback method for processing the result of a CompletableFuture
     * when it arrives but don't return anything.
     */
    @Test
    public void testGivenCompletableFuture_WhenTaskComplete_ThenUseTheResultAndDoNotReturnAnyResult()
            throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
            simulateLongRunningJob(2);

            return 18;
        })
                .thenAccept(age -> System.out.println(String.format("I'm %d years old.", age)));

        // Block and wait for the future to complete
        completableFuture.get();
    }

    /**
     * thenRun(Runnable) - callback method that doesn't use CompletableFuture result, run separate task after
     * CompletableFuture complete and don't return anything.
     */
    @Test
    public void testGivenCompletableFuture_WhenTaskComplete_ThenAnotherSeparateTask()
            throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
            simulateLongRunningJob(2);

            return 18;
        })
                .thenRun(() -> System.out.println("Another task was started"));

        // Block and wait for the future to complete
        completableFuture.get();
    }

    /**
     * thenCompose(Function<? super T, ? extends CompletionStage<U>>) - callback function that returns a
     * CompletableFuture and flattened result from the CompletableFuture chain. All CompletableFutures will be executed
     * consequently.
     */
    @Test
    public void testGivenTwoServiceThatReturnCompletableFuture_WhenOneServiceHasToUseResultFromAnother_ThenTheyHaveToBeComposed()
            throws ExecutionException, InterruptedException {
        CompletableFuture<Double> completableFuture =
                UserService.getUserDetails(1L)
                        .thenCompose(CreditService::getUserCreditRating);

        assertThat(completableFuture.get()).isEqualTo(100.0);
    }

    /**
     * thenCombine(CompletionStage<? extends U>, BiFunction<? super T,? super U,? extends V>) - callback function that
     * to run two Futures independently (at the same time) and do something after both are complete.
     */
    @Test
    public void testGivenTwoServiceThatReturnCompletableFuture_WhenNeedToCalculateMutualResult_ThenTheyHaveToBeCombined()
            throws ExecutionException, InterruptedException {
        CompletableFuture<Double> completableFuture =
                UserService.getUserDetails(1L)
                        .thenCombine(CreditService.getInterestRate(), (user, rate) -> user.getMoney() * rate);

        assertThat(completableFuture.get()).isEqualTo(10000.00 * 13.0);
    }

    /**
     * allOf() - a static method that used in scenarios when you have a List of independent futures that you want to run
     * in parallel and do something after all of them are complete. Pay attention with CompletableFuture.allOf() is that
     * it returns CompletableFuture<Void>, but it's not a problem - see following code.
     */
    @Test
    public void testGivenListOfCompletableFutures_WhenCallAllOfMethod_ThenContinueAfterAllFeaturesComplete()
            throws ExecutionException, InterruptedException {
        List<CompletableFuture<User>> userFutures = LongStream.range(1, 6)
                .mapToObj(UserService::getUserDetails)
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures =
                CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[userFutures.size()]));

        final CompletableFuture<Double> moneySum = allFutures.thenApply(u -> userFutures.stream()
                .map(CompletableFuture::join) // like get(), difference is that it throw exception if Future completes exceptionally
                .mapToDouble(User::getMoney)
                .sum()
        );

        assertThat(moneySum.get()).isEqualTo(5 * 10000.00);
    }

    /**
     * anyOf() - a static method that returns a new CompletableFuture which is completed when any of the given
     * CompletableFutures complete, with the same result.
     */
    @Test
    public void testGivenListOfCompletableFutures_WhenCallAnyOfMethod_ThenContinueAfterOneOfFeaturesComplete()
            throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            simulateLongRunningJob(3);

            return "Result 1";
        });

        final String expectedResult = "Result 2";

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            simulateLongRunningJob(1);

            return expectedResult;
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            simulateLongRunningJob(3);

            return "Result 3";
        });

        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(future1, future2, future3);

        assertThat(anyOfFuture.get()).isEqualTo(expectedResult);
    }
    
    private void simulateLongRunningJob(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
