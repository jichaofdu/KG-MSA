package mock.search;

import java.util.Date;

public class QueryInfo {

    private String startingPlace;
    private String endPlace;
    private Date departureTime;

    public QueryInfo(){
        //Default Constructor
    }

    public QueryInfo(String startingPlace, String endPlace, Date departureTime) {
        this.startingPlace = startingPlace;
        this.endPlace = endPlace;
        this.departureTime = departureTime;
    }

    public String getStartingPlace() {
        return startingPlace;
    }

    public void setStartingPlace(String startingPlace) {
        this.startingPlace = startingPlace;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }
}
