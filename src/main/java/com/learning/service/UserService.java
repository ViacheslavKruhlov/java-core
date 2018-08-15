package com.learning.service;

import com.learning.model.User;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UserService {

    public static CompletableFuture<User> getUserDetails(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("UserService.getUserDetails() execution");

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return getUser(userId);
        });
    }

    private static User getUser(long userId) {
        return new User(userId, 100.0, 10000.00);
    }
}
