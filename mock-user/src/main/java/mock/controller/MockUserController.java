package mock.controller;

import mock.async.AsyncTask;
import mock.contacts.Contacts;
import mock.login.LoginInfo;
import mock.login.LoginResult;
import mock.search.QueryInfo;
import mock.search.TripResponse;
import mock.ssh.RemoteExecuteCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.websocket.server.PathParam;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Future;

@RestController
public class MockUserController {

    @Autowired
    private RestTemplate restTemplate;

    private String loginID;
    private String loginToken;

    @Autowired
    private AsyncTask asyncTask;

    @RequestMapping(path = "/welcome", method = RequestMethod.GET)
    public String home(@RequestHeader HttpHeaders headers) {
        return "Welcome to [ Mock Service ] !";
    }

//    @RequestMapping(path = "/docmd", method = RequestMethod.GET)
//    public String doCmd(){
//        String ip = "10.141.211.162";
//        String user = "root";
//        String passwd = "FlHy355g@rA#grhV";
//        RemoteExecuteCommand rec = new RemoteExecuteCommand(ip, user, passwd);
//        System.out.println(rec.login());
//        System.out.println(rec.execute("export KUBECONFIG=/etc/kubernetes/admin.conf; kubectl scale deployment.v1.apps/ts-login-service --replicas=10"));
//        return "";
//    }

    private void changeReplica(int relica){
        String ip = "10.141.211.162";
        String user = "root";
        String passwd = "FlHy355g@rA#grhV";
        RemoteExecuteCommand rec = new RemoteExecuteCommand(ip, user, passwd);
        System.out.println(rec.login());
        System.out.println(rec.execute("export KUBECONFIG=/etc/kubernetes/admin.conf; kubectl scale deployment.v1.apps/ts-login-service --replicas=" + relica));
    }




    @RequestMapping(path = "/mock", method = RequestMethod.GET)
    public String mock() throws Exception{

        int[] lastTime = {10, 50, 100, 50, 100};
        int[] frequncy = {5, 20, 3, 30, 1};

        for(int i = 0; i < lastTime.length; i++){
            int t = lastTime[i];
            int f = frequncy[i];
            int gap = 1000 / f;

            if(f > 10){
                changeReplica(10);
            }else if(f < 5){
                changeReplica(2);
            }

            for(int ongoing = 0; ongoing < t * f; ongoing++){
                Thread.sleep(gap);
                System.out.println("发送在" + new Date().toString());
                Future<String> fr = asyncTask.mockAsyncLogin();
            }
        }

        return "";
    }

//    private void mockLogin(){
//        LoginInfo loginInfo = new LoginInfo("fdse_microservices@163.com", "DefaultPassword", "abcd");
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cookie", "YsbCaptcha=asdasdaasd");
//
//        LoginResult loginResult = restTemplate.exchange(
//                "http://10.141.211.162:31380/login",
//                HttpMethod.POST,
//                new HttpEntity(loginInfo,headers),
//                LoginResult.class).getBody();
//        loginID = loginResult.getAccount().getId().toString();
//        loginToken = loginResult.getToken();
//
//        System.out.println("MockLogin:" + loginResult.getMessage());
//    }

//    private void mockSearchTicket() throws Exception{
//        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd");
//        Date sate = sdf.parse("2019-12-26");
//        QueryInfo queryInfo = new QueryInfo("Shang Hai", "Su Zhou", sate);
//
//        HttpHeaders headers = new HttpHeaders();
//        ArrayList<TripResponse> result = restTemplate.postForObject("http://10.141.211.162:31380/travel/query",
//                queryInfo, new ArrayList<TripResponse>().getClass());
//
//        System.out.println("MockSearch:" + result.size());
//
//    }

    public void mockSearchContacts(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "loginId=" + loginID);
        headers.add("Cookie", "loginToken=" + loginToken);

        ArrayList<Contacts> contacts = restTemplate.exchange(
                "http://10.141.211.162:31380/contacts/findContacts",
                HttpMethod.GET,
                new HttpEntity(null,headers),
                new ArrayList<Contacts>().getClass()).getBody();

        System.out.println("MockContacts:" + contacts.size());
    }

    public void mockReserveTickets(){

    }

}
