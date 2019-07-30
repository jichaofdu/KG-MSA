package collector.domain.entities;

import java.util.ArrayList;

public class ServiceApiMetric extends GraphNode {

    private ArrayList<Integer> values = new ArrayList<>();

    public ServiceApiMetric() {
        super();
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public void setValues(ArrayList<Integer> values) {
        this.values = values;
    }
}
