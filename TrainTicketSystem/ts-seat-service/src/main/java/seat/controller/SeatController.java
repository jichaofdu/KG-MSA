package seat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import seat.domain.InjectionResult;
import seat.domain.SeatRequest;
import seat.domain.Ticket;
import seat.service.SeatService;

@RestController
public class SeatController {

    @Autowired
    private SeatService seatService;

    @RequestMapping(path = "/welcome", method = RequestMethod.GET)
    public String home() {
        return "Welcome to [ Seat Service ] !";
    }

    //分配座位
    @CrossOrigin(origins = "*")
    @RequestMapping(value="/seat/getSeat", method= RequestMethod.POST)
    public Ticket create(@RequestBody SeatRequest seatRequest,@RequestHeader HttpHeaders headers){
        return seatService.distributeSeat(seatRequest,headers);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/seat/injection/{status}", method = RequestMethod.GET)
    public InjectionResult injectOrNot(@PathVariable String status){
        return seatService.injectOrNot(status);
    }

    //查询特定区间余票
    @CrossOrigin(origins = "*")
    @RequestMapping(value="/seat/getLeftTicketOfInterval", method= RequestMethod.POST)
    public int getLeftTicketOfInterval(@RequestBody SeatRequest seatRequest,@RequestHeader HttpHeaders headers){
        return seatService.getLeftTicketOfInterval(seatRequest,headers);
    }
}
