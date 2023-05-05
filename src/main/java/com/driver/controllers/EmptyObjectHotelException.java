package com.driver.controllers;

public class EmptyObjectHotelException extends RuntimeException {
    public EmptyObjectHotelException(String failure) {
        super(failure);
    }
}
