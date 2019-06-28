package server;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import com.google.gson.*;
public class Room implements Comparable<Room> {
    String user = "";
    String name = "";
    Furniture [] furniture = new Furniture[10];
    int amount_of_furniture = 0;
    int area = 0;
    ZonedDateTime time;

    Room(String _name, int _area){
        name = _name;
        area  = _area;
        time = ZonedDateTime.now();
    }

    void set_user (String _user){user = _user;}

    void Add_furniture(Furniture f){
        this.furniture[this.amount_of_furniture] = f;
        this.amount_of_furniture = this.amount_of_furniture + 1;
    }

    @Override
    public int compareTo(Room a_) {
        Room a = (Room) a_;
        if (this.area > a.area)
            return 1;
        else if (this.area < a.area)
            return -1;
        else
        if(this.amount_of_furniture < a.amount_of_furniture)
            return 1;
        else if (this.amount_of_furniture > a.amount_of_furniture)
            return -1;
        else return 0;
    }
    @Override
    public String toString(){
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        jsonString = jsonString.replace(",null", "");
        return jsonString;
    }

    @Override
    public int hashCode(){
        int res = 10 * area + 100 * amount_of_furniture + name.hashCode() * 1000;
        int c = 1000;
        for (int i = 0; i < amount_of_furniture; ++i){
            c *= 10;
            res += furniture[i].cost * c;
        }
        return res;
    }

    @Override
    public boolean equals(Object a_){
        Room a =  (Room) a_;
        if (this.area == a.area && this.amount_of_furniture == a.amount_of_furniture){
            int sum1 = 0, sum2 = 0;
            for(int i = 0; i < amount_of_furniture; ++i){
                sum1 += this.furniture[i].cost;
                sum2 += this.furniture[i].cost;
            }
            if (sum1 == sum2) return true;
        }
        return false;
    }
}