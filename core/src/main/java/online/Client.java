package online;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends Thread{
    private DatagramSocket socket;
    private boolean end = false;
    private String stringServerIp ="255.255.255.255";
    private InetAddress ipServer;
    private int serverPort = 5555;
    private NetManager netManager;
    public static ConcurrentLinkedQueue<Vector3> positionUpdates = new ConcurrentLinkedQueue<Vector3>();
    public Client(NetManager netManager){
        try{
            this.netManager = netManager;
            this.ipServer = InetAddress.getByName(this.stringServerIp);
            socket = new DatagramSocket();
        } catch (SocketException | UnknownHostException e) {
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try{
                socket.receive(packet);
                procesarMensaje(packet);
                Thread.sleep(1);
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (!end);
    }
    private void procesarMensaje(DatagramPacket packet) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            DataInputStream dis = new DataInputStream(bais);
            String type = dis.readUTF();

            switch (type) {
                case "updatePos":
                    float x = dis.readFloat();
                    float y = dis.readFloat();
                    float angle = dis.readFloat();
                    positionUpdates.add(new Vector3(x, y, angle));
                    break;
            }
        } catch (IOException e){
            String mensaje = (new String(packet.getData(), 0, packet.getLength())).trim();
            String[] parts = mensaje.split(";");

            switch (parts[0]){
                case "connected":
                    this.netManager.connect(true, Integer.valueOf(parts[1]));
                    this.ipServer = packet.getAddress();
                    break;
                case "updateOthersPos":
                    netManager.updateOtherPos(parts[1]);
                    break;
                case "newCar":
                    netManager.createSpritePlayer(parts[1]);
                    break;
                case "updateGrid":
                    netManager.updateGrid(parts[1]);
                    break;
                case "ended":
                    netManager.checkFinished(Integer.parseInt(parts[1]));
                    System.err.println("el auto "+parts[1]+" termino");
                    end = true;
                    break;
                case "serverfull":
                    this.netManager.connect(false, -1);
                    break;
                case "contricantdisconnected":
                    netManager.deleteUser(parts[1]);
                    break;
                case "serverClosed":
                    netManager.connect(false, -2);
                    finish();
                    break;
            }
        }
    }

    public void sendMessage(String message) {
        byte[] bytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.ipServer, this.serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finish() {
        sendMessage("disconnect");
        this.end = true;
        socket.close();
        this.interrupt();
    }
}
