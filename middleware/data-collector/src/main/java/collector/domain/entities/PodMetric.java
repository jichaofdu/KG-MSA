package collector.domain.entities;

import java.util.ArrayList;

public class PodMetric extends GraphNode  {

    private String value;

    private String time;

    private ArrayList<String> historyTimestamps = new ArrayList<>();

    private ArrayList<String> historyValues = new ArrayList<>();

    public PodMetric() {
        super();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<String> getHistoryTimestamps() {
        return historyTimestamps;
    }

    public void setHistoryTimestamps(ArrayList<String> historyTimestamps) {
        this.historyTimestamps = historyTimestamps;
    }

    public ArrayList<String> getHistoryValues() {
        return historyValues;
    }

    public void setHistoryValues(ArrayList<String> historyValues) {
        this.historyValues = historyValues;
    }
}
