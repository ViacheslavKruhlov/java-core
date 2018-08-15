package com.learning.service;

import com.learning.model.User;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CreditService {

    public static CompletableFuture<Double> getUserCreditRating(User user) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("CreditService.getUserCreditRating() execution");

            simulateLongRunningJob();

            return getCreditRating(user);
        });
    }

    public static CompletableFuture<Double> getInterestRate() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("CreditService.getInterestRate() execution");

            simulateLongRunningJob();

            return 13.0;
        });
    }

    private static double getCreditRating(final User user) {
        return user.getCreditRating();
    }

    private static void simulateLongRunningJob() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
