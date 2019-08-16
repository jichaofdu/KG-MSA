package graphapp.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import java.util.ArrayList;
import java.util.Objects;

@NodeEntity(label="Metric")
public class Metric extends GraphNode{

    @Property(name="time")
    private long time;

    @Property(name="value")
    private double value;

    @Property(name="abnormality")
    private double abnormality;

    @Property(name="severity")
    private double severity;

    @Property(name="historyTimestamps")
    private ArrayList<Long> historyTimestamps = new ArrayList<>();

    @Property(name="historyValues")
    private ArrayList<Double> historyValues = new ArrayList<>();

    @Property(name="historyAbnormality")
    private ArrayList<Double> historyAbnormality = new ArrayList<>();

    @Property(name="historySeverity")
    private ArrayList<Double> historySeverity = new ArrayList<>();

    public Metric() {
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

    public double getSeverity() {
        return severity;
    }

    public void setSeverity(double severity) {
        this.severity = severity;
    }

    public ArrayList<Double> getHistorySeverity() {
        return historySeverity;
    }

    public void setHistorySeverity(ArrayList<Double> historySeverity) {
        this.historySeverity = historySeverity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Metric)) return false;
        if (!super.equals(o)) return false;
        Metric metric = (Metric) o;
        return time == metric.time &&
                Double.compare(metric.value, value) == 0 &&
                Double.compare(metric.abnormality, abnormality) == 0 &&
                Objects.equals(historyTimestamps, metric.historyTimestamps) &&
                Objects.equals(historyValues, metric.historyValues) &&
                Objects.equals(historyAbnormality, metric.historyAbnormality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), time, value, abnormality, historyTimestamps, historyValues, historyAbnormality);
    }
}
