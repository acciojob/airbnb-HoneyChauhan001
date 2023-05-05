package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HotelRepository {

    private Map<String, Hotel> hotelMap;
    private Map<Integer,User> userMap;
    private Map<String,Booking> bookingMap;
    private Map<Integer,List<String>> userBookingMap;

    public HotelRepository() {
        this.hotelMap = new HashMap<>();
        this.userMap = new HashMap<>();
        this.bookingMap = new HashMap<>();
        this.userBookingMap = new HashMap<>();
    }

    public Optional<Hotel> getHotelByHotelName(String hotelName) {
        if(hotelMap.containsKey(hotelName)){
            return Optional.of(hotelMap.get(hotelName));
        }
        else return Optional.empty();
    }

    public void addHotel(Hotel hotel) {
        hotelMap.put(hotel.getHotelName(),hotel);
    }

    public void addUser(User user) {
        userMap.put(user.getaadharCardNo(),user);
    }

    public List<String> getHotelList() {
        return new ArrayList<>(hotelMap.keySet());
    }

    public void addBooking(Booking booking) {
        bookingMap.put(booking.getBookingId(),booking);

    }

    public Optional<User> getUserByAadharCard(Integer aadharCard) {
        if(userMap.containsKey(aadharCard)){
            return Optional.of(userMap.get(aadharCard));
        }
        return Optional.empty();
    }

    public void addUserBooking(int bookingAadharCard, String bookingId) {
        if(userBookingMap.containsKey(bookingAadharCard)){
            List<String> oldList = userBookingMap.get(bookingAadharCard);
            oldList.add(bookingId);
            userBookingMap.put(bookingAadharCard,oldList);
        }
        else {
            List<String> newList = new ArrayList<>();
            newList.add(bookingId);
            userBookingMap.put(bookingAadharCard,newList);
        }
    }

    public List<String> getBookingsByAadharcard(Integer aadharCard) {
        if(userBookingMap.containsKey(aadharCard)){
            return userBookingMap.get(aadharCard);
        }
        else return new ArrayList<>();
    }
}
