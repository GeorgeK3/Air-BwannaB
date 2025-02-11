import java.io.*;
import java.net.*;
import org.json.*;

public class MasterWorkerClient extends Thread{

    int worker_socket;
    String ip;
    String jsonObj;


    MasterWorkerClient(int worker_socket, String ip, String jsonObj) {

        this.worker_socket = worker_socket;
        this.jsonObj = jsonObj;
        this.ip = ip;

    }



    public void run() {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            requestSocket = new Socket(ip, worker_socket);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeObject(jsonObj);
            out.flush();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }finally {
            try {
                in.close();	out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
