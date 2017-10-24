package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientManager {
    public static final String PATH_OF_CLIENT = "clientdata/";
    private Socket s;
    protected BufferedReader is;
    protected PrintWriter os;
    protected DataInputStream in;

    protected String serverAddress;
    protected int serverPort;

    public static final int PORT = 4444;
    public static final String LOCALHOST = "172.20.41.125";
    public static final String ADDRESS = "172.20.88.49";

    public ClientManager(String address, int port){
        serverAddress = address;
        serverPort    = port;

    }

    public void connect(){
        try{
            s = new Socket(serverAddress, serverPort);
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = new PrintWriter(s.getOutputStream());
            in = new DataInputStream(s.getInputStream());
            //in.close();
            System.out.println("Successfully connected to " + serverAddress + " on port " + serverPort);
        }catch(IOException ex){
            Logger.getLogger(String.valueOf(ClientManager.class)).log(Level.SEVERE,ex.toString());
            System.exit(1);
        }
    }
    public void disconnect(){
        try {
            is.close();
            os.close();
            //br.close();
            s.close();
            in.close();
            System.out.println("ConnectionToServer. SendForAnswer. Connection Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static List<String> fetchFromLocalDirectory(){
        //DropBoxOperations operations = new DropBoxOperations();
        List<String> ret = new ArrayList<>();
        File folder = new File(ClientManager.PATH_OF_CLIENT);
        File[] directory = folder.listFiles();
        for (File f : directory){
            if(f.isFile()){
                ret.add(f.getName());
                //operations.dboxUpload(f.getName());
            }
        }
        return ret;
    }
/*
    private List<String> parseHashes(DataInputStream in){
        List<String> ret = new ArrayList<>();
        try{
            boolean done = false;
            while(!done){
                byte messageType = in.readByte();
                if(messageType == -1){
                    done = true;
                }else{
                    ret.add(in.readUTF());
                }
            }
        }catch(IOException ex){
            Logger.getLogger(String.valueOf(ClientManager.class)).log(Level.SEVERE,ex.toString());
        }
        return ret;
    }

    private void checkIntegrityOfClient(){
        List<String> hashes = parseHashes(in);
        List<String> hashesUpdate = new ArrayList<>();
        for(String hash : hashes){
            hashesUpdate.add(hash.split(":")[1]);
        }
        File folder = new File(PATH_OF_CLIENT);
        File[] directory = folder.listFiles();
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            for(File f : directory){
                if(f.isFile()){
                    byte[] buffer= new byte[8192];
                    int count;
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f.getPath()));
                    while ((count = bis.read(buffer)) > 0) {
                        sha256.update(buffer, 0, count);
                    }
                    byte[] digest = sha256.digest();
                    if(hashesUpdate.contains(new BASE64Encoder().encode(digest))){
                        hashesUpdate.remove(new BASE64Encoder().encode(digest));
                    }
                }
            }
            if(hashesUpdate.isEmpty()){
                System.out.println("All the files checked out with the master server (same)");
            }else{
                for(String remain : hashesUpdate){
                    System.out.println(remain);
                }
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(String.valueOf(ClientManager.class)).log(Level.SEVERE,ex.toString());
        } catch (IOException ex){
            Logger.getLogger(String.valueOf(ClientManager.class)).log(Level.SEVERE,ex.toString());
        }
    }
    */
}
