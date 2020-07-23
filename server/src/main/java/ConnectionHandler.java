import java.io.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ConnectionHandler implements Runnable {

    private DataInputStream is;
    private DataOutputStream os;
    private List<File> clientFileList;
    private String serverPath = "./server/src/main/resources/";


    public ConnectionHandler(Socket socket) throws IOException, InterruptedException {
        System.out.println("Connection accepted");
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        Thread.sleep(2000);
    }

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
        clientFileList = new ArrayList<>();
        serverPath = "./server/src/main/resources/";

        File dir = new File(serverPath);
        if (!dir.exists()) {
            throw new RuntimeException("directory resource not exists on server");
        }
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            clientFileList.add(file);
        }


        byte [] buffer = new byte[1024];
        while (true) {
            try {
                String command = is.readUTF();
                if (command.equals("./upload")) {
                    String fileName = is.readUTF();
                    System.out.println("fileName: " + fileName);
                    long fileLength = is.readLong();
                    System.out.println("fileLength: " + fileLength);
                    File file = new File(Server.serverPath + "/" + fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try(FileOutputStream fos = new FileOutputStream(file)) {
                        for (long i = 0; i < (fileLength / 1024 == 0 ? 1 : fileLength / 1024); i++) {
                            int bytesRead = is.read(buffer);
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    os.writeUTF("OK");
                } else if (command.equals("./download")) {
                    String fileName = is.readUTF();
                    File currentFile = findFileByName(fileName);
                    if (currentFile != null) {
                        try {
                            os.writeUTF("./upload");
                            os.writeUTF(fileName);
                            os.writeLong(currentFile.length());
                            FileInputStream fis = new FileInputStream(currentFile);
                            while (fis.available() > 0) {
                                int bytesRead = fis.read(buffer);
                                os.write(buffer, 0, bytesRead);
                            }
                            os.flush();
                            String response = is.readUTF();
                            System.out.println(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } else {
                    os.writeUTF("ERROR command");//ошибка
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
