package cinema.service;

import org.springframework.cache.support.NullValue;
import org.springframework.stereotype.Service;

import java.util.*;

public class SeatService {

    private int total_rows;
    private int total_columns;
    private ArrayList<Map<String, Object>> available_seats;

    public SeatService(int total_rows, int total_columns) {
        this.total_rows = total_rows;
        this.total_columns = total_columns;
        available_seats = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Map<String, Object> seat = new HashMap<>();
                seat.put("row", i + 1);
                seat.put("column", j + 1);
                if (i + 1 <= 4) {
                    seat.put("price", 10);
                } else {
                    seat.put("price", 8);
                }
                available_seats.add(seat);
            }
        }
    }

    public int getTotal_rows() {
        return total_rows;
    }

    public int getTotal_columns() {
        return total_columns;
    }

    public ArrayList<Map<String, Object>> getAvailable_seats() {
        return available_seats;
    }

    public void setTotal_rows(int total_rows) {
        this.total_rows = total_rows;
    }

    public void setTotal_columns(int total_columns) {
        this.total_columns = total_columns;
    }

    public void setAvailable_seats(ArrayList<Map<String, Object>> availableSeats) {
        this.available_seats = availableSeats;
    }

    public Object purchaseSeat(Map<String, Integer> seatToBePurchased) {
        for (int i = 0; i < available_seats.size(); i++)  {
            Map<String, Object> seat = available_seats.get(i);
            if (Objects.equals(seatToBePurchased.get("row"), seat.get("row")) &&
                    Objects.equals(seatToBePurchased.get("column"), seat.get("column"))) {
                available_seats.remove(seat);
                Map<String, Object> newSeat = new HashMap<>();
                String id = UUID.randomUUID().toString();
                newSeat.put("token", id);
                newSeat.put("ticket", seat);
                available_seats.add(newSeat);
                return newSeat;
            }
        }
        return null;
    }

    public Object returnTicket(Map<String, String> token) {
        for (int i = 0; i < available_seats.size(); i++)  {
            Map<String, Object> seat = available_seats.get(i);
            if (seat.containsKey("token") && Objects.equals(seat.get("token"), token.get("token"))) {
                Map<String, Object> ticket = new HashMap<>();
                ticket.put("returned_ticket", seat.get("ticket"));
                available_seats.remove(seat);
                return ticket;
            }
        }
        return null;
    }
}
