package graphapp.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import java.util.ArrayList;
import java.util.Objects;

@NodeEntity(label="ServiceApiMetric")
public class ServiceApiMetric extends GraphNode {


    @Property(name="values")
    private ArrayList<Double> values = new ArrayList<>();

    @Property(name="abnormality")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceApiMetric)) return false;
        if (!super.equals(o)) return false;
        ServiceApiMetric that = (ServiceApiMetric) o;
        return Double.compare(that.abnormality, abnormality) == 0 &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), values, abnormality);
    }
}
