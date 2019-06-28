package client;
import java.util.regex.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class UDPClient implements Runnable {
    private final int clientPort;
    private final int serverPort;
    private String serverAdress;
    DatagramChannel channel;
    String user = "";

    UDPClient(int port, int serverPort, String serverAdress) {
        this.clientPort = port;
        this.serverPort = serverPort;
        this.serverAdress = serverAdress;
    }
    void send_auth(String message){
        byte[] data = message.getBytes();
        try{
            channel.write(ByteBuffer.wrap(data));}
        catch (IOException e) {
            System.err.println("can't send package");
        }
    }

    boolean receive_auth(){
        String s1 = "Адрес задан некорректно";
        String s2 = "Пароль неверный";
        try{
            ByteBuffer inBuffer = ByteBuffer.allocate(1024);
            int ans = channel.read(inBuffer);
            byte[] arr = inBuffer.array();
            String receivedMessage = new String(arr).trim();
            System.out.println("response from server:" + "\n" + receivedMessage);
            if (receivedMessage.trim().equals(""))
            if (ans == -1){
                System.out.println("Не удаётся получить ответ от сервера :(");
                System.exit(1);
            }
            if (receivedMessage.equals(s1) || receivedMessage.equals(s2)) System.exit(1);
            else
                return true;
        }
        catch (Exception e) {
            System.out.println("Не удаётся получить ответ от сервера :(");
            System.exit(1);
        }
        return false;
    }

    void auth(){
        Scanner in = new Scanner(System.in);
        System.out.println("Введите адрес электронной почты с целью дальнейшей регистрации или авторизации");
        String login = in.nextLine();
        send_auth("check_login " + login);
        if (receive_auth()){
            System.out.println("Введите пароль");
            send_auth("check_password "+ login +" " + in.nextLine());
            if (receive_auth()){
                user = login;
                System.out.println("Вы успешно авторизованы");
            }
            else{System.exit(1);}
        }
    }
    boolean send(){
        String requestMessage;

        Scanner in = new Scanner(System.in);
        requestMessage =  in.nextLine() + " " + user;
        if (requestMessage.equals("exit"))
            return false;
        byte[] data = requestMessage.getBytes();
        try{
        channel.write(ByteBuffer.wrap(data));}
        catch (IOException e) {
            System.err.println("can't send package");
        }
        return true;
        //DatagramPacket datagramRequestPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), serverPort);
    }

    void receive(){
        try{
            ByteBuffer inBuffer = ByteBuffer.allocate(1024);
            int ans = channel.read(inBuffer);
            byte[] arr = inBuffer.array();
            String recievedMessage = new String(arr);
            System.out.println("response from server:" + "\n" + recievedMessage.trim());

            if (ans == -1){
                System.out.println("Не удаётся получить ответ от сервера :(");
                System.exit(1);
            }
        }
        catch (Exception e) {
        System.out.println("Не удаётся получить ответ от сервера :(");
        System.exit(1);
        }
    }

    @Override
    public void run() {
      ///здесь получаем сообщения с сервера
        InetAddress serverA;
        try{serverA = InetAddress.getByName(serverAdress);}
        catch (UnknownHostException e){
            System.out.println("Не удаётся распознать хост");
            return;
        }
        try {
           SocketAddress address = new InetSocketAddress(serverA, serverPort);
           this.channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(clientPort));
           while (!channel.isConnected())
                channel.connect(address);
           System.out.println("Клиент с портом " + clientPort + " запущен");
        } catch (IOException e) {
           System.out.println("Не удаётся содать канал. Порт клиента занят");
           return;
        }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    auth();
                    boolean work = true;
                    while (true) {
                        //отправляем
                        work = send();
                        if (work == false)
                            break;
                        receive();
                    }

                }
            });
            //чтобы получатель работал, надо запустить его
            thread.start();
    }
}