package com.driver.controllers;

public class HotelAlreadyPresentException extends RuntimeException {
    public HotelAlreadyPresentException(String failure) {
        super(failure);
    }
}
