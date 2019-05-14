package neo4jserver.utils;

import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 通用的neo4j调用类
 */
@Component
public class Neo4jUtil {
    private static Driver driver;

    @Autowired
    public Neo4jUtil(Driver driver) {
        Neo4jUtil.driver = driver;
    }

    /**
     * cql的return返回多种节点match (n)-[edge]-(n) return n,m,edge：限定返回关系时，关系的别名必须“包含”edge
     * @param cql 查询语句
     * @param lists 和cql的return返回节点顺序对应
     * @return List<Map<String,Object>>
     */
    public static <T> void getList(String cql, Set<T>... lists) {
        //用于给每个Set list赋值
        int listIndex = 0;
        try {
            Session session = driver.session();
            StatementResult result = session.run(cql);
            List<Record> list = result.list();
            for (Record r : list) {
                if (r.size() != lists.length) {
                    System.out.println("节点数和lists数不匹配");
                    return;
                }
            }
            for (Record r : list) {
                for (String index : r.keys()) {
                    //对于关系的封装
                    if (index.indexOf("edge") != -1) {
                        Map<String, Object> map = new HashMap<>();
                        //关系上设置的属性
                        map.putAll(r.get(index).asMap());
                        //外加三个固定属性
                        map.put("edgeId", r.get(index).asRelationship().id());
                        map.put("edgeFrom", r.get(index).asRelationship().startNodeId());
                        map.put("edgeTo", r.get(index).asRelationship().endNodeId());
                        lists[listIndex++].add((T) map);
                    }
                    //对于节点的封装
                    else {
                        Map<String, Object> map = new HashMap<>();
                        //关系上设置的属性
                        map.putAll(r.get(index).asMap());
                        //外加一个固定属性
                        map.put("nodeId", r.get(index).asNode().id());
                        lists[listIndex++].add((T) map);
                    }
                }
                listIndex = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * cql 路径查询 返回节点和关系
     * @param cql 查询语句
     * @param nodeList 节点
     * @param edgeList 关系
     * @return List<Map<String,Object>>
     */
    public static <T> void getPathList(String cql, Set<T> nodeList, Set<T> edgeList) {
        try {
            Session session = driver.session();
            StatementResult result = session.run(cql);
            List<Record> list = result.list();
            for (Record r : list) {
                for (String index : r.keys()) {
                    Path path = r.get(index).asPath();
                    //节点
                    Iterable<Node> nodes = path.nodes();
                    for (Iterator iter = nodes.iterator(); iter.hasNext(); ) {
                        InternalNode nodeInter = (InternalNode) iter.next();
                        Map<String, Object> map = new HashMap<>();
                        //节点上设置的属性
                        map.putAll(nodeInter.asMap());
                        //外加2个固定属性
                        map.put("id", nodeInter.id());
                        map.put("labels", new HashSet<>(nodeInter.labels()));
                        nodeList.add((T) map);
                    }
                    //关系
                    Iterable<Relationship> edges = path.relationships();
                    for (Iterator iter = edges.iterator(); iter.hasNext(); ) {
                        InternalRelationship relationInter = (InternalRelationship) iter.next();
                        Map<String, Object> map = new HashMap<>();
                        //关系上设置的属性
                        map.putAll(relationInter.asMap());
                        map.put("edgeId", relationInter.id());
                        map.put("edgeFrom", relationInter.startNodeId());
                        map.put("edgeTo", relationInter.endNodeId());
                        edgeList.add((T) map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
