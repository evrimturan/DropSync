package client;

import hashoperations.SocketFile;
import sun.plugin2.message.Message;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileOperator {
    private String portAmount;
    private String fileName;
    private byte[] data;
    private static final int MB = 1000000;

    public FileOperator(String portAmount, String fileName){
        this.portAmount = portAmount;
        this.fileName = fileName;
    }

    public FileOperator(byte[] data,String fileName){
        this.data = data;
        this.fileName = fileName;
    }

    protected boolean uploadFile(PrintWriter os){
        if(data == null || data.length == 0){
            System.out.println("Empty file");
            os.println("NONE");
            os.flush();
            return false;
        }
        //will be implemented
        if(100*MB > data.length){
            System.out.println("SINGLE THREAD SENT --<EXEC>--");
            os.println("SINGLE");
            os.flush();
            Thread singleThread = new Thread(() -> {
                try {
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4445);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeInt(data.length);
                    out.writeUTF(fileName);
                    out.writeUTF(SocketFile.hashOfByteArray(sha256,data));
                    if(data.length > 0){
                        out.write(data,0,data.length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            });
            singleThread.start();
            try{
                singleThread.join();
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            data = null;
        }else if(200*MB >data.length){
            System.out.println("DOUBLE THREAD SENT --<EXEC>--");
            os.println("DOUBLE");
            os.flush();
            List<byte[]> listOfData = FileOperator.divideArray(data,data.length/2);
            Thread firstThread = new Thread(() -> {
                try{
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4445);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeInt(listOfData.get(0).length);
                    out.writeUTF(fileName);
                    out.writeUTF(SocketFile.hashOfByteArray(sha256,listOfData.get(0)));
                    if(listOfData.get(0).length > 0){
                        out.write(listOfData.get(0),0,listOfData.get(0).length);
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }catch(NoSuchAlgorithmException ex){
                    ex.printStackTrace();
                }
            });
            Thread secondThread = new Thread(() -> {
                try{
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4446);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    if(listOfData.size() > 2){
                        ByteArrayOutputStream outByte = new ByteArrayOutputStream();
                        outByte.write(listOfData.get(1));
                        outByte.write(listOfData.get(2));
                        byte[] fin = outByte.toByteArray();
                        outByte.close();
                        out.writeInt(fin.length);
                        out.writeUTF(fileName);
                        out.writeUTF(SocketFile.hashOfByteArray(sha256,fin));
                        if(fin.length > 0){
                            out.write(fin,0,fin.length);
                        }
                    }else{
                        out.writeInt(listOfData.get(1).length);
                        out.writeUTF(fileName);
                        out.writeUTF(SocketFile.hashOfByteArray(sha256,listOfData.get(1)));
                        if(listOfData.get(1).length > 0){
                            out.write(listOfData.get(1),0,listOfData.get(1).length);
                        }
                    }

                }catch(IOException ex){
                    ex.printStackTrace();
                }catch(NoSuchAlgorithmException ex){
                    ex.printStackTrace();
                }
            });
            firstThread.start();
            secondThread.start();
            try{
                firstThread.join();
                secondThread.join();
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            listOfData.clear();
        }else if(300*MB > data.length){
            System.out.println("TRIPLE THREAD SENT --<EXEC>--");
            os.println("TRIPLE");
            os.flush();
            List<byte[]> listOfData = FileOperator.divideArray(data,data.length/3);
            Thread firstThread = new Thread(() -> {
                try{
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4445);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeInt(listOfData.get(0).length);
                    out.writeUTF(fileName);
                    out.writeUTF(SocketFile.hashOfByteArray(sha256,listOfData.get(0)));
                    if(listOfData.get(0).length > 0){
                        out.write(listOfData.get(0),0,listOfData.get(0).length);
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }catch(NoSuchAlgorithmException ex){
                    ex.printStackTrace();
                }
            });
            Thread secondThread = new Thread(() -> {
                try{
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4446);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeInt(listOfData.get(0).length);
                    out.writeUTF(fileName);
                    out.writeUTF(SocketFile.hashOfByteArray(sha256,listOfData.get(1)));
                    if(listOfData.get(1).length > 0){
                        out.write(listOfData.get(1),0,listOfData.get(1).length);
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }catch(NoSuchAlgorithmException ex){
                    ex.printStackTrace();
                }
            });
            Thread thirdThread = new Thread(() -> {
                try{
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4447);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    if(listOfData.size() > 3){
                        ByteArrayOutputStream outByte = new ByteArrayOutputStream();
                        outByte.write(listOfData.get(2));
                        outByte.write(listOfData.get(3));
                        byte[] fin = outByte.toByteArray();
                        outByte.close();
                        out.writeInt(fin.length);
                        out.writeUTF(fileName);
                        out.writeUTF(SocketFile.hashOfByteArray(sha256,fin));
                        if(fin.length > 0){
                            out.write(fin,0,fin.length);
                        }
                    }else{
                        out.writeInt(listOfData.get(2).length);
                        out.writeUTF(fileName);
                        out.writeUTF(SocketFile.hashOfByteArray(sha256,listOfData.get(1)));
                        if(listOfData.get(2).length > 0){
                            out.write(listOfData.get(2),0,listOfData.get(2).length);
                        }
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }catch(NoSuchAlgorithmException ex){
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
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            listOfData.clear();
        }else{
            System.out.println("QUAD THREAD SENT --<EXEC>--");
            os.println("QUAD");
            os.flush();
            System.out.println(data.length);
            List<byte[]> listOfData = FileOperator.divideArray(data,data.length/4);
            Thread firstThread = new Thread(() -> {
                try{
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4445);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeInt(listOfData.get(0).length);
                    out.writeUTF(fileName);
                    out.writeUTF(SocketFile.hashOfByteArray(sha256,listOfData.get(0)));
                    if(listOfData.get(0).length > 0){
                        out.write(listOfData.get(0),0,listOfData.get(0).length);
                    }
                    System.out.println(listOfData.get(0).length);
                }catch(IOException ex){
                    ex.printStackTrace();
                }catch(NoSuchAlgorithmException ex){
                    ex.printStackTrace();
                }
            });
            Thread secondThread = new Thread(() -> {
                try{
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4446);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeInt(listOfData.get(1).length);
                    out.writeUTF(fileName);
                    out.writeUTF(SocketFile.hashOfByteArray(sha256,listOfData.get(1)));
                    if(listOfData.get(1).length > 0){
                        out.write(listOfData.get(1),0,listOfData.get(1).length);
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }catch(NoSuchAlgorithmException ex){
                    ex.printStackTrace();
                }
            });
            Thread thirdThread = new Thread(() -> {
                try{
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4447);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeInt(listOfData.get(2).length);
                    out.writeUTF(fileName);
                    out.writeUTF(SocketFile.hashOfByteArray(sha256,listOfData.get(2)));
                    if(listOfData.get(2).length > 0){
                        out.write(listOfData.get(2),0,listOfData.get(2).length);
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }catch(NoSuchAlgorithmException ex){
                    ex.printStackTrace();
                }
            });
            Thread fourthThread = new Thread(() -> {
                try{
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    Socket s = new Socket(ClientManager.LOCALHOST,4448);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    if(listOfData.size() > 4){
                        //System.out.println("size is 5");
                        ByteArrayOutputStream outByte = new ByteArrayOutputStream();
                        outByte.write(listOfData.get(3));
                        outByte.write(listOfData.get(4));
                        byte[] fin = outByte.toByteArray();
                        outByte.close();
                        out.writeInt(fin.length);
                        out.writeUTF(fileName);
                        out.writeUTF(SocketFile.hashOfByteArray(sha256,fin));
                        if(fin.length > 0){
                            out.write(fin,0,fin.length);
                        }
                        //wSystem.out.println(fin.length);
                    }else{
                        //System.out.println("Size is 4");
                        out.writeInt(listOfData.get(3).length);
                        out.writeUTF(fileName);
                        out.writeUTF(SocketFile.hashOfByteArray(sha256,listOfData.get(1)));
                        if(listOfData.get(3).length > 0){
                            out.write(listOfData.get(3),0,listOfData.get(3).length);
                        }
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }catch(NoSuchAlgorithmException ex){
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
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            listOfData.clear();
        }
        return true;
    }
    protected boolean receiveFile(){
        if(portAmount.equals("SINGLE")){
            try {
                Socket s = new Socket(ClientManager.LOCALHOST,4445);
                DataInputStream in = new DataInputStream(s.getInputStream());
                int len = in.readInt();
                String hashValue = in.readUTF();
                byte[] dat = new byte[len];
                if(len > 0){
                    in.readFully(dat);
                }
                s.close();
                in.close();
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(ClientManager.PATH_OF_CLIENT+fileName);
                    fos.write(dat);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dat = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(portAmount.equals("DOUBLE")){
            System.out.println("Double thread receive -->EXEC<--");
            int startPort = ClientManager.PORT + 1;
            final byte[][] out1 = new byte[1][];
            final byte[][] out2 = new byte[1][];
            int finalStartPort_1 = startPort;
            Thread firstThread = new Thread(() -> {
                try {
                    Socket s = new Socket(ClientManager.LOCALHOST, finalStartPort_1);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int len = in.readInt();
                    String hashValue = in.readUTF();
                    byte[] dat = new byte[len];
                    if(len > 0){
                        in.readFully(dat);
                    }
                    out1[0] = dat;
                    s.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            firstThread.start();
            startPort++;
            int finalStartPort_2 = startPort;
            Thread secondThread = new Thread(() -> {
                try {
                    Socket s = new Socket(ClientManager.LOCALHOST, finalStartPort_2);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int len = in.readInt();
                    String hashValue = in.readUTF();
                    byte[] dat = new byte[len];
                    if(len > 0){
                        in.readFully(dat);
                    }
                    out2[0] = dat;
                    s.close();
                    in.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            secondThread.start();
            try{
                firstThread.join();
                secondThread.join();
            }catch(InterruptedException ex){
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
                outStream.close();
                fos = new FileOutputStream(ClientManager.PATH_OF_CLIENT+fileName);
                fos.write(fin);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out1[0] = null;
            out2[0] = null;

        }else if(portAmount.equals("TRIPLE")){
            System.out.println("Triple thread receive -->EXEC<--");
            int startPort = ClientManager.PORT + 1;
            final byte[][] out1 = new byte[1][];
            final byte[][] out2 = new byte[1][];
            final byte[][] out3 = new byte[1][];

            int finalStartPort_1 = startPort;
            Thread firstThread = new Thread(() -> {
                try {
                    Socket s = new Socket(ClientManager.LOCALHOST, finalStartPort_1);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int len = in.readInt();
                    String hashValue = in.readUTF();
                    byte[] dat = new byte[len];
                    if(len > 0){
                        in.readFully(dat);
                    }
                    out1[0] = dat;
                    s.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            firstThread.start();
            startPort++;
            int finalStartPort_2 = startPort;
            Thread secondThread = new Thread(() -> {
                try {
                    Socket s = new Socket(ClientManager.LOCALHOST, finalStartPort_2);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int len = in.readInt();
                    String hashValue = in.readUTF();
                    byte[] dat = new byte[len];
                    if(len > 0){
                        in.readFully(dat);
                    }
                    out2[0] = dat;
                    s.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            secondThread.start();
            startPort++;
            int finalStartPort_3 = startPort;
            Thread thirdThread = new Thread(() -> {
                try {
                    Socket s = new Socket(ClientManager.LOCALHOST, finalStartPort_3);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int len = in.readInt();
                    String hashValue = in.readUTF();
                    byte[] dat = new byte[len];
                    if(len > 0){
                        in.readFully(dat);
                    }
                    out3[0] = dat;
                    s.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thirdThread.start();
            try{
                firstThread.join();
                secondThread.join();
                thirdThread.join();
            }catch(InterruptedException ex){
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
                fos = new FileOutputStream(ClientManager.PATH_OF_CLIENT+fileName);
                fos.write(fin);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out1[0] = null;
            out2[0] = null;
            out3[0] = null;
        }else if(portAmount.equals("QUAD")){
            System.out.println("Quad thread receive -->EXEC<--");
            int startPort = ClientManager.PORT + 1;
            final byte[][] out1 = new byte[1][];
            final byte[][] out2 = new byte[1][];
            final byte[][] out3 = new byte[1][];
            final byte[][] out4 = new byte[1][];
            int finalStartPort_1 = startPort;
            Thread firstThread = new Thread(() -> {
                try {
                    Socket s = new Socket(ClientManager.LOCALHOST, finalStartPort_1);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int len = in.readInt();
                    String hashValue = in.readUTF();
                    byte[] dat = new byte[len];
                    if(len > 0){
                        in.readFully(dat);
                    }
                    out1[0] = dat;
                    try {
                        String checkSum = SocketFile.hashOfByteArray(MessageDigest.getInstance("SHA-256"),out1[0]);
                        if(!hashValue.equals(checkSum)){
                            System.out.println("Received file is not same as the sent one");
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    s.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            firstThread.start();
            startPort++;
            int finalStartPort_2 = startPort;
            Thread secondThread = new Thread(() -> {
                try {
                    Socket s = new Socket(ClientManager.LOCALHOST, finalStartPort_2);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int len = in.readInt();
                    String hashValue = in.readUTF();
                    byte[] dat = new byte[len];
                    if(len > 0){
                        in.readFully(dat);
                    }
                    out2[0] = dat;
                    s.close();
                    in.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            secondThread.start();
            startPort++;
            int finalStartPort_3 = startPort;
            Thread thirdThread = new Thread(() -> {
                try {
                    Socket s = new Socket(ClientManager.LOCALHOST, finalStartPort_3);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int len = in.readInt();
                    String hashValue = in.readUTF();
                    byte[] dat = new byte[len];
                    if(len > 0){
                        in.readFully(dat);
                    }
                    out3[0] = dat;
                    s.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thirdThread.start();
            startPort++;
            int finalStartPort_4 = startPort;
            Thread fourthThread = new Thread(() -> {
                try {
                    Socket s = new Socket(ClientManager.LOCALHOST, finalStartPort_4);
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int len = in.readInt();
                    String hashValue = in.readUTF();
                    byte[] dat = new byte[len];
                    if(len > 0){
                        in.readFully(dat);
                    }
                    out4[0] = dat;
                    s.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fourthThread.start();
            try{
                firstThread.join();
                secondThread.join();
                thirdThread.join();
                fourthThread.join();
            }catch(InterruptedException ex){
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
                fos = new FileOutputStream(ClientManager.PATH_OF_CLIENT+fileName);
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
        return true;
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
