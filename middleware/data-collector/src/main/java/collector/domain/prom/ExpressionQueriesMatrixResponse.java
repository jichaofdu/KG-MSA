package collector.domain.prom;

//区间向量查询结果
//通过使用 QUERY_RANGE API 我们则可以直接查询 PromQL表达式在一段时间内返回的计算结果
//  URL请求参数:query=<string> : PromQL 表达式。
//  start=<rfc3339 | unix_timestamp> : 起始时间戳。
//  end=<rfc3339 | unix_timestamp> : 结束时间戳。
//  step=<duration | float> : 查询时间步长，时间区间内每 step 秒执行一次

import java.util.ArrayList;

public class ExpressionQueriesMatrixResponse {

    private String status;

    private DataMatrix data;

    private String errorType;

    private String error;

    private ArrayList<String> warnings;

    public ExpressionQueriesMatrixResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataMatrix getData() {
        return data;
    }

    public void setData(DataMatrix data) {
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
