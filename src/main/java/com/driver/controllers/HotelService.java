package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Priority;
import java.util.*;

@Service
public class HotelService {


    @Autowired
    HotelRepository hotelRepository;

    public HotelService() {
    }

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public void addHotel(Hotel hotel) {
        //You need to add an hotel to the database
        //incase the hotelName is null or the hotel Object is null return an empty a FAILURE
        //Incase somebody is trying to add the duplicate hotelName return FAILURE
        //in all other cases return SUCCESS after successfully adding the hotel to the hotelDb.

        if(Objects.isNull(hotel) || Objects.isNull(hotel.getHotelName())){
            throw new EmptyObjectHotelException("FAILURE");
        }
        Optional<Hotel> hotelOpt =  hotelRepository.getHotelByHotelName(hotel.getHotelName());
        if(hotelOpt.isPresent()){
            throw new HotelAlreadyPresentException("FAILURE");
        }
        else {
            hotelRepository.addHotel(hotel);
        }
    }

    public int addUser(User user) {
        hotelRepository.addUser(user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        //Out of all the hotels we have added so far, we need to find the hotelName with most no of facilities
        //Incase there is a tie return the lexicographically smaller hotelName
        //Incase there is not even a single hotel with atleast 1 facility return "" (empty string)

        List<String> hotelList = hotelRepository.getHotelList();
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        for(String hotelName : hotelList){
            Optional<Hotel> hotelOpt = hotelRepository.getHotelByHotelName(hotelName);
            int numberOfFacilities = hotelOpt.get().getFacilities().size();
            pq.add(new Pair(hotelName,numberOfFacilities));
        }

        Pair hotelWithMaxFacilities = pq.remove();
        if(hotelWithMaxFacilities.numberOfFacilities < 1)return "";
        else return hotelWithMaxFacilities.hotelName;

    }

    public int bookARoom(Booking booking) {
        //The booking object coming from postman will have all the attributes except bookingId and amountToBePaid;
        //Have bookingId as a random UUID generated String
        //save the booking Entity and keep the bookingId as a primary key
        //Calculate the total amount paid by the person based on no. of rooms booked and price of the room per night.
        //If there arent enough rooms available in the hotel that we are trying to book return -1
        //in other case return total amount paid

        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        booking.setBookingId(uuidAsString);
        hotelRepository.addBooking(booking);
        hotelRepository.addUserBooking(booking.getBookingAadharCard(),booking.getBookingId());
        Optional<Hotel> hotelOpt = hotelRepository.getHotelByHotelName(booking.getHotelName());
        if(hotelOpt.isPresent()){
            Hotel hotel = hotelOpt.get();
            int roomAvailable = hotel.getAvailableRooms();
            int numberOfRooms = booking.getNoOfRooms();
            if(numberOfRooms>roomAvailable){
                return -1;
            }
            else {
                int pricePerNight = hotel.getPricePerNight();
                int amountToBePaid = pricePerNight * numberOfRooms;
                booking.setAmountToBePaid(amountToBePaid);
                hotel.setAvailableRooms(roomAvailable - numberOfRooms);
                return amountToBePaid;
            }
        }
        else {
            return -1;
        }

    }

    public int getBookingsByAPerson(Integer aadharCard) {
        List<String> bookings = hotelRepository.getBookingsByAadharcard(aadharCard);
        return bookings.size();
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        //return the final updated List of facilities and also update that in your hotelDb
        //Note that newFacilities can also have duplicate facilities possible

        Optional<Hotel> hotelOpt = hotelRepository.getHotelByHotelName(hotelName);
        List<Facility> oldFacilities = hotelOpt.get().getFacilities();
        Set<Facility> hset = new HashSet<>();
        for(Facility facility : newFacilities){
            hset.add(facility);
        }
        for(Facility facility : oldFacilities){
            hset.add(facility);
        }

        hotelOpt.get().setFacilities(new ArrayList<>(hset));
        hotelRepository.addHotel(hotelOpt.get());
        return hotelOpt.get();

    }

    public class Pair implements Comparable<Pair>{
        String hotelName;
        int numberOfFacilities;

        public Pair(String hotelName, int numberOfFacilities) {
            this.hotelName = hotelName;
            this.numberOfFacilities = numberOfFacilities;
        }

        @Override
        public int compareTo(Pair o) {
            if(this.numberOfFacilities == o.numberOfFacilities){
                return this.hotelName.compareTo(o.hotelName);
            }
            else {
                return o.numberOfFacilities - this.numberOfFacilities;
            }
        }
    }
}
