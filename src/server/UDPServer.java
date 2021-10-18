package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Scanner;

public class UDPServer implements Runnable {
    private final int serverPort;

    public UDPServer(int serverPort) {
        this.serverPort = serverPort;
    }

    static void save(MyLinkedHashSet hset) {
        String path = "input.txt";
        File file = new File(path);
        try {
            PrintWriter printWriter = new PrintWriter(file);
            Iterator<Room> iter = hset.getSet().iterator();
            while (iter.hasNext()) {
                printWriter.println(iter.next().toString());
            }
            printWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Необходимо указать существующий файл");
            return;
        }

    }

    static String wrong_input() {
        return "Неверный формат команды";
    }

    static String wrong_json() {
        return "Неверно задан json";
    }

    String processRemoveCommand(String[] args, HashSetRepository repository) {
        if (args.length != 3)
            return wrong_input();
        String roomStr = args[1];
        try {
            Room room = gson.fromJson(roomStr, Room.class);
            room.setUser(args[2]);
            if (room.getArea() == 0)
                return wrong_json();
            else {
                return repository.remove(room);
            }
        } catch (RuntimeException e) {
            return wrong_json();
        }
    }

    String processShowCommand(String[] args, HashSetRepository repository) {
        if (args.length > 2) return wrong_input();
        else {
            String ans = repository.show();
            if (ans.isEmpty())
                return "Коллекция пустая\n";
            return ans;
        }
    }

    String processAddIfMinCommand(String[] args, HashSetRepository repository) {
        if (args.length == 3) {
            String roomStr = args[1];
            try {
                Room room = gson.fromJson(roomStr, Room.class);
                room.setUser(args[2]);
                if (room.getArea() == 0)
                    return wrong_json();
                else {

                    return repository.add_if_min(room);
                }
            } catch (RuntimeException e) {
                return wrong_json();
            }
        } else return wrong_input();
    }

    String processRemoveGreaterCommand(String[] args, HashSetRepository repository) {
        if (args.length == 3) {
            try {
                Room room = gson.fromJson(args[1], Room.class);
                room.setUser(args[2]);
                if (room.getArea() == 0) {
                    return wrong_json();
                } else {
                    return repository.remove_greater(room);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                return wrong_json();
            }
        } else return wrong_input();
    }

    String processAddCommand(String[] args, HashSetRepository repository) {
        if (args.length == 3) {
            try {
                Room room = gson.fromJson(args[1], Room.class);
                room.setUser(line[2]);
                if (room.getArea() == 0)
                    return wrong_json();
                else {
                    return repository.add(room);
                }

            } catch (RuntimeException e) {
                return wrong_json();
            }
        }
        return wrong_input();
    }

    String processHelpCommand(String[] args, HashSetRepository repository) {
        if (args.length > 2) return wrong_input();
        else {
            String ans = "Поддерживаются команды";
            ans = ans + "remove {element}: удалить элемент из коллекции по его значению\n" +
                    "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                    "add_if_min {element}: добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                    "remove_greater {element}: удалить из коллекции все элементы, превышающие заданный\n" +
                    "clear: очистить коллекцию\n" +
                    "add {element}: добавить новый элемент в коллекцию\n" + "exit: выход из программы";
            return ans;
        }
    }

    String processing_request(String request, MyLinkedHashSet hset, HashSetRepository repository, Authorisation auth) {
        String[] line = request.split(" ");
        if (line.length > 3) {
            return wrong_input();
        }

        String command = line[0];
        Room room;
        Gson gson = new Gson();
        switch (command) {
            case ("remove"):
                return processRemoveCommand(line, repository);
            case ("show"):
                return processShowCommand(line, repository);
            case ("add_if_min"):
                return processAddIfMinCommand(line, repository);
            case ("remove_greater"):
                return processRemoveGreaterCommand(line, repository);
            case ("clear"):
                if (line.length > 2) return wrong_input();
                else {
                    return repository.clear();
                }
            case ("add"):
                return processAddCommand(line, repository);
            case ("help"):
                return processHelpCommand(line, repository);
            case ("check_login"):
                if (line.length != 2) return wrong_input();
                else return auth.check_login(line[1]);
            case ("check_password"):
                if (line.length != 3) return wrong_input();
                else return auth.check_password(line[1], line[2]);
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
        HashSetRepository repository = new HashSetRepository();
        Authorisation auth = new Authorisation();

        //пытаемся установить соединение с бд
        if (repository.connection() == true) {
            System.out.println("Соединение с БД установлено");
        } else System.out.println("Соединение с БД установить не удалось");

        if (auth.connection() == true) {
            System.out.println("Соединение с БД установлено");
        } else System.out.println("Соединение с БД установить не удалось");

        String msg = "";
        try {

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String s = sc.nextLine().trim();
                Gson gson = new Gson();
                try {
                    Room room = new Room("", 0);
                    room = gson.fromJson(s, Room.class);
                    if (room.getArea() == 0) {
                        msg = "В файле неверно заданы данные, создана пустая коллекция";
                        continue;
                    }
                    hset.add(room);
                } catch (RuntimeException e) {
                    msg = "В файле неверно заданы данные, создана пустая колекция";
                    continue;
                }

            }
            sc.close();
        } catch (FileNotFoundException e) {

        }


        try (DatagramSocket serverSocket = new DatagramSocket(serverPort)) {
            byte[] buffer = new byte[65507]; //64407 - max size
            // если msg изменилось, значит должна быть создана пустая коллекция
            if (!msg.isEmpty())
                System.out.println(msg);
            while (true) {
                //показали как выглядит пакет
                DatagramPacket requestPacket = new DatagramPacket(
                        buffer,
                        0,
                        buffer.length
                );
                //получили пакет
                serverSocket.receive(requestPacket);
                //System.out.println("адрес клиента " + requestPacket.getAddress() + " порт клиента "+ requestPacket.getPort());
                //определили, что внутри
                int clientPort = requestPacket.getPort();
                //System.out.println(clientPort);
                String clientMessage = new String(requestPacket.getData(), requestPacket.getOffset(), requestPacket.getLength());
                System.out.println("Получен запрос: " + clientMessage + " с клиентского порта: " + clientPort);

                //формируем ответ
                String serverResponseMessage = processing_request(clientMessage, hset, repository, auth);
                //System.out.println(serverResponseMessage);
                DatagramPacket datagramResponsePacket = new DatagramPacket(
                        serverResponseMessage.getBytes(),
                        serverResponseMessage.getBytes().length,
                        requestPacket.getAddress(),
                        requestPacket.getPort()
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