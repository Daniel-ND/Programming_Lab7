package server;

import java.io.IOException;
import java.net.*;
import com.google.gson.*;
import java.io.File;
import java.util.Iterator;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class UDPServer implements Runnable
{
    private final int serverPort;

    public UDPServer(int serverPort) {
        this.serverPort = serverPort;
    }

    static void save (MyLinkedHashSet hset){
        String path = "input.txt";
        File file = new File (path);
        try {
            PrintWriter printWriter = new PrintWriter(file);
            Iterator<Room> iter = hset.getSet().iterator();
            while (iter.hasNext()) {
                printWriter.println(iter.next().toString());
            }
            printWriter.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Необходимо указать существующий файл");
            return;
        }

    }

    static String wrong_input(){
        return "Неверный формат команды";
    }

    static String wrong_json(){
        return "Неверно задан json";
    }

    String processing_request (String request, MyLinkedHashSet hset){
        String answer = "";
        String [] line = request.split(" ");
        if (line.length > 2){
            return wrong_input();
        }
        String command = "";
        Room room = new Room("",0);
        Gson gson = new Gson();
        command = line[0];
        switch (command){
            case ("remove"):
                if(line.length == 2) {
                    String s = line[1];
                    try {
                        room = gson.fromJson(s, Room.class);
                        if (room.area == 0)
                            return wrong_json();
                        else{
                            int cnt = hset.getSet().size();
                            hset.remove(room);
                            save(hset);
                            if (cnt != hset.getSet().size())
                                return ("Элемент удалён из коллекции");
                            else
                                return ("Элемент не удалось удалить из коллекции");
                        }
                    }
                    catch (RuntimeException e) {
                        return wrong_json();
                    }
                }
                else return wrong_input();
            case ("show"):
                if (line.length > 1) return wrong_input();
                else {
                    save(hset);
                    String ans = hset.show();
                    if (ans.isEmpty())
                        return "Коллекция пустая\n";
                    return ans;
                }
            case("add_if_min"):
                if(line.length == 2) {
                    String s = line[1];
                    try {
                        room = gson.fromJson(s, Room.class);
                        if (room.area == 0)
                            return wrong_json();
                        else{
                            int cnt = hset.getSet().size();
                            hset.add_if_min(room);
                            save(hset);
                            if (cnt != hset.getSet().size())
                                return ("Элемент добавлен в коллекцию");
                            else
                                return ("Элемент не удалось добавить в коллекцию");
                        }
                    }
                    catch (RuntimeException e) {
                        return wrong_json();
                    }
                }
                else return wrong_input();

            case("remove_greater"):
                if(line.length == 2) {
                    String s = line[1];
                    try {
                        room = gson.fromJson(s, Room.class);
                        if (room.area == 0) {
                            //System.out.println("here! (1)");
                            return wrong_json();
                        }
                        else{
                            int cnt = hset.getSet().size();
                            hset.remove_greater(room);
                            save(hset);
                            if (cnt != hset.getSet().size())
                                return ("Элементы удалены");
                            else
                                return ("В коллекции не оказалось элементов больше заданного");
                        }

                    }
                    catch (RuntimeException e) {
                        e.printStackTrace();
                        //System.out.println("here! (2)");
                        return wrong_json();
                    }
                }
                else return wrong_input();
            case("info"):
                if (line.length > 1) return wrong_input();
                else
                return hset.info();
            case("clear"):
                if (line.length > 1) return wrong_input();
                else {hset.clear();
                save(hset);
                return "Из коллекции удалены все элементы";

                }
            case("add"):
                if(line.length == 2) {
                    String s = line[1];
                    try {
                        room = gson.fromJson(s, Room.class);
                        if (room.area == 0)
                            return wrong_json();
                        else{
                            int cnt = hset.getSet().size();
                            hset.add(room);
                            save(hset);
                            if (cnt != hset.getSet().size())
                                return "Элемент добавлен в коллекцию";
                            return ("Элемент не удалось добавить в коллекцию");
                        }

                    }
                    catch (RuntimeException e) {
                        return wrong_json();
                    }
                }
                else
                    save(hset);
                    return wrong_input();
            case("help"):
                if (line.length > 1) return wrong_input();
                else {String ans ="Поддерживаются команды";
                     ans  = ans +  "remove {element}: удалить элемент из коллекции по его значению\n" +
                            "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                            "add_if_min {element}: добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                            "remove_greater {element}: удалить из коллекции все элементы, превышающие заданный\n" +
                            "info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                            "clear: очистить коллекцию\n" +
                            "add {element}: добавить новый элемент в коллекцию\n"+"exit: выход из программы";
                    return ans;}
            case("exit"):
                if (line.length > 1) return wrong_input();
                save(hset);
                /// придумать, как завершить работу с клиентом
            default:
                return wrong_input();
        }
    }


    @Override
    public void run() {
        boolean ind = true;
        // обработка файла с коллекцией
        File file = new File("input.txt");
        MyLinkedHashSet hset = new MyLinkedHashSet();

        String msg = "";
        try {

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String s = sc.nextLine().trim();
                Gson gson = new Gson();
                try {
                    Room room = new Room("",0);
                    room = gson.fromJson(s, Room.class);
                    if (room.area == 0) {
                        msg = "В файле неверно заданы данные, создана пустая коллекция";
                        continue;
                    }
                    hset.add(room);
                }
                catch (RuntimeException e) {
                    msg = "В файле неверно заданы данные, создана пустая колекция";
                    continue;
                }

            }

            sc.close();

        }
        catch (FileNotFoundException e) {
            try {
                boolean b = file.createNewFile();
            } catch (IOException ex) {
                msg = "Нет прав на создание файла на сервере";
                ind = false;
                return;
            }
        }



        try(DatagramSocket serverSocket = new DatagramSocket(serverPort)) {
            byte[] buffer = new byte[65507]; //64407 - max size
            // если msg изменилось, значит должна быть создана пустая коллекция
           if (!(msg == ""))
               System.out.println(msg);

            while (ind == true) {
                //показали как выглядит пакет
                DatagramPacket requestPacket = new DatagramPacket(
                        buffer,
                        0,
                        buffer.length
                );


                //получили пакет
                serverSocket.receive(requestPacket);
                //определили, что внутри
                int clientPort = requestPacket.getPort();
                String clientMessage = new String(requestPacket.getData(), requestPacket.getOffset(), requestPacket.getLength());
                System.out.println("Получен запрос: "+ clientMessage + " с клиентского порта: " + clientPort);

                //формируем ответ
                String serverResponseMessage = processing_request(clientMessage, hset);
                //System.out.println(serverResponseMessage);
                DatagramPacket datagramResponsePacket = new DatagramPacket(
                        serverResponseMessage.getBytes(),
                        serverResponseMessage.getBytes().length,
                        requestPacket.getAddress(),
                        clientPort
                );
                serverSocket.send(datagramResponsePacket);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}