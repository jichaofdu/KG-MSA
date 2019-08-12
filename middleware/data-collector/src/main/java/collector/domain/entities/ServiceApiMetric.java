package collector.domain.entities;

import java.util.ArrayList;

public class ServiceApiMetric extends GraphNode {

    private ArrayList<Double> values = new ArrayList<>();

    private double abnormality = 0.1;

    public ServiceApiMetric() {
        super();
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public double getAbnormality() {
        return abnormality;
    }

    public void setAbnormality(double abnormality) {
        this.abnormality = abnormality;
    }
}
