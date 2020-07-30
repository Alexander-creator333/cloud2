package NIO;

import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.List;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NIOServer implements Runnable {

    ServerSocketChannel srv;
    Selector selector;
    private DataInputStream is;
    private DataOutputStream os;
    private List<File> clientFileList;
    private String serverPath = "./common/src/main/resources/";

    private File findFileByName(String fileName) {
        for (File file : clientFileList) {
            if (file.getName().equals(fileName)){
                return file;
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            srv = ServerSocketChannel.open();
            srv.bind(new InetSocketAddress(8189));
            System.out.println("server started!");
            srv.configureBlocking(false);
            selector = Selector.open();
            srv.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select(); // block
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isAcceptable()) {
                        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
                        System.out.println("Client accepted");
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        SocketChannel channel = (SocketChannel) key.channel();
                        int cnt = channel.read(buffer);
                        if (cnt == -1) {
                            System.out.println("client leave chat!");
                            channel.close();
                        }
                        buffer.flip();
                        StringBuilder msg = new StringBuilder();
                        char cHar;
                        byte bYte;
                        while (buffer.hasRemaining()) {
                            cHar = (char) buffer.get();
                            bYte = (byte) cHar;
                            System.out.println(cHar + " " + bYte);
                            msg.append(cHar);
                        }
                        System.out.println(msg);
                        String[] subStr;
                        subStr = msg.toString().split ("\0\2");
                        for (int i = 0; i < subStr.length; i++) {
                            System.out.println(i+": "+subStr[i]);
                        }
                        //channel.write(ByteBuffer.wrap(subStr[0].getBytes()));
                        if (subStr[0].equals("./upload")) {
                            String fileName = subStr[1];
                            System.out.println("fileName: " + fileName);
                            long fileLength = Long.parseLong(subStr[2]);
                            System.out.println("fileLength: " + fileLength);
                            File file = new File(serverPath + "/" + fileName);
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            try(FileOutputStream fos = new FileOutputStream(file)) {
                                for (long i = 0; i < (fileLength / 1024 == 0 ? 1 : fileLength / 1024); i++) {
                                    int bytesRead = channel.read(buffer);
                                    fos.write(buffer.array(), 0, bytesRead);
                                }
                            }
                            //os.writeUTF("OK");
                            channel.write(ByteBuffer.wrap("OK\0\2".getBytes()));
                        } else if (subStr[0].equals("./download")) {
                            String fileName = subStr[1];
                            File currentFile = findFileByName(fileName);
                            byte [] buffer1 = new byte[1024];
                            if (currentFile != null) {
                                try {
                                    channel.write(ByteBuffer.wrap((fileName+"\0\2").getBytes()));
                                    buffer.clear();
                                    buffer.putLong(currentFile.length());
                                    channel.write(buffer);
                                    FileInputStream fis = new FileInputStream(currentFile);
                                    while (fis.available() > 0) {
                                        int bytesRead = fis.read(buffer1);
                                        buffer.put(buffer1, 0, bytesRead);
                                        channel.write(buffer);
                                    }
                                    msg.delete(0,msg.length());
                                    while (buffer.hasRemaining()) {
                                        cHar = (char) buffer.get();
                                        bYte = (byte) cHar;
                                        System.out.println(cHar + " " + bYte);
                                        msg.append(cHar);
                                    }
                                    System.out.println(msg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            os.writeUTF("ERROR command");//ошибка
                        }
                        /*
                        for (SelectionKey out : selector.keys()) {
                            if (out.isReadable() && out.channel() instanceof SocketChannel) {
                                ((SocketChannel) out.channel()).write(ByteBuffer.wrap
                                        (msg.toString().getBytes()));
                            }
                        }
                        */
                        //channel.close();
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        new Thread(new NIOServer()).start();
    }
}
