package collector.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUrlRouterUtil {

    public static String nodeIdMatcherPattern(String maStr){
        Pattern p = Pattern.compile("\\~..-.*-.*-.*-.....");
        Matcher matcher = p.matcher(maStr);
        String urlRouter2 = "";
        if (matcher.find()) {
            urlRouter2 = matcher.group(0);
            urlRouter2 = urlRouter2.replaceAll("~","");
        }
        return urlRouter2;
    }

    //http://ts-station-service:12345/station/exist
    public static String matcherPattern(String fullUri){
        String str = fullUri;
        str = str.substring(7);
        int indexOne = str.indexOf("/");
        str = str.substring(indexOne);

        int indexLast = str.lastIndexOf("/");
        if(str.substring(indexLast+1).length() > 28){
            return str.substring(0, indexLast);
        }else{
            if(str.contains("getRouteByTripId")){
                return str.substring(0, indexLast);
            }
            return str;
        }
    }

//    public static String matcherPattern(String maStr) {
//        Pattern p = Pattern.compile("\\d{1}/.*/.*/");
//        Matcher matcher = p.matcher(maStr);
//        String urlRouter = "";
//        if (matcher.find()) {
//            urlRouter = matcher.group(0);
//            urlRouter = urlRouter.substring(1, urlRouter.length() - 1);
//            System.out.println("第一");
//        } else {
//            p = Pattern.compile("\\d{1}/.*/");
//            matcher = p.matcher(maStr);
//            if (matcher.find()) {
//                urlRouter = matcher.group(0);
//                urlRouter = urlRouter.substring(1, urlRouter.length() - 1);
//                System.out.println("第二");
//            } else {
//                p = Pattern.compile("\\d{1}/.*\\?");
//                matcher = p.matcher(maStr);
//                if (matcher.find()) {
//                    urlRouter = matcher.group(0);
//                    urlRouter = urlRouter.substring(1, urlRouter.length() - 1);
//                    System.out.println("第三");
//                } else {
//                    p = Pattern.compile("\\d{1}/.*");
//                    matcher = p.matcher(maStr);
//                    if (matcher.find()) {
//                        urlRouter = matcher.group(0);
//                        urlRouter = urlRouter.substring(1);
//                        System.out.println("第四");
//                    } else {
//                        urlRouter = maStr;
//                        System.out.println("第五");
//                    }
//                }
//            }
//        }
//        System.out.println(urlRouter);
//        return urlRouter;
//    }
}