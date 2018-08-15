package com.learning.service;

import com.learning.model.User;
import com.learning.util.ThreadUtils;
import java.util.concurrent.CompletableFuture;

public class UserService {

    public static CompletableFuture<User> getUserDetails(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("UserService.getUserDetails() execution");

            ThreadUtils.simulateLongRunningJob(3);

            return getUser(userId);
        });
    }

    private static User getUser(long userId) {
        return new User(userId, 100.0, 10000.00);
    }
}
