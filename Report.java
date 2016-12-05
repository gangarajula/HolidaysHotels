package models;
// this class returns the rooms booked by the customers. The variables are initialized below.
// using the get set methods the variables are set to the values.
public class Report {
    String first_name,last_name,email,start_date,end_date,place;
    int user_id,room_id,order_id,n_rooms;
/*used to generate the reports for the managers and admin*/
    public Report(){

    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    } //variable place initialized

    public int getN_rooms() {
        return n_rooms;
    }// variable n_rooms initialized and returned.

    public void setN_rooms(int n_rooms) {
        this.n_rooms = n_rooms;
    }

    public String getFirst_name() {
        return first_name;
    }// variable getFrist_name initialized and returned.

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }// variable getLast_name initialized and returned.

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }// variable getEmail initialized and returned.

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStart_date() {
        return start_date;
    } // variable getStart_date initialized and returned.

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    } // variable getEnd_date initialized and returned.

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getUser_id() {
        return user_id;
    }// variable getUser_id initialized and returned.

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRoom_id() {
        return room_id;
    }// variable getRoom_date initialized and returned.

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getOrder_id() {
        return order_id;
    }// variable getOrder_date initialized and returned.

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
}
