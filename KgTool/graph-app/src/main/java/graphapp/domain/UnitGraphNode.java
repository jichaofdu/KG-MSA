package graphapp.domain;

import graphapp.domain.entities.GraphNode;

//绑定GraphNode与Abnormality以便进行排序
    public class UnitGraphNode {

        public double resultValue;

        public GraphNode node;

        public UnitGraphNode(double resultValue, GraphNode node) {
            this.resultValue = resultValue;
            this.node = node;
        }
    }