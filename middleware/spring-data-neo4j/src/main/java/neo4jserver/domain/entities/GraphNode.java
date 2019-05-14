package neo4jserver.domain.entities;

import org.neo4j.ogm.annotation.*;
import java.util.HashSet;
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
    private Set<String> labels = new HashSet<String>();

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
}
