package collector.domain.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 是所有Entity节点的父类
 * 注意：Property请和成员变量名保持严格一致
 */
public class GraphNode {

    private String id;

    private String name;

    private String className = this.getClass().getName();

    private Set<String> labels = new HashSet<>();

    private String latestUpdateTimestamp;

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

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    //通过Name判断是否相等
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof GraphNode) {
            if (((GraphNode) obj).name.equals(this.name)) {
                return true;
            }
        }

        return false;
    }
}
