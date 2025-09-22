package online;

import com.badlogic.gdx.Net;

import java.io.IOException;
import java.net.*;

public class Client extends Thread{
    private DatagramSocket socket;
    private boolean end = false;
    private String stringServerIp ="255.255.255.255";
    private InetAddress ipServer;
    private int serverPort = 5555;
    private NetManager netManager;

    public Client(NetManager netManager){
        this.netManager = netManager;
        try{
            System.out.println("cliente iniciado perrito");
            this.ipServer = InetAddress.getByName(this.stringServerIp);
            socket = new DatagramSocket();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            System.out.println("esperando el mensaje");
            try{
                socket.receive(packet);
                procesarMensaje(packet);
            }catch (IOException e){

            }
        } while (!end);
    }

    private void procesarMensaje(DatagramPacket packet) {
        String mensaje = (new String(packet.getData())).trim();
        System.out.println("mensaje: "+mensaje);

        String[] parts = mensaje.split("\\$");

        switch (parts[0]){
            case "connected":
                this.netManager.connect(true);
                System.out.println("se conecto perfectamente");
                this.ipServer = packet.getAddress();
                break;
            case "serverfull":
                this.netManager.connect(false);
                System.out.println("el servidor esta lleno de personas");
                break;
            case "alreadyconnected":
                System.out.println("ya estas conectado");
                break;
            case "waitingotheruser":
                System.out.println("el contricante no se conecto todavia");
                break;
            case "newmessage":
                System.out.println(parts[1]);
                break;
            case "contricantdisconnected":
                System.out.println("el contricante se desconecto");
                break;
            case "serverclosed":
                netManager.connect(false);
                System.out.println("el servidor de apago");
                break;
        }
    }

    private void sendMessage(String message) {
        byte[] bytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.ipServer, this.serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finish() {
        this.end = true;
        socket.close();
        this.interrupt();
    }

}
