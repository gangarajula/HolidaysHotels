package controllers;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.mvc.*;
import play.data.*;
import utils.Authenticaton;
import utils.Authorization;
import views.html.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// All the packages needed for the application are imported here.

import static play.data.Form.form;

/*The main application page for the website*/

public class Application extends Controller
    // The controller class is the Superclass for a Java-based controller it is used to call the http sessions.
{
    static
    {
        // using try catch block for the database driver.
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex)
        {
            // Log or abort here.
        }
    }

    //  The index page is the main home page for the web application.
    public static Result index() {
        String email = session().get("user");//user-key value-email key value pairs
        String s_type = session().get("type");// these variables get the current email and user type from the index page.
        //System.out.println(email+","+s_type);
        if (email != null && s_type != null)

        {
            int type = Integer.valueOf(s_type);
            return returnPageBasedOnType(type);
        } else
            {
            return ok(index.render("Index", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), 0));
        }
    }

    public static Result signup_page() {
        if (session().get("user") == null) {
            return ok(register.render("Register", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), "Page", 0));//"page-response, 0 if not logged in,ok 200 = success "
        } else {
            int type = Integer.valueOf(session().get("type"));
            return returnPageBasedOnType(type);
        }
    }

    public static Result signup() {
        // after user fills the form and clicks on register. signup() post, signup_page()-get
        final Map<String, String[]> form_values = request().body().asFormUrlEncoded();

        String email, first_name, last_name, pwd;
        email = form_values.get("email")[0];
        first_name = form_values.get("first_name")[0];
        last_name = form_values.get("last_name")[0];
        pwd = form_values.get("pwd")[0];

        if (email == null || email.isEmpty() || first_name == null || first_name.isEmpty()
                || last_name == null || last_name.isEmpty() || pwd == null || pwd.isEmpty())
            return ok(register.render("Register", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), "Fields cannot be empty!!!", 0));

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        userInfo.setPassword(pwd);
        userInfo.setFirstName(first_name);
        userInfo.setLastName(last_name);

        String res = Authenticaton.insertIntoTabl(userInfo);

        if (!res.equals("SUCCESS"))
            return ok(register.render("Register", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), "User Already Exists!!!", 0));
        else
            return ok(index.render("Index", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), 0));
    }

    public static Result login_page() {
        if (session().get("user") == null) {
            return ok(login.render("Login", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), "Page", 0));
        } else {
            int type = Integer.valueOf(session().get("type"));
            return returnPageBasedOnType(type);
        }
    }

    public static Result login() {

        final Map<String, String[]> form_values = request().body().asFormUrlEncoded();//to get the values form the form as key value paris
        String email = form_values.get("email")[0];//
        String pwd = form_values.get("pwd")[0];
        if (email == null || email.isEmpty() || pwd == null || pwd.isEmpty())
            return ok(login.render("Login", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), "Email or Password cannot be empty!!!", 0));

        UserLogin u_login = new UserLogin();
        u_login.setEmail(email);
        u_login.setPassword(pwd);

        boolean status = Authenticaton.getFromTabl(u_login);

        if (!status)// if user doesnt exist then this error message is displayed.
            return ok(login.render("Login", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), "Email/Password doesn't match", 0));
        else {// if user exists then page is rendered based on type.
            UserInfo user = Authenticaton.getUserByEmail(email);
            session("user", user.getEmail());
            session("type", String.valueOf(user.getType()));
            int type = Integer.valueOf(session().get("type"));
            return returnPageBasedOnType(type);
        }
    }

    public static Result reportPage() {
        List<Report> reports = Authenticaton.getReports();
        return ok(report.render("Report", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), reports, 0));
    }

    public static Result returnPageBasedOnType(int type) {
//        UserInfo user = Authenticaton.getUserByEmail(email);
//        int type = user.getType();
        if (type == 1) {
            //admin
            Form<UserInfo> managerForm = Form.form(UserInfo.class);
            return ok(admin.render("Admin", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), Authenticaton.getUserByType(3), Authenticaton.getUserByType(2), managerForm, type));
        } else if (type == 2) {
            //manager
            Form<Rooms> roomsForm = Form.form(Rooms.class);
            return ok(manager.render("Manager", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), list_rooms(type), roomsForm, type));
        } else {
            // customer page is rendered if no user type or 3 is given.
            Form<Order> roomsForm = Form.form(Order.class);
            return ok(rooms.render("Rooms", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), list_rooms(type), roomsForm, type));
        }
    }

    public static Result profile() {
        int type = Integer.parseInt(session().get("type"));
        return ok(profile.render("Profile", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), type));
    }

    public static Result changePwd() {
        JsonNode jnode = request().body().asJson();
        int id = Integer.valueOf(jnode.get("id").asText());
        String pwd = jnode.get("pwd").asText();

        Authenticaton.changePassword(id, pwd);
//        System.out.println("id="+id+",pwd="+pwd);
        return ok();
    }

    public static Result saveProfile() {
        JsonNode jnode = request().body().asJson();
        int id = Integer.valueOf(jnode.get("id").asText());
        String first_name = jnode.get("first_name").asText();
        String last_name = jnode.get("last_name").asText();

        Authenticaton.changeName(id, first_name, last_name);
        return ok();
    }

    public static Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }

    public static List<Rooms> list_rooms(int type) {
        List<Rooms> list = new ArrayList<>();
        if (type == 3) {
            String email = session().get("user");
            list = Authenticaton.generateRoomsList(null, null, email);
        } else {
            list = Authenticaton.generateRoomsList(null, null, null);
        }
        return list;
    }


    public static Result rooms() {
        Form<Order> roomsForm = Form.form(Order.class);
        int type = Integer.valueOf(session().get("type"));
//        System.out.println("Current user type:"+type);
        return ok(rooms.render("Rooms", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), list_rooms(type), roomsForm, type));
    }

    public static Result booked_rooms() {

        String email = session().get("user");
        int type = Integer.valueOf(session().get("type"));
        UserInfo user = Authenticaton.getUserByEmail(email);
        List<Rooms> rooms = list_rooms(type);
        List<Order> ordersList = new ArrayList<>();
        for (Rooms room : rooms) {
            ordersList.addAll(Authenticaton.getOrderByRoomId(room.getId(), user.getId()));
//            System.out.println("current order size="+ordersList.size());
        }


        return ok(booked_rooms.render("Rooms", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), ordersList, type));
    }

    public static Result order() {
        JsonNode form_values = request().body().asJson();
        System.out.println(form_values);
        int room_id = Integer.parseInt(form_values.get("room_id").asText());
        int no_of_rooms = Integer.parseInt(form_values.get("no_of_rooms").asText());
        String start_date = form_values.get("start_date").asText();
        String end_date = form_values.get("end_date").asText();
        String email = session().get("user");
//        String card_number = form_values.get("cardNumber")[0];
//        String card_expiry = form_values.get("cardExpiry")[0];
//        String card_cvc = form_values.get("cardCVC")[0];
//        System.out.println("room id = "+room_id);
        Rooms status = Authenticaton.createOrder(room_id, no_of_rooms, start_date, end_date, email);
        if (status != null) {
//            int type= Integer.valueOf(session().get("type"));
//            return ok(order.render("Order", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), status,type,start_date,end_date));
            return redirect(routes.Application.rooms());
        } else
            return internalServerError("Oops");
    }

    public static Result addManager() {
        final Map<String, String[]> form_values = request().body().asFormUrlEncoded();
//        System.out.println(form_values);
        UserInfo user = new UserInfo();
        user.setEmail(form_values.get("email")[0]);
        user.setFirstName(form_values.get("firstName")[0]);
        user.setLastName(form_values.get("lastName")[0]);
        user.setType(2);
        user.setPassword("password");// explicitly setting the password to "Password for all managers"

        Authenticaton.addManager(user);
        int type = Integer.valueOf(session().get("type"));
        Form<UserInfo> managerForm = Form.form(UserInfo.class);
        return ok(admin.render("Admin", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), Authenticaton.getUserByType(3), Authenticaton.getUserByType(2), managerForm, type));
    }

    public static Result deleteManager(String email) {
        Authenticaton.deleteUserByEmail(email);
        int type = Integer.valueOf(session().get("type"));
        return returnPageBasedOnType(type);
//        Form<UserInfo> managerForm = Form.form(UserInfo.class);
//        return ok(admin.render("Admin", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), Authenticaton.getUserByType(3),Authenticaton.getUserByType(2), managerForm));
    }

    public static Result deleteRoom(int id) {
        Authenticaton.deleteRoomById(id);
        Form<Rooms> roomsForm = Form.form(Rooms.class);
        int type = Integer.valueOf(session().get("type"));
        return ok(manager.render("Manager", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), list_rooms(type), roomsForm, type));
    }

    public static Result deleteOrder(int id) {
        Authenticaton.deleteOrderById(id);
        return redirect(routes.Application.booked_rooms());
    }

    public static Result addRoom() {
        JsonNode form_values = request().body().asJson();
        Rooms room = new Rooms();
        room.setAddress(form_values.get("address").asText());
        room.setCity(form_values.get("city").asText());
        room.setState(form_values.get("state").asText());
        room.setPlace_name(form_values.get("place_name").asText());
        room.setNo_of_beds(Integer.valueOf(form_values.get("no_of_beds").asText()));
        room.setNo_of_rooms(Integer.valueOf(form_values.get("no_of_rooms").asText()));
        if (form_values.get("id") != null && !form_values.get("id").asText().isEmpty())
            room.setId(Integer.valueOf(form_values.get("id").asText()));
        Authenticaton.addRoom(room);
        int type = Integer.valueOf(session().get("type"));
        Form<Rooms> roomsForm = Form.form(Rooms.class);
        return ok(manager.render("Manager", Authorization.isSessionAvailable(ctx()), Authorization.getUser(ctx()), list_rooms(type), roomsForm, type));
    }

}