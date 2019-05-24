package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.ArrayList;

@NodeEntity(label="Metric")
public class Metric extends GraphNode{

    @Property(name="value")
    private String value;

    @Property(name="time")
    private String time;

    @Property(name="historyTimestamps")
    private ArrayList<String> historyTimestamps = new ArrayList<>();

    @Property(name="historyValues")
    private ArrayList<String> historyValues = new ArrayList<>();

    public Metric() {
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
