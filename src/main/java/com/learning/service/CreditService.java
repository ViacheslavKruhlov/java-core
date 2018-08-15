package com.learning.service;

import com.learning.model.User;
import com.learning.util.ThreadUtils;
import java.util.concurrent.CompletableFuture;

public class CreditService {

    public static CompletableFuture<Double> getUserCreditRating(User user) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("CreditService.getUserCreditRating() execution");

            ThreadUtils.simulateLongRunningJob(3);

            return getCreditRating(user);
        });
    }

    public static CompletableFuture<Double> getInterestRate() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("CreditService.getInterestRate() execution");

            ThreadUtils.simulateLongRunningJob(3);

            return 13.0;
        });
    }

    private static double getCreditRating(final User user) {
        return user.getCreditRating();
    }
}
