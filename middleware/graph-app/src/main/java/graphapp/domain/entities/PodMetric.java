package graphapp.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.ArrayList;
import java.util.Objects;

@NodeEntity(label="PodMetric")
public class PodMetric extends GraphNode  {

    @Property(name="time")
    private long time;

    @Property(name="value")
    private double value;

    @Property(name="abnormality")
    private double abnormality = 0.1;

    @Property(name="historyTimestamps")
    private ArrayList<Long> historyTimestamps = new ArrayList<>();

    @Property(name="historyValues")
    private ArrayList<Double> historyValues = new ArrayList<>();

    @Property(name="historyAbnormality")
    private ArrayList<Double> historyAbnormality = new ArrayList<>();

    public PodMetric() {
        super();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getAbnormality() {
        return abnormality;
    }

    public void setAbnormality(double abnormality) {
        this.abnormality = abnormality;
    }

    public ArrayList<Long> getHistoryTimestamps() {
        return historyTimestamps;
    }

    public void setHistoryTimestamps(ArrayList<Long> historyTimestamps) {
        this.historyTimestamps = historyTimestamps;
    }

    public ArrayList<Double> getHistoryValues() {
        return historyValues;
    }

    public void setHistoryValues(ArrayList<Double> historyValues) {
        this.historyValues = historyValues;
    }

    public ArrayList<Double> getHistoryAbnormality() {
        return historyAbnormality;
    }

    public void setHistoryAbnormality(ArrayList<Double> historyAbnormality) {
        this.historyAbnormality = historyAbnormality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PodMetric)) return false;
        if (!super.equals(o)) return false;
        PodMetric podMetric = (PodMetric) o;
        return time == podMetric.time &&
                Double.compare(podMetric.value, value) == 0 &&
                Double.compare(podMetric.abnormality, abnormality) == 0 &&
                Objects.equals(historyTimestamps, podMetric.historyTimestamps) &&
                Objects.equals(historyValues, podMetric.historyValues) &&
                Objects.equals(historyAbnormality, podMetric.historyAbnormality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), time, value, abnormality, historyTimestamps, historyValues, historyAbnormality);
    }
}
