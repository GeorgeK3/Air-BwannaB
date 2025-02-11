import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class ReducerMasterClient extends Thread{
    int master_socket;
    String ip;
    String request;
    String jsonObj;


    ReducerMasterClient(int master_socket, String ip, String jsonObj) {

        this.master_socket = master_socket;
        this.jsonObj = jsonObj;
        this.ip = ip;

    }



    public void run() {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket(ip, master_socket);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());


            out.writeObject(jsonObj);
            out.flush();

            System.out.println("OBJECT : \n" + jsonObj + " \nSENT TO MASTER");

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();	out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
