package server;

import hashoperations.SocketFile;
import org.apache.commons.cli.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandSocket extends Thread {
    private BufferedReader is;
    private PrintWriter os;
    private DataOutputStream out;
    private Socket sock;
    private String line;
    private ServerSocket[] dataSockets;
    private ArrayList<SocketFile> lastSyncFiles;
    private ArrayList<SocketFile> lastSyncWithDropbox;


    public CommandSocket(Socket s, ServerSocket[] dataSockets) {
        this.sock = s;
        this.dataSockets = dataSockets;
        this.lastSyncFiles = SocketManager.mainLastSync;
    }

    @Override
    public void run() {
        try {
            out = new DataOutputStream(sock.getOutputStream());
            line = new String();
            is = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            os = new PrintWriter(sock.getOutputStream());
        } catch (IOException e) {
            Logger.getLogger(String.valueOf(CommandSocket.class)).log(Level.SEVERE, e.toString());
        }
        try {
            System.out.println("Server is ready to accept strings.");
            line = is.readLine();
            while (line.compareTo("quit") != 0) {
                try{
                    parseClientCommands(line);
                }catch(org.apache.commons.cli.ParseException ex){
                    System.out.println("Command is not recognized.");
                    os.println("UNRECOGNIZED COMMAND");
                    os.flush();
                }catch(ArrayIndexOutOfBoundsException ex){
                    System.out.println("Command is not in the correct format");
                    os.println("COMMAND FORMAT ERROR");
                    os.flush();
                }
                System.out.println("Client " + sock.getRemoteSocketAddress() + " sent : " + line);
                line = is.readLine();
                //System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(String.valueOf(CommandSocket.class)).log(Level.SEVERE, ex.toString());
        } catch(NullPointerException ex){
            System.out.println("Client disconnected.");
        }
        finally {
            try {
                System.out.println("Closing the connection");
                if (is != null) {
                    is.close();
                    System.out.println(" Socket Input Stream Closed");
                }

                if (os != null) {
                    os.close();
                    System.out.println("Socket Out Closed");
                }
                if (sock != null) {
                    sock.close();
                    System.out.println("Socket Closed");
                }
                if (out != null) {
                    out.close();
                }

            } catch (IOException ie) {
                System.err.println("Socket Close Error");
            }
        }//end finally

    }

    private ArrayList<SocketFile> socketFileOfServer(){
        ArrayList<SocketFile> ret = new ArrayList<>();
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            File folder = new File(DboxServer.PATH_OF_SERVER);
            File[] directory = folder.listFiles();
            for (File f : directory){
                if(f.isFile()){
                    String hashValue = SocketFile.getFileChecksum(sha256,f);
                    System.out.println(f.getName() + "--> hash is-->" + hashValue);
                    ret.add(new SocketFile(f.getName(), hashValue, String.valueOf(f.length()), String.valueOf(f.lastModified())));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(String.valueOf(CommandSocket.class)).log(Level.SEVERE,e.toString());
        }catch(IOException ex){
            Logger.getLogger(String.valueOf(CommandSocket.class)).log(Level.SEVERE,ex.toString());
        }
        return ret;
    }

    private void sendChanges(ArrayList<String> ListOfFiles){

        String temp = "";


        if(!ListOfFiles.isEmpty()) {
            for(String fileString : ListOfFiles) {
                temp += fileString;
                temp += ";";
            }
            temp = temp.substring(0,temp.length()-1);
        }
        else {
            temp += "NULL";
        }

        /*for(String fileString : ListOfFiles) {
            temp += fileString;
            temp += ";";
        }
        temp = temp.substring(0,temp.length()-1);*/


        ArrayList<String> syncList = new ArrayList<>();
        for(SocketFile file : lastSyncFiles) {
            syncList.add(file.name + ":" + file.hashValue + ":" + file.size + ":" + file.lastUpdateTime);
        }

        temp += "!";

        if(!syncList.isEmpty()){
            for(String fileString : syncList) {
                temp += fileString;
                temp += ";";
            }
            temp = temp.substring(0,temp.length()-1);
        }else{
            temp += "NULL";
        }

        //System.out.println("temp temp temp temp     " + temp);
        os.println(temp);
        os.flush();
        //System.out.println(temp);
    }

    private void parseClientCommands(String command) throws ParseException,ArrayIndexOutOfBoundsException{
        String[] cmd = command.split(" ");
        String main = cmd[0];
        String option = cmd[1];

        if(main.equals("sync")){
            if(option.equals("check")){

                ArrayList<String> sentFiles = new ArrayList<>();

                ArrayList<SocketFile> filesOfServer = socketFileOfServer();

                ArrayList<String> listOfName = new ArrayList();
                ArrayList<String> listOfHashValue = new ArrayList();
                ArrayList<String> listOfSize = new ArrayList();
                ArrayList<String> listOfLastUpdateTime = new ArrayList();

                ArrayList<String> listOfNameServer = new ArrayList();
                ArrayList<String> listOfHashValueServer = new ArrayList();
                ArrayList<String> listOfSizeServer = new ArrayList();
                ArrayList<String> listOfLastUpdateTimeServer = new ArrayList();
                //sync check parse hashes and send

                for(SocketFile file : lastSyncFiles) {
                    listOfName.add(file.name);
                    listOfHashValue.add(file.hashValue);
                    listOfSize.add(file.size);
                    listOfLastUpdateTime.add(file.lastUpdateTime);
                }

                for(SocketFile file : filesOfServer) {
                    listOfNameServer.add(file.name);
                    listOfHashValueServer.add(file.hashValue);
                    listOfSizeServer.add(file.size);
                    listOfLastUpdateTimeServer.add(file.lastUpdateTime);
                }

                for(SocketFile file : filesOfServer) {
                    if(!listOfHashValue.contains(file.hashValue)) {
                        if(listOfName.contains(file.name)) {
                            sentFiles.add(file.name + ":" + "Update(f)" + ":" + file.size + ":" + file.lastUpdateTime);
                        }
                        else {
                            sentFiles.add(file.name + ":" + "Add(f)" + ":" + file.size + ":" + file.lastUpdateTime);
                        }
                    }
                }

                for(SocketFile file : lastSyncFiles) {
                    if(!listOfHashValueServer.contains(file.hashValue)) {
                        if(listOfNameServer.contains(file.name)) {
                            sentFiles.add(file.name + ":" + "Update(f)" + ":" + file.size + ":" + file.lastUpdateTime);
                        }
                        else {
                            sentFiles.add(file.name + ":" + "Delete(f)" + ":" + file.size + ":" + file.lastUpdateTime);
                        }
                    }
                }

                sendChanges(sentFiles);
            }else{
                List<String> files = CommandSocket.fetchFromLocalDirectory();
                for(String f : files){
                    System.out.println("File name : " + f);
                    if(option.equals(f)){
                        //sync check file
                        String file_ = DboxServer.PATH_OF_SERVER + f;
                        try {
                            byte[] data = Files.readAllBytes(Paths.get(file_));
                            //Flag true for send -->
                            FileOperationSocket fileOpt = new FileOperationSocket(true,data,os,dataSockets);
                            System.out.println("Sent data length --> "+data.length);
                            fileOpt.operateDownload(f);
                        } catch (IOException e) {
                            //File not found ?
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                System.out.println("File will be received from the client");
                //If the file is not found, try to receive the file
                FileOperationSocket fileOpt = new FileOperationSocket(false,os,is,dataSockets);
                fileOpt.operateUpload();
            }

        }else if(main.equals("delete_private")){
            List<String> folder = fetchFromLocalDirectory();
            if(folder.contains(option)){
                new File(DboxServer.PATH_OF_SERVER+option).delete();
            }else{
                //wont reach here
            }
        }

        else if (main.equals("FIN")){
            lastSyncFiles = socketFileOfServer();
            SocketManager.mainLastSync = lastSyncFiles;
            os.println("Starting to sync with dropbox");
            os.flush();
            dropBoxSync();
            os.println("Ended uploading to dropbox.");
            os.flush();
        }

        else{
            throw new org.apache.commons.cli.ParseException("Command : "+main+" is not recognized.");
        }
    }
    private static List<String> fetchFromLocalDirectory(){
        //DropBoxOperations operations = new DropBoxOperations();
        List<String> ret = new ArrayList<>();
        File folder = new File(DboxServer.PATH_OF_SERVER);
        File[] directory = folder.listFiles();
        for (File f : directory){
            if(f.isFile()){
                ret.add(f.getName());
                //operations.dboxUpload(f.getName());
            }
        }
        return ret;
    }

    private void dropBoxSync(){ // checks lastsync list
        //LastSyncUpdater.Data[] data = lastSyncUpdater.getJsonData();
        DropBoxOperations operations = new DropBoxOperations();
        operations.deleteAll();
        File folder = new File(DboxServer.PATH_OF_SERVER);
        File[] dir = folder.listFiles();
        for(File f : dir){
            operations.dboxUpload(f.getName());
        }

    }

}
