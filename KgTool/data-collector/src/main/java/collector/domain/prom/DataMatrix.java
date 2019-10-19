package collector.domain.prom;


import java.util.ArrayList;

//[
//  {
//    "metric": { "<label_name>": "<label_value>", ... },
//    "values": [ [ <unix_time>, "<sample_value>" ], ... ]
//  },
//  ...
//]
public class DataMatrix {

    private String resultType;

    private ArrayList<ResultMatrix> result;

    public DataMatrix() {
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public ArrayList<ResultMatrix> getResult() {
        return result;
    }

    public void setResult(ArrayList<ResultMatrix> result) {
        this.result = result;
    }
}
