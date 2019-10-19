package collector.domain.prom;


import java.util.ArrayList;
import java.util.HashMap;

//[
//  {
//    "metric": { "<label_name>": "<label_value>", ... },
//    "value": [ <unix_time>, "<sample_value>" ]
//  },
//  ...
//]
public class ResultVector {

    private HashMap<String, String> metric;

    private ArrayList<String> value;

    public ResultVector() {
    }

    public HashMap<String, String> getMetric() {
        return metric;
    }

    public void setMetric(HashMap<String, String> metric) {
        this.metric = metric;
    }

    public ArrayList<String> getValue() {
        return value;
    }

    public void setValue(ArrayList<String> value) {
        this.value = value;
    }
}
