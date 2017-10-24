package server;

import client.ClientManager;
import client.DboxClient;
import hashoperations.SocketFile;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FileOperationSocket {
    private static final int MB = 1000000;
    private boolean flag;
    private PrintWriter os;
    private ServerSocket[] sockets;
    private byte[] data;
    private BufferedReader is;

    public FileOperationSocket(boolean flag, byte[] data,PrintWriter os,ServerSocket[] sockets) {
        this.flag=flag;
        this.data = data;
        this.os = os;
        this.sockets = sockets;
    }
    public FileOperationSocket(boolean flag,PrintWriter os, BufferedReader is,ServerSocket[] sockets){
        this.flag = flag;
        this.is = is;
        this.sockets = sockets;
        this.os = os;
    }

    protected void operateUpload(){
        if(!flag){
            os.println("RECEIVE:PORT");
            os.flush();
            try {
                String port = is.readLine();
                if(port.equals("NONE")){
                    System.out.println("File does not exist on the client.");
                    return;
                }
                //--> correct form (working) System.out.println(port);
                if(port.equals("SINGLE")){
                    try {
                        System.out.println("SINGLE THREAD RECEIVE --<EXEC>--");
                        Socket s = sockets[0].accept();
                        ReceiveTask task = new ReceiveTask(s);
                        task.start();
                        task.join();
                        String fileName = task.getFileName();
                        byte[] dat = task.getData();
                        FileOutputStream fos;
                        try {
                            String nameOfFile = fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
                            fos = new FileOutputStream(DboxServer.PATH_OF_SERVER+nameOfFile);
                            fos.write(dat);
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dat = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else if(port.equals("DOUBLE")){
                    System.out.println("DOUBLE THREAD RECEIVE --<EXEC>--");
                    final byte[][] out1 = new byte[1][];
                    final byte[][] out2= new byte[1][];
                    final String[] fileName = new String[1];
                    Thread firstThread = new Thread(() -> {
                        try{
                            Socket s = sockets[0].accept();
                            ReceiveTask task = new ReceiveTask(s);
                            task.start();
                            task.join();
                            fileName[0] = task.getFileName();
                            out1[0] = task.getData();
                        }catch(InterruptedException | IOException ex){
                            ex.printStackTrace();
                        }
                    });
                    Thread secondThread = new Thread(() -> {
                        try{
                            Socket s = sockets[1].accept();
                            ReceiveTask task = new ReceiveTask(s);
                            task.start();
                            task.join();
                            out2[0] = task.getData();
                        }catch(InterruptedException | IOException ex){
                            ex.printStackTrace();
                        }
                    });
                    firstThread.start();
                    secondThread.start();
                    try{
                        firstThread.join();
                        secondThread.join();
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    try {
                        outStream.write(out1[0]);
                        outStream.write(out2[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] fin = outStream.toByteArray();
                    FileOutputStream fos;
                    try {
                        String nameOfFile = fileName[0].substring(fileName[0].lastIndexOf("/")+1,fileName[0].length());
                        fos = new FileOutputStream(DboxServer.PATH_OF_SERVER+nameOfFile);
                        fos.write(fin);
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out1[0] = null;
                    out2[0] = null;
                }else if(port.equals("TRIPLE")){
                    System.out.println("TRIPLE THREAD RECEIVE --<EXEC>--");
                    final byte[][] out1 = new byte[1][];
                    final byte[][] out2= new byte[1][];
                    final byte[][] out3= new byte[1][];
                    final String[] fileName = new String[1];
                    Thread firstThread = new Thread(() -> {
                        try{
                            Socket s = sockets[0].accept();
                            ReceiveTask task = new ReceiveTask(s);
                            task.start();
                            task.join();
                            fileName[0] = task.getFileName();
                            out1[0] = task.getData();
                        }catch(InterruptedException | IOException ex){
                            ex.printStackTrace();
                        }
                    });
                    Thread secondThread = new Thread(() -> {
                        try{
                            Socket s = sockets[1].accept();
                            ReceiveTask task = new ReceiveTask(s);
                            task.start();
                            task.join();
                            out2[0] = task.getData();
                        }catch(InterruptedException | IOException ex){
                            ex.printStackTrace();
                        }
                    });
                    Thread thirdThread = new Thread(() -> {
                        try{
                            Socket s = sockets[2].accept();
                            ReceiveTask task = new ReceiveTask(s);
                            task.start();
                            task.join();
                            out3[0] = task.getData();
                        }catch(InterruptedException | IOException ex){
                            ex.printStackTrace();
                        }
                    });
                    firstThread.start();
                    secondThread.start();
                    thirdThread.start();
                    try{
                        firstThread.join();
                        secondThread.join();
                        thirdThread.join();
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    try {
                        outStream.write(out1[0]);
                        outStream.write(out2[0]);
                        outStream.write(out3[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] fin = outStream.toByteArray();
                    FileOutputStream fos;
                    try {
                        outStream.close();
                        String nameOfFile = fileName[0].substring(fileName[0].lastIndexOf("/")+1,fileName[0].length());
                        fos = new FileOutputStream(DboxServer.PATH_OF_SERVER+nameOfFile);
                        fos.write(fin);
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out1[0] = null;
                    out2[0] = null;
                    out3[0] = null;
                }else if(port.equals("QUAD")){
                    System.out.println("QUAD THREAD RECEIVE --<EXEC>--");
                    final byte[][] out1 = new byte[1][];
                    final byte[][] out2= new byte[1][];
                    final byte[][] out3= new byte[1][];
                    final byte[][] out4= new byte[1][];
                    final String[] fileName = new String[1];
                    Thread firstThread = new Thread(() -> {
                        try{
                            Socket s = sockets[0].accept();
                            ReceiveTask task = new ReceiveTask(s);
                            task.start();
                            task.join();
                            fileName[0] = task.getFileName();
                            out1[0] = task.getData();
                            //System.out.println(task.getData().length);
                        }catch(InterruptedException | IOException ex){
                            ex.printStackTrace();
                        }
                    });
                    Thread secondThread = new Thread(() -> {
                        try{
                            Socket s = sockets[1].accept();
                            ReceiveTask task = new ReceiveTask(s);
                            task.start();
                            task.join();
                            out2[0] = task.getData();
                            //System.out.println(task.getData().length);
                        }catch(InterruptedException | IOException ex){
                            ex.printStackTrace();
                        }
                    });
                    Thread thirdThread = new Thread(() -> {
                        try{
                            Socket s = sockets[2].accept();
                            ReceiveTask task = new ReceiveTask(s);
                            task.start();
                            task.join();
                            out3[0] = task.getData();
                            //System.out.println(task.getData().length);
                        }catch(InterruptedException | IOException ex){
                            ex.printStackTrace();
                        }
                    });
                    Thread fourthThread = new Thread(() -> {
                        try{
                            Socket s = sockets[3].accept();
                            ReceiveTask task = new ReceiveTask(s);
                            task.start();
                            task.join();
                            out4[0] = task.getData();
                            //System.out.println(task.getData().length);
                        }catch(InterruptedException | IOException ex){
                            ex.printStackTrace();
                        }
                    });
                    firstThread.start();
                    secondThread.start();
                    thirdThread.start();
                    fourthThread.start();
                    try{
                        firstThread.join();
                        secondThread.join();
                        thirdThread.join();
                        fourthThread.join();
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    try {
                        outStream.write(out1[0]);
                        outStream.write(out2[0]);
                        outStream.write(out3[0]);
                        outStream.write(out4[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] fin = outStream.toByteArray();
                    FileOutputStream fos;
                    try {
                        outStream.close();
                        String nameOfFile = fileName[0].substring(fileName[0].lastIndexOf("/")+1,fileName[0].length());
                        fos = new FileOutputStream(DboxServer.PATH_OF_SERVER+nameOfFile);
                        fos.write(fin);
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out1[0] = null;
                    out2[0] = null;
                    out3[0] = null;
                    out4[0] = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void operateDownload(String filename){
        if(100*MB > data.length){
            //1 thread
            if(flag){
                //send
                try {
                    os.println("READY:PORT:SINGLE:"+filename);
                    os.flush();
                    Socket s = sockets[0].accept();
                    SendTask task = new SendTask(s,data);
                    task.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                //receive
            }
        }else if(200*MB > data.length){
            //2 thread
            if(flag){
                //send
                os.println("READY:PORT:DOUBLE:"+filename);
                os.flush();
                List<byte[]> listOfFile = FileOperationSocket.divideArray(data,(data.length/2));
                List<Thread> threadList = new ArrayList<>();
                for(int i = 0; i < 2 ;i++){
                    int finalI = i;
                    Thread thread = new Thread(() -> {
                        try{
                            Socket s = sockets[finalI].accept();
                            //System.out.println("Length of data -->" + listOfFile.get(finalI).length);
                            if(finalI == 0){
                                SendTask task = new SendTask(s,listOfFile.get(finalI));
                                task.start();
                                task.join();
                            }else if(finalI == 1){
                                if(listOfFile.size() > 2){
                                    ByteArrayOutputStream outTest = new ByteArrayOutputStream();
                                    outTest.write(listOfFile.get(1));
                                    outTest.write((listOfFile.get(2)));
                                    byte[] fin = outTest.toByteArray();
                                    outTest.close();
                                    SendTask task = new SendTask(s,fin);
                                    task.start();
                                    task.join();
                                    fin = null;
                                }else{
                                    SendTask task = new SendTask(s,listOfFile.get(finalI));
                                    task.start();
                                    task.join();
                                }
                            }
                        }catch(IOException ex){
                            ex.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    threadList.add(thread);
                }
                for(Thread t : threadList){
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                listOfFile.clear();
            }else{
                //receive
                int startPort = SocketManager.DEFAULT+1;
                for(int i = 0; i < 2 ;i++){
                    try{
                        ServerSocket sock = new ServerSocket((startPort));
                        Socket s = sock.accept();
                        ReceiveTask task = new ReceiveTask(s);
                        task.start();
                        startPort++;
                    }catch(IOException ex){
                        ex.printStackTrace();
                    }

                }
            }
        }else if(300*MB > data.length){
            //3 thread
            if(flag){
                //send
                os.println("READY:PORT:TRIPLE:"+filename);
                os.flush();
                List<byte[]> listOfFile = FileOperationSocket.divideArray(data,(data.length/3));
                List<Thread> threadList = new ArrayList<>();
                for(int i = 0; i < 3 ;i++){
                    int finalI = i;
                    Thread thread =  new Thread(() -> {
                        try{
                            Socket s = sockets[finalI].accept();
                            //System.out.println("Length of data -->" + listOfFile.get(finalI).length);
                            if(finalI == 0){
                                SendTask task = new SendTask(s,listOfFile.get(finalI));
                                task.start();
                                task.join();
                            }else if(finalI == 1){
                                SendTask task = new SendTask(s,listOfFile.get(finalI));
                                task.start();
                                task.join();
                            }else if(finalI == 2){
                                if(listOfFile.size() > 3){
                                    ByteArrayOutputStream outTest = new ByteArrayOutputStream();
                                    outTest.write(listOfFile.get(finalI));
                                    outTest.write((listOfFile.get(finalI+1)));
                                    byte[] fin = outTest.toByteArray();
                                    outTest.close();
                                    SendTask task = new SendTask(s,fin);
                                    task.start();
                                    task.join();
                                }else{
                                    SendTask task = new SendTask(s,listOfFile.get(finalI));
                                    task.start();
                                    task.join();
                                }
                            }
                        }catch(IOException ex){
                            ex.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    threadList.add(thread);
                }
                for(Thread t : threadList){
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                listOfFile.clear();
                threadList.clear();
            }else{

            }
        }else{
            //4 thread
            if(flag){
                //send
                os.println("READY:PORT:QUAD:"+filename);
                os.flush();
                List<byte[]> listOfFile = FileOperationSocket.divideArray(data,(data.length/4));
                List<Thread> threadList = new ArrayList<>();
                for(int i = 0; i < 4 ;i++){
                    int finalI = i;
                    Thread thread = new Thread(() -> {
                        try{
                            Socket s = sockets[finalI].accept();
                            //System.out.println("Length of data -->" + listOfFile.get(finalI).length);
                            if(finalI == 0){
                                SendTask task = new SendTask(s,listOfFile.get(finalI));
                                task.start();
                                task.join();
                            }else if(finalI == 1){
                                SendTask task = new SendTask(s,listOfFile.get(finalI));
                                task.start();
                                task.join();
                            }else if(finalI == 2){
                                SendTask task = new SendTask(s,listOfFile.get(finalI));
                                task.start();
                                task.join();
                            }else if(finalI == 3){
                                if(listOfFile.size() > 4){
                                    ByteArrayOutputStream outTest = new ByteArrayOutputStream();
                                    outTest.write(listOfFile.get(finalI));
                                    outTest.write((listOfFile.get(finalI+1)));
                                    byte[] fin = outTest.toByteArray();
                                    outTest.close();
                                    SendTask task = new SendTask(s,fin);
                                    task.start();
                                    task.join();
                                }else{
                                    SendTask task = new SendTask(s,listOfFile.get(finalI));
                                    task.start();
                                    task.join();
                                }
                            }
                        }catch(IOException ex){
                            ex.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    threadList.add(thread);
                }
                for(Thread t : threadList){
                    try{
                        t.join();
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
                listOfFile.clear();
                threadList.clear();
            }else{

            }
        }
    }

    private class SendTask extends Thread{
        private Socket s;
        private byte[] data;
        private DataOutputStream out;
        @Override
        public void run() {
            try {
                String hashValue = SocketFile.hashOfByteArray(MessageDigest.getInstance("SHA-256"),data);
                out = new DataOutputStream(s.getOutputStream());
                out.writeInt(data.length);
                out.flush();
                out.writeUTF(hashValue);
                out.flush();
                if(data.length > 0){
                    out.write(data,0,data.length);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        public SendTask(Socket s, byte[] data){
            this.s = s;
            this.data = data;
        }
    }

    private class ReceiveTask extends Thread{
        private Socket s;
        private DataInputStream in;
        private String fileName;
        private byte[] data;
        @Override
        public void run() {
            try {
                in = new DataInputStream(s.getInputStream());
                int size = in.readInt();
                this.fileName = in.readUTF();
                String hashValue = in.readUTF();
                byte[] dat = new byte[size];
                if(size > 0){
                    in.readFully(dat);
                }
                this.data = dat;
                s.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public ReceiveTask(Socket s){
            this.s = s;
        }

        public String getFileName(){
            return fileName;
        }

        public byte[] getData(){
            return data;
        }
    }

    private static List<byte[]> divideArray(byte[] source, int chunkSize) {

        List<byte[]> result = new ArrayList<>();
        int start = 0;
        while (start < source.length) {
            int end = Math.min(source.length, start + chunkSize);
            result.add(Arrays.copyOfRange(source, start, end));
            start += chunkSize;
        }

        return result;
    }
}
