package collector.domain.entities;

import java.util.ArrayList;

public class PodMetric extends GraphNode  {

    private long time;

    private double value;

    private double abnormality = 0.1;

    private double severity;

    private ArrayList<Long> historyTimestamps = new ArrayList<>();

    private ArrayList<Double> historyValues = new ArrayList<>();

    private ArrayList<Double> historyAbnormality = new ArrayList<>();

    private ArrayList<Double> historySeverity = new ArrayList<>();

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
}
