package collector.domain.prom;


import java.util.ArrayList;

public class DataVector {

    private String resultType;

    private ArrayList<ResultVector> result;

    public DataVector() {
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public ArrayList<ResultVector> getResult() {
        return result;
    }

    public void setResult(ArrayList<ResultVector> result) {
        this.result = result;
    }
}
