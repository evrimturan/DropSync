package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import hashoperations.SocketFile;

public class SocketManager {
    protected static ArrayList<SocketFile> mainLastSync;
    private ServerSocket sock;
    private ServerSocket[] dataSockets;
    public static final int DEFAULT = 4444;
    public SocketManager(){
        try {
            mainLastSync = new ArrayList<>();
            System.out.println("Starting server...");
            sock = new ServerSocket(DEFAULT);
            dataSockets = new ServerSocket[4];
            dataSockets[0] = new ServerSocket(DEFAULT + 1);
            dataSockets[1] = new ServerSocket(DEFAULT + 2);
            dataSockets[2] = new ServerSocket(DEFAULT + 3);
            dataSockets[3] = new ServerSocket(DEFAULT + 4);
            System.out.println("Opened port on " + DEFAULT);
        } catch (IOException e) {
            Logger.getLogger(String.valueOf(SocketManager.class)).log(Level.SEVERE,e.toString());
        }
        while(true){
            createCommandConnection();
            System.out.println("Creating command line connection...");
        }

    }
    private void createCommandConnection(){
        Socket s;
        try {
            s = sock.accept();
            CommandSocket cmd = new CommandSocket(s,dataSockets);
            cmd.start();
            System.out.println("Cmd connection started");
        } catch (IOException e) {
            Logger.getLogger(String.valueOf(SocketManager.class)).log(Level.SEVERE,e.toString());
        }

    }


}
