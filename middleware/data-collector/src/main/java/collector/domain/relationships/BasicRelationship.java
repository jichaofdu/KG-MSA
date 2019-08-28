package collector.domain.relationships;

import java.util.Objects;

public class BasicRelationship {

    private String id;

    private String relation;

    private String className = this.getClass().toString();

    public BasicRelationship() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicRelationship)) return false;
        BasicRelationship that = (BasicRelationship) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(relation, that.relation) &&
                Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, relation, className);
    }
}
