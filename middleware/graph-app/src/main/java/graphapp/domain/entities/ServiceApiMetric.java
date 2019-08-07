package graphapp.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import java.util.ArrayList;

@NodeEntity(label="ServiceApiMetric")
public class ServiceApiMetric extends GraphNode {


    @Property(name="values")
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
