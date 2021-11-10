package cinema.contoller;

import cinema.service.SeatService;
import cinema.service.Statistics;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class SeatsController {

    private SeatService seatService = new SeatService(9,9);
    private int totalSeat = seatService.getAvailable_seats().size();
    private Statistics statistics = new Statistics(0, totalSeat, 0);

    @GetMapping("/seats")
    public SeatService getSeats() {
        return seatService;
    }

    @PostMapping("/purchase")
    public ResponseEntity<Object> purchaseSeat(@RequestBody Map<String, Integer> seatToBePurchased) {
        if (seatToBePurchased.get("row") > seatService.getTotal_rows() || seatToBePurchased.get("row") <= 0 ||
                seatToBePurchased.get("column") > seatService.getTotal_columns() || seatToBePurchased.get("column") <= 0 ) {
            return new ResponseEntity<>(Map.of("error", "The number of a row or a column is out of bounds!"), HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> seat = (Map<String, Object>) seatService.purchaseSeat(seatToBePurchased);
        if (seat == null) {
            return new ResponseEntity<>(Map.of("error", "The ticket has been already purchased!"), HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> ticket = (Map<String, Object>) seat.get("ticket");
        int price = (int) ticket.get("price");
        statistics.setCurrent_income(statistics.getCurrent_income() + price);
        statistics.setNumber_of_available_seats(statistics.getNumber_of_available_seats() - 1);
        statistics.setNumber_of_purchased_tickets(statistics.getNumber_of_purchased_tickets() + 1);
        return new ResponseEntity<>(seat, HttpStatus.OK);
    }

    @PostMapping("/return")
    public ResponseEntity<Object> returnTicket(@RequestBody Map<String, String> token) {
        Map<String, Object> returnedTicket = (Map<String, Object>) seatService.returnTicket(token);
        if (returnedTicket == null) {
            return new ResponseEntity<>(Map.of("error", "Wrong token!"), HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> ticket = (Map<String, Object>) returnedTicket.get("returned_ticket");
        int price = (int) ticket.get("price");
        statistics.setCurrent_income(statistics.getCurrent_income() - price);
        statistics.setNumber_of_available_seats(statistics.getNumber_of_available_seats() + 1);
        statistics.setNumber_of_purchased_tickets(statistics.getNumber_of_purchased_tickets() - 1);
        return new ResponseEntity<>(returnedTicket, HttpStatus.OK);
    }

    @PostMapping("/stats")
    public ResponseEntity<Object> theaterStats(@RequestParam(required = false) String password) {
        if (password == null) {
            return new ResponseEntity<>(Map.of("error", "The password is wrong!"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}
