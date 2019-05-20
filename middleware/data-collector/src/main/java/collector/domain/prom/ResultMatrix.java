package collector.domain.prom;

import java.util.ArrayList;
import java.util.HashMap;

//[
//  {
//    "metric": { "<label_name>": "<label_value>", ... },
//    "values": [ [ <unix_time>, "<sample_value>" ], ... ]
//  },
//  ...
//]
public class ResultMatrix {

    private HashMap<String, String> metric;

    private ArrayList<ArrayList<String>> values;

    public ResultMatrix() {
    }

    public HashMap<String, String> getMetric() {
        return metric;
    }

    public void setMetric(HashMap<String, String> metric) {
        this.metric = metric;
    }

    public ArrayList<ArrayList<String>> getValues() {
        return values;
    }

    public void setValues(ArrayList<ArrayList<String>> values) {
        this.values = values;
    }
}
