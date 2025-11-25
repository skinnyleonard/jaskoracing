package online;

import com.badlogic.gdx.math.Interpolation;
import entities.Car;
import screens.PlayScreen;
import tools.MapLoader;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Server extends Thread {
    public static int clientIndexed = 0;
    public static DatagramSocket socket;
    private int port = 5555;
    private boolean end = false;
    private final int MAX_CLIENTS = 1;
    private int connectedUsers = 0;
    public static ArrayList<User> users = new ArrayList<User>();
    private NetManager netManager;
    public static String move = "";

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

    private void processMessage(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split("\\$");

        switch (parts[0]) {
            case "connect":
                if(connectedUsers == MAX_CLIENTS) {
                    sendMessage("serverfull", packet.getAddress(), packet.getPort());
                } else {
                    sendMessage("connected;"+connectedUsers, packet.getAddress(), packet.getPort());
                    this.connectedUsers++;
                    User newUser = new User(parts[2], packet.getAddress(), packet.getPort());
                    this.users.add(newUser);
                    netManager.placeNewPlayer(connectedUsers, parts[1]);
                }
                break;
            case "move":
                move = parts[1];
                clientIndexed = searchUser(packet);
                netManager.moveCar(parts[1], searchUser(packet));
                break;
            case "disconnect":
                int index = searchUser(packet);
                this.users.remove(index);
                netManager.deleteRacer(index);
                this.connectedUsers--;
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
