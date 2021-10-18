package server;

import org.postgresql.*;

import java.sql.*;

public class HashSetRepository {
    String username = "s265091";
    String password = "qwh669";
    String URL = "jdbc:postgresql://pg/studs";
    String insert_into_rooms = "INSERT INTO rooms(login, name, amount_of_furniture, area) VALUES(?, ?, ?, ?) ";
    String insert_into_furniture = "INSERT INTO furniture(room_id, cost) VALUES(?, ?)";

    HashSetRepository() {
    }

    // осуществляет соединение с БД и создает таблицы, если такие не имеются
    synchronized private boolean exist(Room room) {
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            boolean ind1 = false;
            int cost1 = 0;
            String sql = "select * from rooms where login = ? and area = ? and name = ? and amount_of_furniture = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, room.getUser());
            pst.setInt(2, room.getArea());
            pst.setString(3, room.getName());
            pst.setInt(4, room.getAmountOfFurniture());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                ind1 = true;
                sql = "select sum(cost) from furniture";
                ResultSet rs2 = connection.createStatement().executeQuery(sql);
                if (rs2.next()) cost1 = rs2.getInt("sum");
            }
            int cost2 = 0;
            for (int i = 0; i < room.getAmountOfFurniture(); i++)
                cost2 += room.getFurniture().get(i).getCost();
            if (cost1 == cost2 && ind1) return true;
            else return false;
        } catch (SQLException e) {
            System.out.println("Неудача");
            return false;
        }
    }

    boolean connection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS rooms( id serial, login text, name text, amount_of_furniture int, area int)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS furniture(room_id int, cost int)");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    //добавляет элемент в БД
    synchronized String add(Room room) {
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            // сначала надо проверить, есть ли элемент в БД
            if (exist(room))
                return "Данный элемент был добавлен ранее";
            //обновляем таблицу rooms
            PreparedStatement statement1 = connection.prepareStatement(insert_into_rooms);
            statement1.setString(1, room.getUser());
            statement1.setString(2, room.getName());
            statement1.setInt(3, room.getAmountOfFurniture());
            statement1.setInt(4, room.getArea());
            statement1.executeUpdate();
            //пытаемся получить room_id добавленной комнаты
            Statement statement2 = connection.createStatement();
            ResultSet rs = statement2.executeQuery("SELECT MAX(id) as maxid FROM rooms");
            if (rs.next()) {
                int id = 0;
                id = rs.getInt("maxid");
                for (int i = 0; i < room.getAmountOfFurniture(); i++) {
                    PreparedStatement statement3 = connection.prepareStatement(insert_into_furniture);
                    statement3.setInt(1, id);
                    statement3.setInt(2, room.getFurniture().get(i).getCost());
                    statement3.executeUpdate();
                }
            }
            return "Комната успешно добавлена в хранилище";
        } catch (SQLException e) {
            return "";
        }
    }


    String show() {
        String result = "";
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM rooms");
            while (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("login");
                String name = rs.getString("name");
                int area = rs.getInt("area");

                Room room = new Room(name, area);
                room.setUser(user);
                Statement statement2 = connection.createStatement();
                String insert = "SELECT * FROM furniture WHERE room_id=" + Integer.toString(id);
                ResultSet rs2 = statement2.executeQuery(insert);
                while (rs2.next()) {
                    int cost = rs2.getInt("cost");
                    room.Add_furniture(new Furniture(cost));
                }
                result = result + "\n" + room.toString();
            }
        } catch (SQLException e) {
            return "Неудача";
        }
        return result;
    }


    synchronized String remove(Room room) {
        String result = "";
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            String sql = "select * from rooms where login = ? and area = ? and name = ? and amount_of_furniture = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, room.getUser());
            pst.setInt(2, room.getArea());
            pst.setString(3, room.getName());
            pst.setInt(4, room.getAmountOfFurniture());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                sql = "delete from rooms where id=" + id;
                Statement statement1 = connection.createStatement();
                statement1.executeUpdate(sql);
                sql = "delete from furniture where room_id=" + id;
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate(sql);
                result = "Элемент был удалён из коллекции";
            } else result = "Данного элемента не оказалось в коллекции, удалить не удалось";
        } catch (SQLException e) {
            return "Неудача";
        }
        return result;
    }

    synchronized String add_if_min(Room room) {
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            String sql = "select min(area) from rooms";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int min_area = Integer.MAX_VALUE;
            if (rs.next()) {
                min_area = rs.getInt("min");
            }
            if (min_area > room.getArea()) return this.add(room);
            else if (min_area == room.getArea()) {
                sql = "select max(amount_of_furniture) from rooms where area = " + room.getArea();
                st = connection.createStatement();
                rs = st.executeQuery(sql);
                int max_amount = -1;
                if (rs.next()) {
                    max_amount = rs.getInt("max");
                }
                if (room.getAmountOfFurniture() > max_amount) return this.add(room);
            }
        } catch (SQLException e) {
            return "Неудача";
        }
        return "Комнату не удалось добавить";
    }

    synchronized String remove_greater(Room room) {
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            String sql = "select count(*) from rooms";
            Statement st = connection.createStatement();
            int count = 0;
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt("count");
            }
            sql = "delete from rooms where login = ? and area > ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, room.getUser());
            pst.setInt(2, room.getArea());
            pst.executeUpdate();
            sql = "delete from rooms where login = ? and area = ? and amount_of_furniture < ?";
            pst = connection.prepareStatement(sql);
            pst.setString(1, room.getUser());
            pst.setInt(2, room.getArea());
            pst.setInt(3, room.getAmountOfFurniture());
            pst.executeUpdate();
            sql = "delete from furniture where room_id not in (select id from rooms)";
            connection.createStatement().executeUpdate(sql);

            sql = "select count(*) from rooms";
            st = connection.createStatement();
            int count2 = 0;
            rs = st.executeQuery(sql);
            if (rs.next()) {
                count2 = rs.getInt("count");
            }
            if (count == count2) return "Элементы удалить не удалось";
            else return "Удалено " + Integer.toString(count - count2) + " элементов";
        } catch (SQLException e) {
            return "Неудача";
        }
    }

    String clear() {
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            String sql = "delete from rooms";
            connection.createStatement().executeUpdate(sql);
            sql = "delete from furniture";
            connection.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            return "Неудача";
        }
        return "Коллекция очищена";
    }
}
