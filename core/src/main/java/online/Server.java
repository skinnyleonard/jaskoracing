package online;

import entities.Car;
import screens.PlayScreen;
import tools.MapLoader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Server extends Thread {
    public static int clientIndexed = 0;
    private DatagramSocket socket;
    private int port = 5555;
    private boolean end = false;
    private final int MAX_CLIENTS = 2;
    private int connectedUsers = 0;
    public ArrayList<User> users = new ArrayList<User>();
    private NetManager netManager;
    public static String move = "";
    private Car player;
    private MapLoader mapLoader;
    private PlayScreen playScreen;

    public Server(NetManager netManager) {
        this.netManager = netManager;
        try {
            System.out.println("server iniciado en el puerto "+port);
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server shutting down");
            this.pingEveryone("serverClosed");
            end = true;
        }));
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
//            System.out.println("esperando mensajito, con cariño el servidor");
            try {
                socket.receive(packet);
                processMessage(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (!end);
    }

    public void pingEveryone(String message) {
        for(User user : users) {
            sendMessage(message, user.getIp(), user.getPort());
        }
    }

//    private void processMessage(DatagramPacket packet) {
//        String message = (new String(packet.getData())).trim();
//        System.out.println("Mensaje recibido: " + message);
//        String[] parts = message.split("\\$");
//
//        int indexRemitter = searchUser(packet.getAddress(), packet.getPort());
//        int indexReceiver = (indexRemitter == 0) ? 1 : 0;
//
//        if(parts[0].equals("connect")) {
//            if(indexRemitter == -1) {
//                sendMessage("alreadyConnected", packet.getAddress(), packet.getPort());
//                System.out.println(("el usuario ya esta conectado"));
//                return;
//            }
//            if(this.connectedUsers < this.MAX_CLIENTS) {
//                this.sendMessage("connected", packet.getAddress(), packet.getPort());
//                System.out.println("cliente conectado");
//                User newUser = new User(parts[1], packet.getAddress(), packet.getPort());
//                this.users.add(newUser);
//                this.connectedUsers++;
//            } else {
//                this.sendMessage("serverfull",  packet.getAddress(), packet.getPort());
//                System.out.println("servidor lleno");
//            }
//        } else if(parts[0].equals("disconnect")) {
//            if(indexRemitter == -1) {
//                if(this.connectedUsers > 1) {
//                    this.sendMessage("usuario desconectado",this.users.get(indexReceiver).getIp(), this.users.get(indexReceiver).getPort());
//                    this.connectedUsers--;
//                    this.users.remove(indexRemitter);
//                }
//            }
//        } else {
//            if(indexRemitter == -1) {
//                System.out.println("el usuario desconectado, no se puede procesar el mensaje");
//                return;
//            }
//             if(this.connectedUsers < this.MAX_CLIENTS) {
//                 this.sendMessage("waitingotheruser",  packet.getAddress(), packet.getPort());
//                 System.out.println("esperando otro usuario");
//                 return;
//             }
//
//             switch (parts[0]) {
//                 case "newmessage":
//                     String messageChat = "mensajechat$" + this.users.get(indexRemitter).getUsername() + "dice: " + parts[1];
//                     this.sendMessage(messageChat, this.users.get(indexReceiver).getIp(), this.users.get(indexReceiver).getPort());
//                     break;
//             }
//        }
//    }
    private void processMessage(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
//        System.out.println("Mensaje recibido: " + message);
        String[] parts = message.split("\\$");

        switch (parts[0]) {
            case "connect":
                sendMessage("connected", packet.getAddress(), packet.getPort());
                this.connectedUsers++;
                User newUser = new User("usuario"+connectedUsers, packet.getAddress(), packet.getPort());
                this.users.add(newUser);
                netManager.placeNewPlayer(connectedUsers);
                this.pingEveryone(netManager.getNewPlayerPos(connectedUsers));
                break;
            case "move":
                move = parts[1];
                clientIndexed = searchUser(packet);
                netManager.moveCar(parts[1], searchUser(packet));
//                sendMessage(netManager.updateMetrics(searchUser(packet)),  packet.getAddress(), packet.getPort());
                break;
        }
    }

    private int searchUser(DatagramPacket packet) {
        int i = 0;
        int indexUser = -1;
        while (i < users.size() && indexUser == -1) {
            User user = this.users.get(i);
            String id = packet.getAddress()+":"+packet.getPort();
            if(id.equals(user.getId())) {
                indexUser = i;
            }
            i++;
        }
        return indexUser;
    }

    public void sendMessage(String message, InetAddress ipDestination, int port) {
        byte[] datosByte = message.getBytes();
        DatagramPacket packet = new DatagramPacket(datosByte, datosByte.length, ipDestination, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
