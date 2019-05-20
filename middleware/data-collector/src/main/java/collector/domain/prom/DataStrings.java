package collector.domain.prom;

import java.util.ArrayList;

public class DataStrings {

    private String resultType;

    private ArrayList<String> result;

    public DataStrings() {
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public ArrayList<String> getResult() {
        return result;
    }

    public void setResult(ArrayList<String> result) {
        this.result = result;
    }
}
