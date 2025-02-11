import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MasterServer {
    public static void main(String args[]) throws IOException {
        String file = args[0];
        String data = new String(Files.readAllBytes(Paths.get(file)));

        new MasterServer(data).openServer();
    }

    static ArrayList<String> workersIP = new ArrayList<>();
    static ArrayList<Integer> workersPort = new ArrayList<>();
    static Map<Integer, JSONArray> replys = new HashMap<Integer, JSONArray>();


    MasterServer(String data){
        JSONArray jsonWorkers = new JSONArray(data);
        int size = jsonWorkers.length();
        for (int i = 0;i<size ; i+=1){
            JSONObject str = new JSONObject(jsonWorkers.get(i).toString());

            workersIP.add(str.getString("IP"));
            workersPort.add(str.getInt("Port"));

        }

    }
    ServerSocket providerSocket;
    Socket connection = null;

    void openServer() {
        try {
            providerSocket = new ServerSocket(5330, 10);

            while (true) {
                connection = providerSocket.accept();

                System.out.println("NEW CLIENT!!!!!");

                Thread t = new MasterActionsForUsers(connection);
                t.start();

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}