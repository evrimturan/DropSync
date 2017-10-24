package client;


import hashoperations.SocketFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DboxClient {

    public static void main(String[] args) {
        ClientManager x = new ClientManager(ClientManager.LOCALHOST,ClientManager.PORT);
        x.connect();
        Scanner scanner = new Scanner(System.in);
        System.out.print(">");
        String message = scanner.nextLine();

        //ArrayListler gelicek
        ArrayList<SocketFile> lastSyncList = new ArrayList<>();
        ArrayList<SocketFile> filesOfClient = new ArrayList<>();

        ArrayList<String> infoFiles = new ArrayList<>();

        ArrayList<String> listOfName = new ArrayList();
        ArrayList<String> listOfHashValue = new ArrayList();
        ArrayList<String> listOfSize = new ArrayList();
        ArrayList<String> listOfLastUpdateTime = new ArrayList();

        ArrayList<String> listOfNameClient = new ArrayList();
        ArrayList<String> listOfHashValueClient = new ArrayList();
        ArrayList<String> listOfSizeClient = new ArrayList();
        ArrayList<String> listOfLastUpdateTimeClient = new ArrayList();

        while (!message.equals("quit")) {
            //actionList parse edilecek

            String[] words = message.split(" ");
            if(words[0].equals("sync") && words.length == 2) {
                x.os.println("sync check");
                x.os.flush();
            }
            else {
                System.out.println("Command not recognized");
                System.out.print(">");
                message = scanner.nextLine();
                continue;
            }


            String receivedMsg = null;
            String hashValue = null;
            try {
                receivedMsg = x.is.readLine();
                hashValue = receivedMsg;
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println("Response from server: " + receivedMsg);
            try{
                //System.out.println(hashValue + "hashvalue değerleri");
                String[] data = hashValue.split("!");
                String changes = data[0];
                String lastSync = data[1];
                //System.out.println("LastSync -->"+lastSync);

                String[] lastSyncValues = new String[0];
                if(!lastSync.equals("NULL")) {
                    lastSyncValues = lastSync.split(";");
                }

                lastSyncList = new ArrayList<>();

                listOfName = new ArrayList();
                listOfHashValue = new ArrayList();
                listOfSize = new ArrayList();
                listOfLastUpdateTime = new ArrayList();

                listOfNameClient = new ArrayList();
                listOfHashValueClient = new ArrayList();
                listOfSizeClient = new ArrayList();
                listOfLastUpdateTimeClient = new ArrayList();

                for(String dat : lastSyncValues){
                    //System.out.println(dat);
                    String[] values = dat.split(":");
                    SocketFile recFile = new SocketFile(values[0],values[1],values[2],values[3]);
                    lastSyncList.add(recFile);
                }

                //TODO: Evrim !Unutma buraları!
                //Client dosyalarının hashleri bulunacak ve name, hashvalue, size ve last modificationla birlikte
                //SocketFile objeleri oluşturulacak onlarda array atılacak.
                filesOfClient = socketFileOfClient();
                infoFiles = new ArrayList<>();

                for(SocketFile file : lastSyncList) {
                    //System.out.println("LastSyncList" + " " + file.name + " " + file.hashValue + " " + file.size + " " + file.lastUpdateTime);
                    listOfName.add(file.name);
                    listOfHashValue.add(file.hashValue);
                    listOfSize.add(file.size);
                    listOfLastUpdateTime.add(file.lastUpdateTime);
                }

                for(SocketFile file : filesOfClient) {
                    //System.out.println("FileOfClient" + " " + file.name + " " + file.hashValue + " " + file.size + " " + file.lastUpdateTime);
                    listOfNameClient.add(file.name);
                    listOfHashValueClient.add(file.hashValue);
                    listOfSizeClient.add(file.size);
                    listOfLastUpdateTimeClient.add(file.lastUpdateTime);
                }

                for(SocketFile file : filesOfClient) {
                    if(!listOfHashValue.contains(file.hashValue)) {
                        if(listOfName.contains(file.name)) {
                            infoFiles.add(file.name + ":" + "Update(m)" + ":" + file.size + ":" + file.lastUpdateTime);
                        }
                        else {
                            infoFiles.add(file.name + ":" + "Add(m)" + ":" + file.size + ":" + file.lastUpdateTime);
                        }
                    }
                }

                for(SocketFile file : lastSyncList) {
                    if(!listOfHashValueClient.contains(file.hashValue)) {
                        //System.out.println(file.name + "  Hash YOK");
                        if(listOfNameClient.contains(file.name)) {
                            infoFiles.add(file.name + ":" + "Update(m)" + ":" + file.size + ":" + file.lastUpdateTime);
                        }
                        else {
                            infoFiles.add(file.name + ":" + "Delete(m)" + ":" + file.size + ":" + file.lastUpdateTime);
                        }
                    }
                    else {
                        //System.out.println(file.name + "  Hash VAR");
                    }
                }

                //System.out.println("Al sana liste  " + listOfHashValueClient);

                String[] fromServer = new String[0];
                if(!changes.equals("NULL")) {
                    fromServer = changes.split(";");
                }
                else {
                    //System.out.println("Changes NULL GELIYOR");
                    //System.out.println("infoFiles size:   " + infoFiles.size());
                }

                //String[] fromServer = changes.split(";");

                for(String info: fromServer) {
                    infoFiles.add(info);
                }

                String option = message.split(" ")[1];
                if(!option.equals("check")) {
                    ArrayList<String> newInfoList = new ArrayList<>();
                    for(String elm : infoFiles){
                        String fields[] = elm.split(":");
                        if(option.equals(fields[0])) {
                            newInfoList.add(elm);
                        }
                    }
                    infoFiles = newInfoList;
                }

                ArrayList<String> actionList = new ArrayList<>();

                for(String item : infoFiles) {
                    boolean last = true;
                    String[] itemValues = item.split(":");
                    for(String other : infoFiles) {
                        String[] otherValues = other.split(":");
                        if(itemValues[0].equals(otherValues[0])) {
                            if(Long.parseLong(itemValues[3]) < Long.parseLong(otherValues[3])) {
                                last = false;
                            }
                        }
                    }
                    if(last) {
                        actionList.add(item);
                    }
                }

                for(int i=0; i<actionList.size(); i++) {
                    String[] a = actionList.get(i).split(":");
                    if(a[0].equals(".DS_Store")) {
                        actionList.remove(i);
                    }
                }


                if(!words[1].equals("check")) {
                    ArrayList<String> tempList = new ArrayList<>();
                    for(int i=0; i<actionList.size(); i++) {
                        String[] a = actionList.get(i).split(":");
                        if(a[0].equals(words[1])) {
                            tempList.add(actionList.get(i));
                        }
                    }
                    actionList = tempList;
                }


                for(String a : actionList) {
                    //System.out.println("action:   " + a);
                }

                //System.out.println("ActionSize:" + actionList.size());
                List<String> clientFiles = ClientManager.fetchFromLocalDirectory();
                for(String str : actionList) {
                    //System.out.println("aaaaaaaaaaaaaaaaaaaaaa      " + str);
                    //String[] fileList = changes.split(";");

                        String[] prop = str.split(":");
                        String name = prop[0];
                        String operation = prop[1];
                        long size = Long.valueOf(prop[2]);
                        long time = Long.valueOf(prop[3]);


                        if(operation.equals("Add(m)") || operation.equals("Add(f)")) {
                            x.os.println("sync " + name);
                            x.os.flush();
                            try {
                                fileOperations(x,x.is.readLine(),"sync "+name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        else if(operation.equals("Delete(m)")){
                            x.os.println("delete_private " + name);
                            x.os.flush();
                        }

                        else if(operation.equals("Delete(f)")) {
                            new File(ClientManager.PATH_OF_CLIENT+name).delete();
                        }

                        else if(operation.equals("Update(m)")){
                            x.os.println("delete_private " + name);
                            x.os.flush();

                            x.os.println("sync "+ name);
                            x.os.flush();
                            try {
                                fileOperations(x,x.is.readLine(),"sync "+name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        else if(operation.equals("Update(f)")) {
                            new File(ClientManager.PATH_OF_CLIENT+name).delete();

                            x.os.println("sync "+ name);
                            x.os.flush();
                            try {
                                fileOperations(x,x.is.readLine(),"sync "+name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        /*if(!clientFiles.contains(name)){
                            System.out.println("File " + name + "will be received by the server");
                            x.os.println("sync " + name);
                            x.os.flush();
                            try {
                                fileOperations(x,x.is.readLine(),"sync "+name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else{
                            if(time > new File(ClientManager.PATH_OF_CLIENT+name).lastModified()){
                                System.out.println("Server file is more recent, downloading.");
                                new File(ClientManager.PATH_OF_CLIENT+str).delete();
                                x.os.println("sync "+ name);
                                x.os.flush();
                                try {
                                    fileOperations(x,x.is.readLine(),"sync "+name);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                x.os.println("delete_private " + name);
                                x.os.flush();
                                System.out.println("Server file is older, uploading.");
                                x.os.println("sync "+ name);
                                x.os.flush();
                                try {
                                    fileOperations(x,x.is.readLine(),"sync "+name);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            //finally
                            clientFiles.remove(name);
                        }*/
                    }
                    /*for(String clientFile: clientFiles){
                        x.os.println("sync "+clientFile);
                        x.os.flush();
                        try {
                            fileOperations(x,x.is.readLine(),"sync "+clientFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*/

                    x.os.println("FIN EL");
                    x.os.flush();

                try {
                    String _final = x.is.readLine();
                    System.out.println(_final);
                    String abs_final = x.is.readLine();
                    System.out.println(abs_final);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }catch(ArrayIndexOutOfBoundsException ex){
                //System.out.println("cannot parse hash-value string");//which means this is not a sync check request
            }
            fileOperations(x,receivedMsg,message);
            System.out.print(">");
            message = scanner.nextLine();
        }
        x.disconnect();
    }

    private static void fileOperations(ClientManager x,String receivedMsg,String message){
        try{
            //data transfer operations DO NOT MODIFY EVRIM
            String[] arguments = receivedMsg.split(":");
            if(arguments[0].equals("READY") && arguments[1].equals("PORT")){
                FileOperator receive = new FileOperator(arguments[2],arguments[3]);
                if(receive.receiveFile()){
                    System.out.println("File : "+arguments[3]+" Received.");
                }else{
                    System.out.println("File failed to be acquired.");
                }
            }else if(arguments[0].equals("RECEIVE")&& arguments[1].equals("PORT")){
                String fileName = message.split(" ")[1];
                System.out.println(ClientManager.PATH_OF_CLIENT+fileName);
                FileOperator upload = new FileOperator(Files.readAllBytes(Paths.get(ClientManager.PATH_OF_CLIENT+fileName)),ClientManager.PATH_OF_CLIENT+fileName);
                if(upload.uploadFile(x.os)){
                    System.out.println("File : "+fileName+" uploaded");
                }else{
                    System.out.println("File failed to be uploaded.");
                }
            }

        }catch (ArrayIndexOutOfBoundsException ex){
            System.out.println("Arguments cannot be parsed.");
            x.os.println("COMMAND FORMAT ERROR");
            x.os.flush();
            try {
                System.out.println(x.is.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            x.os.println("NONE");
            x.os.flush();
            System.out.println("Local client file could not be load ! Aborting");
        }
    }

    private static ArrayList<SocketFile> socketFileOfClient(){
        ArrayList<SocketFile> ret = new ArrayList<>();
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            File folder = new File(ClientManager.PATH_OF_CLIENT);
            File[] directory = folder.listFiles();
            for (File f : directory){
                if(f.isFile()){
                    String hashValue = SocketFile.getFileChecksum(sha256,f);
                    //System.out.println(f.getName() + "--> hash is-->" + hashValue);
                    ret.add(new SocketFile(f.getName(), hashValue, String.valueOf(f.length()), String.valueOf(f.lastModified())));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(String.valueOf(ClientManager.class)).log(Level.SEVERE,e.toString());
        }catch(IOException ex){
            Logger.getLogger(String.valueOf(ClientManager.class)).log(Level.SEVERE,ex.toString());
        }
        return ret;
    }

}
