package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.ArrayList;
import java.util.Objects;

@NodeEntity
public class BasicMetric extends GraphNode {

    @Property(name="time")
    private long time;

    @Property(name="value")
    private double value;

    @Property(name="abnormality")
    private double abnormality = 0.1;

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

    public BasicMetric() {
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

    public double getSeverity() {
        return severity;
    }

    public void setSeverity(double severity) {
        this.severity = severity;
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

    public ArrayList<Double> getHistorySeverity() {
        return historySeverity;
    }

    public void setHistorySeverity(ArrayList<Double> historySeverity) {
        this.historySeverity = historySeverity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicMetric)) return false;
        if (!super.equals(o)) return false;
        BasicMetric that = (BasicMetric) o;
        return time == that.time &&
                Double.compare(that.value, value) == 0 &&
                Double.compare(that.abnormality, abnormality) == 0 &&
                Double.compare(that.severity, severity) == 0 &&
                Objects.equals(historyTimestamps, that.historyTimestamps) &&
                Objects.equals(historyValues, that.historyValues) &&
                Objects.equals(historyAbnormality, that.historyAbnormality) &&
                Objects.equals(historySeverity, that.historySeverity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), time, value, abnormality, severity, historyTimestamps, historyValues, historyAbnormality, historySeverity);
    }
}
