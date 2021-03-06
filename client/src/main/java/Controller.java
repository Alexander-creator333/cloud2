import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button send;
    public ListView<String> listView;
    public TextField text;
    private List<File> clientFileList;
    public static Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private String clientPath = "./client/src/main/resources/";
    public void sendCommand(ActionEvent actionEvent) {
        try {
            byte [] buffer = new byte[1024];
            String[] subStr;
            subStr = text.getText().split(" ");
            os.writeUTF(subStr[0]);
            if (subStr.length >1 ){
                os.writeUTF(subStr[1]);
            } else {
                os.writeUTF(subStr[0]); //времянка, чтобы комманды из одного слова отправлялись без ероров.
            }
            }
            String command = is.readUTF();
            if (command.equals("./upload")) {
                String fileName = is.readUTF();
                System.out.println("fileName: " + fileName);
                long fileLength = is.readLong();
                System.out.println("fileLength: " + fileLength);
                File file = new File(  clientPath + "/" + fileName);
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
            } else { //if (command.equals("./upload")) {
                System.out.println(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(text.getText() );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: 7/21/2020 init connect to server
        try{
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread.sleep(1000);
            clientFileList = new ArrayList<>();
            //String clientPath = "./client/src/main/resources/";
            File dir = new File(clientPath);
            if (!dir.exists()) {
                throw new RuntimeException("directory resource not exists on client");
            }
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                clientFileList.add(file);
                listView.getItems().add(file.getName() + " : " + file.length());
            }
            listView.setOnMouseClicked(a -> {
                if (a.getClickCount() == 2) {
                    String fileName = listView.getSelectionModel().getSelectedItem();
                    String[] subStr;
                    subStr = fileName.split(" : ");
                    fileName = subStr[0];
                    System.out.println(fileName);

                    File currentFile = findFileByName(fileName);
                    if (currentFile != null) {
                        try {
                            os.writeUTF("./upload");
                            os.writeUTF(fileName);
                            os.writeLong(currentFile.length());
                            FileInputStream fis = new FileInputStream(currentFile);
                            byte [] buffer = new byte[1024];
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File findFileByName(String fileName) {
        for (File file : clientFileList) {
            if (file.getName().equals(fileName)){
                return file;
            }
        }
        return null;
    }
}
