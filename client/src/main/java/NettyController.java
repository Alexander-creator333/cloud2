import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class NettyController implements Initializable {

    public Button send;
    public ListView<String> listView;
    public TextField text;
    private NettyNetwork network;
    private Socket socket;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;


    public void sendCommand(ActionEvent actionEvent) {
        try {
            Path path = Paths.get("./client/src/main/resources");
            String fileName = text.getText();
            os.writeObject(new File(path.toString() + "/" + fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}