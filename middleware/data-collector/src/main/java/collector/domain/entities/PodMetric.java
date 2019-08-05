package collector.domain.entities;

import java.util.ArrayList;

public class PodMetric extends GraphNode  {

    private ArrayList<Integer> values = new ArrayList<>();

    public PodMetric() {
        super();
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public void setValues(ArrayList<Integer> values) {
        this.values = values;
    }
}
