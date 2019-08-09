package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.ArrayList;

@NodeEntity(label="ServiceApiMetric")
public class ServiceApiMetric extends GraphNode {


    @Property(name="values")
    private ArrayList<Double> values = new ArrayList<>();

    @Property(name="abnormality")
    private double abnormality;

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
