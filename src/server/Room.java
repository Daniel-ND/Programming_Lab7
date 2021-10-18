package server;

import com.google.gson.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Room implements Comparable<Room> {
    private String user;
    private String name;
    private ArrayList<Furniture> furniture;
    private int area = 0;
    private ZonedDateTime time;

    Room(String _name, int _area) {
        name = _name;
        area = _area;
        time = ZonedDateTime.now();
        furniture = new ArrayList<>();
    }

    protected void setUser(String _user) {
        user = _user;
    }

    public void Add_furniture(Furniture f) {
        furniture.add(f);
    }

    public int getAmountOfFurniture() {
        return furniture.size();
    }

    public ArrayList<Furniture> getFurniture() {
        return furniture;
    }

    public int getArea() {
        return area;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Room a) {
        if (area > a.getArea())
            return 1;
        else if (this.area < a.getArea())
            return -1;
        else if (furniture.size() < a.getAmountOfFurniture())
            return 1;
        else if (furniture.size() > a.getAmountOfFurniture())
            return -1;
        else return 0;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        jsonString = jsonString.replace(",null", "");
        return jsonString;
    }

    @Override
    public int hashCode() {
        int res = 10 * area + 100 * furniture.size() + name.hashCode() * 1000;
        int c = 1000;
        for (int i = 0; i < furniture.size(); ++i) {
            c *= 10;
            res += furniture.get(i).getCost() * c;
        }
        return res;
    }

    @Override
    public boolean equals(Object a_) {
        Room a = (Room) a_;
        if (area == a.getArea() && furniture.size() == a.getAmountOfFurniture()) {
            int sum1 = 0, sum2 = 0;
            for (int i = 0; i < furniture.size(); ++i) {
                sum1 += furniture.get(i).getCost();
                sum2 += a.getFurniture().get(i).getCost();
            }
            return sum1 == sum2;
        }
        return false;
    }
}