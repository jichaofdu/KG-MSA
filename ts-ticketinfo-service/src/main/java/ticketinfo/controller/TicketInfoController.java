package ticketinfo.controller;

/**
 * Created by Chenjie Xu on 2017/6/6.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import ticketinfo.domain.QueryForStationId;
import ticketinfo.domain.QueryForTravel;
import ticketinfo.domain.ResultForTravel;
import ticketinfo.service.TicketInfoService;

import java.util.ArrayList;

@RestController
public class TicketInfoController {

    @Autowired
    TicketInfoService service;

    @RequestMapping(path = "/welcome", method = RequestMethod.GET)
    public String home() {
        return "Welcome to [ TicketInfo Service ] !";
    }

    @RequestMapping(value="/ticketinfo/queryForTravel", method = RequestMethod.POST)
    public ResultForTravel queryForTravel(@RequestBody QueryForTravel info,@RequestHeader HttpHeaders headers){
        return service.queryForTravel(info,headers);
    }

    @RequestMapping(value="/ticketinfo/queryForTravels", method = RequestMethod.POST)
    public ArrayList<ResultForTravel> queryForTravels(@RequestBody ArrayList<QueryForTravel> infos, @RequestHeader HttpHeaders headers) {
        ArrayList<ResultForTravel> result = new ArrayList<>();
        for(QueryForTravel info : infos){
            result.add(service.queryForTravel(info, headers));
        }
        return result;
    }

    @RequestMapping(value="/ticketinfo/queryForStationId", method = RequestMethod.POST)
    public String queryForStationId(@RequestBody QueryForStationId info,@RequestHeader HttpHeaders headers){
        return service.queryForStationId(info,headers);
    }

    @RequestMapping(value="/ticketinfo/queryForStationIds", method = RequestMethod.POST)
    public ArrayList<String> queryForStationIds(@RequestBody ArrayList<QueryForStationId> infos,@RequestHeader HttpHeaders headers){
        ArrayList<String> result = new ArrayList<>();
        for(QueryForStationId info : infos){
            result.add(service.queryForStationId(info,headers));
        }
        return result;
    }

}
