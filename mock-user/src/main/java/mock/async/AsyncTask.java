package mock.async;

import mock.login.LoginInfo;
import mock.login.LoginResult;
import mock.search.QueryInfo;
import mock.search.TripResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Future;

@Component
public class AsyncTask {

    @Autowired
    private RestTemplate restTemplate;

    private String loginID;
    private String loginToken;

    @Async("mySimpleAsync")
    public Future<String> mockAsyncLogin(){

        String startTime = new Date().toString();

        LoginInfo loginInfo = new LoginInfo("fdse_microservices@163.com", "DefaultPassword", "abcd");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "YsbCaptcha=asdasdaasd");

        LoginResult loginResult = restTemplate.exchange(
                "http://10.141.211.162:31380/login",
                HttpMethod.POST,
                new HttpEntity(loginInfo,headers),
                LoginResult.class).getBody();
        loginID = loginResult.getAccount().getId().toString();
        loginToken = loginResult.getToken();

        String endTime = new Date().toString();
        System.out.println("MockLogin Start:" + startTime + " End:" + endTime);

        return new AsyncResult<String>("MockLogin Start:" + startTime + " End:" + endTime);
    }

    @Async("mySimpleAsync")
    public Future<String> mockAsyncSearch() throws Exception{

        String startTime = new Date().toString();

        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd");
        Date sate = sdf.parse("2019-12-26");
        QueryInfo queryInfo = new QueryInfo("Shang Hai", "Su Zhou", sate);

        HttpHeaders headers = new HttpHeaders();
        ArrayList<TripResponse> result = restTemplate.postForObject("http://10.141.211.162:31380/travel/query",
                queryInfo, new ArrayList<TripResponse>().getClass());

        System.out.println("MockSearch:" + result.size());

        String endTime = new Date().toString();
        System.out.println("MockLogin Start:" + startTime + " End:" + endTime);

        return new AsyncResult<String>("MockLogin Start:" + startTime + " End:" + endTime);
    }

}
