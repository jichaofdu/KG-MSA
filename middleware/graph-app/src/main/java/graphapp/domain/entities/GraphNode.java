package graphapp.domain.entities;

import org.neo4j.ogm.annotation.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 是所有Entity节点的父类
 * 注意：Property请和成员变量名保持严格一致
 */
@NodeEntity
public class GraphNode {

    @Id
    private String id;

    @Property(name="name")
    private String name;

    @Property(name="className")
    private String className = this.getClass().getName();

    @Labels
    private Set<String> labels = new HashSet<>();

    @Property(name="latestUpdateTimestamp")
    private String latestUpdateTimestamp;

    @Property(name="creationTimestamp")
    private String creationTimestamp;

    @Property(name="score")
    private double score;

    public GraphNode() {
    }

    public GraphNode(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public void addLabel(String name) {
        this.labels.add(name);
    }

    public void removeAllLabel(){
        this.labels.clear();
    }

    public String getLatestUpdateTimestamp() {
        return latestUpdateTimestamp;
    }

    public void setLatestUpdateTimestamp(String latestUpdateTimestamp) {
        this.latestUpdateTimestamp = latestUpdateTimestamp;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphNode)) return false;
        GraphNode graphNode = (GraphNode) o;
        return Double.compare(graphNode.score, score) == 0 &&
                Objects.equals(id, graphNode.id) &&
                Objects.equals(name, graphNode.name) &&
                Objects.equals(className, graphNode.className) &&
                Objects.equals(labels, graphNode.labels) &&
                Objects.equals(latestUpdateTimestamp, graphNode.latestUpdateTimestamp) &&
                Objects.equals(creationTimestamp, graphNode.creationTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, className, labels, latestUpdateTimestamp, creationTimestamp, score);
    }
}
