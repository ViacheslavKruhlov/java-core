package com.learning.model;

public class User {

    private long id;
    private double creditRating;
    private double money;

    public User(long id, double creditRating, double money) {
        this.id = id;
        this.creditRating = creditRating;
        this.money = money;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(double creditRating) {
        this.creditRating = creditRating;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
