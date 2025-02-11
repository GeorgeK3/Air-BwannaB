import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class ReducerActionForWorkers extends Thread{
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private static ArrayList<Integer> requests = new ArrayList<Integer>();
    private static Map<Integer,JSONArray> map = new HashMap<Integer,JSONArray>();
    public ReducerActionForWorkers(Socket connection) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String jsonStr = (String) in.readObject();

            JSONObject jsonObj = new JSONObject(jsonStr);

            Random random = new Random();
            int randomNumber = random.nextInt(3001);
            sleep(randomNumber);

            if (jsonObj.getString("requestType").equals("get")|jsonObj.getString("requestType").equals("filter") |jsonObj.getString("requestType").equals("getUser")|jsonObj.getString("requestType").equals("getRents")) {
                synchronized (this) {
                    if (!requests.contains(jsonObj.getInt("requestID"))) {
                        System.out.println("NEW REQUEST RECEIVED");
                        requests.add(jsonObj.getInt("requestID"));
                        map.put(jsonObj.getInt("requestID"), jsonObj.getJSONArray("array"));
                        sleep(5000);

                        JSONArray arr = new JSONArray(map.get(jsonObj.getInt("requestID")));
                        jsonObj.put("array", arr);
                        jsonObj.put("sender", "reducer");
                        System.out.println(jsonObj.getJSONArray("array").toString());
                        map.put(jsonObj.getInt("requestID"), null);
                        ReducerMasterClient rc = new ReducerMasterClient(ReducerServer.masterPort, ReducerServer.masterIP, jsonObj.toString());
                        rc.start();


                    } else {

                        JSONArray arr = new JSONArray(map.get(jsonObj.getInt("requestID")));

                        if (arr == null) {
                            System.out.println("WARNING, Worker took too much time to answer and was timed out");

                        } else {

                            JSONArray jsonA = new JSONArray(jsonObj.getJSONArray("array"));
                            System.out.println("NEW INFO FOR REQUEST");
                            System.out.println("jsonA Before : " + jsonA.toString());
                            for (int i = 0; i < arr.length(); i++) {
                                jsonA.put(arr.getJSONObject(i));
                            }
                            System.out.println("jsonA After : " + jsonA.toString());
                            map.put(jsonObj.getInt("requestID"), jsonA);
                        }

                    }
                }
            }
            else if (jsonObj.getString("requestType").equals("rent")){
                jsonObj.put("sender","reducer");
                ReducerMasterClient rc = new ReducerMasterClient(ReducerServer.masterPort,ReducerServer.masterIP,jsonObj.toString());
                rc.start();
            }


        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

