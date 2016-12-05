package utils;


import models.UserInfo;
import play.mvc.Http;
import play.mvc.Security;


public class Authorization extends Security.Authenticator{

    public static boolean isSessionAvailable(Http.Context context){
        String email = context.session().get("user");
//        System.out.println("EMAIL:"+email);
        if(email==null || email.isEmpty())
            return false;
        else
            return true;
    }

    public static UserInfo getUser(Http.Context context){
        String email = context.session().get("user");
        if(email==null || email.isEmpty())
            return null;
        else
            return Authenticaton.getUserByEmail(email);
    }
}
