import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ReducerServer {
    static String masterIP;
    static int masterPort;
    ServerSocket providerSocket;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    public static void main(String[] args) throws IOException {
        String file = args[0];
        String data = new String(Files.readAllBytes(Paths.get("C:\\Users\\lenovo\\Desktop\\Reducer\\src\\"+file+".json")));

        new ReducerServer(data).openserver();

    }

    ReducerServer(String data){
        JSONObject jsonMaster= new JSONObject(data);
        masterIP = jsonMaster.getString("IP");
        masterPort = jsonMaster.getInt("Port");
    }

    void openserver(){
        try{
            providerSocket = new ServerSocket(5318, 10);


            while(true){
                connection = providerSocket.accept();
                System.out.println("NEW CLIENT!!!!!");

                Thread t = new ReducerActionForWorkers(connection);
                t.start();

            }
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try{
                providerSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }


}