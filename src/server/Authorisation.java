package server;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.sql.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.lang.Math;
import java.util.Random;

public class Authorisation {
    String username = "s265091";
    String password = "qwh669";
    String URL = "jdbc:postgresql://pg/studs";

    Authorisation() {
    }

    boolean connection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (login text, hash_code text)");
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private synchronized String sha1(String login) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(login.getBytes("utf8"));
            return String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            return "0";
        }

    }

    public static String generatePassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    private synchronized boolean registration(String login) {
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            String password = generatePassword(5);
            String hash = sha1(password);
            String sql = "insert into users (login, hash_code) values(?, ?)";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, login);
            st.setString(2, hash);
            st.executeUpdate();
            boolean ind = EmailSender.send(login, password);
            return ind;
        } catch (SQLException e) {
            return false;
        }
    }

    String check_password(String login, String pas) {
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            String hash1 = sha1(pas);
            String hash2 = "";
            String sql = "select * from users where login='" + login + "'";
            ResultSet rs = connection.createStatement().executeQuery(sql);
            if (rs.next()) {
                hash2 = rs.getString("hash_code");
            }
            if (hash1.equals(hash2)) return "Пароль верный";
        } catch (SQLException e) {
            return "Неудача";
        }
        return "Пароль неверный";
    }

    String check_login(String login) {
        try (Connection connection = DriverManager.getConnection(URL, username, password);) {
            String sql = "select * from users where login='" + login + "'";
            ResultSet rs = connection.createStatement().executeQuery(sql);
            if (rs.next()) return "Адрес есть в базе данных";
            else {
                if (registration(login))
                    return "Пользователь успешно зарегистрирован. Пароль был отправлен по адресу " + login;
            }
            return "Адрес задан некорректно";

        } catch (SQLException e) {
            return "Неудача";
        }
    }

}
