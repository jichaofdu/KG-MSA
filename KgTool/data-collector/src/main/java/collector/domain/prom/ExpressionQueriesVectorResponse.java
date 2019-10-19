package collector.domain.prom;

import java.util.ArrayList;

public class ExpressionQueriesVectorResponse {

    private String status;

    private DataVector data;

    private String errorType;

    private String error;

    private ArrayList<String> warnings;

    public ExpressionQueriesVectorResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataVector getData() {
        return data;
    }

    public void setData(DataVector data) {
        this.data = data;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ArrayList<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(ArrayList<String> warnings) {
        this.warnings = warnings;
    }
}
