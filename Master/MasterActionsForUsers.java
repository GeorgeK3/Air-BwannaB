import java.io.*;
import java.net.*;
import java.lang.Math;
import org.json.*;

public class MasterActionsForUsers extends Thread {

    int id = 0;
    private static int requestID = 0;
    private ObjectOutputStream out;
    private static boolean active = false;
    private ObjectInputStream in;
    private static int numOfWorkers;

    public MasterActionsForUsers(Socket connection) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            numOfWorkers = MasterServer.workersIP.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getWorkerSocket(String name) {
        int worker = (Math.abs(name.hashCode())) % numOfWorkers;
        return MasterServer.workersPort.get(worker);
    }

    private String getWorkerIP(String name) {
        int worker = (Math.abs(name.hashCode())) % numOfWorkers;
        return MasterServer.workersIP.get(worker);
    }

    public void run() {
        try {

            String jsonString = (String) in.readObject();
            JSONObject jsonObj = new JSONObject(jsonString);
            System.out.println(jsonString);
            System.out.println("Sender: " + jsonObj.getString("sender"));
            if (MasterServer.workersPort.size()>2 && !active ){
                try{
                    Socket requestSocket = new Socket(MasterServer.workersIP.get(1), MasterServer.workersPort.get(1));
                    requestSocket.close();
                }
                catch (Exception e){
                    active = true;
                    System.out.println("BACKUP ACTIVATED");
                    backup();
                }
            }
            if(jsonObj.getString("sender").equals("app")){
                System.out.println("APP\n\n");

                String request_type = jsonObj.getString("requestType");
                System.out.println("I AM RUNNING AND GOING TO PROCESS THE REQUEST");
                System.out.println("THE REQUEST IS:  " + request_type);


                if (request_type.equals("room") | request_type.equals("update") | request_type.equals("updateReview")) {

                    int worker_socket = getWorkerSocket(jsonObj.getString("roomName"));
                    String ip = getWorkerIP(jsonObj.getString("roomName"));

                    MasterWorkerClient mc = new MasterWorkerClient(worker_socket, ip, jsonObj.toString());

                    System.out.println("I will try to send to the worker for update/room " + worker_socket);

                    mc.start();
                    int worker = (Math.abs(jsonObj.getString("roomName").hashCode())) % numOfWorkers;
                    if (MasterServer.workersPort.size()>2 && worker==1 && !active){
                        System.out.println("ALSO SENDING TO WORKER 1 FOR BACKUP");

                        if (request_type.equals("room")) {
                            jsonObj.put("requestType", "backup");
                        }else{
                            jsonObj.put("requestType", "updateBackup");
                        }
                        MasterWorkerClient mc2 = new MasterWorkerClient(MasterServer.workersPort.get(0), MasterServer.workersIP.get(0), jsonObj.toString());
                        mc2.start();
                    }

                } else if (request_type.equals("filter") | request_type.equals("get")|request_type.equals("getUser")|request_type.equals("getRents")) {


                    requestID += 1;
                    this.id = requestID;
                    jsonObj.put("requestID", this.id);

                    for (int i = 0; i < numOfWorkers; i += 1) {
                        if(active && i==0){
                            i+=1;
                        }
                        System.out.println("I will try to send to the worker for filter " + (i + i));
                        MasterWorkerClient mc = new MasterWorkerClient(MasterServer.workersPort.get(i), MasterServer.workersIP.get(i), jsonObj.toString());
                        mc.start();
                    }



                    JSONArray reducerReply;

                    while (true) {
                        try {
                            if(!MasterServer.replys.containsKey(this.id)){
                                throw new Exception();
                            }
                            reducerReply = new JSONArray(MasterServer.replys.get(this.id));
                            break;

                        } catch (Exception e) {
                            sleep(1000);
                        }
                    }

                    System.out.println("ALL OK FILTER/GET");

                    out.writeObject(reducerReply.toString());
                    out.flush();

                } else if (request_type.equals("rent")) {

                    requestID += 1;
                    this.id = requestID;
                    jsonObj.put("requestID", this.id);
                    int worker_socket = getWorkerSocket(jsonObj.getString("roomName"));
                    String ip = getWorkerIP(jsonObj.getString("roomName"));
                    synchronized (this) {
                        System.out.println("SYNCHRONIZATION BABYYYY");

                        MasterWorkerClient mc = new MasterWorkerClient(worker_socket, ip, jsonObj.toString());
                        System.out.println("I will try to send to the worker for rent " + worker_socket);

                        mc.start();
                    }

                    JSONObject reducerReply;

                    while (true) {
                        try {
                            if(!MasterServer.replys.containsKey(this.id)){
                                throw new Exception();
                            }
                            reducerReply = new JSONObject(MasterServer.replys.get(this.id).get(0).toString());

                            break;

                        } catch (Exception e) {
                            sleep(1000);
                        }
                    }

                    System.out.println("ALL OK RENT");
                    System.out.println(reducerReply.toString());
                    out.writeObject(reducerReply.toString());
                    out.flush();
                }
            }
            else if (jsonObj.getString("sender").equals("reducer") && !jsonObj.getString("requestType").equals("rent")) {
                System.out.println("REDUCER\n\n");

                System.out.println(jsonObj.toString());
                MasterServer.replys.put(jsonObj.getInt("requestID"), jsonObj.getJSONArray("array"));

                System.out.println("REQUEST ID:" + jsonObj.getInt("requestID"));

            }else{
                System.out.println("REDUCER\n\n");

                System.out.println(jsonObj.toString());
                JSONArray jsonObjAsArray = new JSONArray();
                MasterServer.replys.put(jsonObj.getInt("requestID"), jsonObjAsArray.put(jsonObj));

                System.out.println(MasterServer.replys.get(jsonObj.getInt("requestID")).toString());
            }


        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void backup() throws InterruptedException {
        JSONObject help = new JSONObject();
        help.put("requestType","loadBackup");
        System.out.println("I will try to send to the worker for backup ");
        if(MasterServer.workersIP.size()<2){
            System.out.println("Backup failed , not enough workers ");
            throw new RuntimeException();
        }
        int port = MasterServer.workersPort.get(0);
        String IP = MasterServer.workersIP.get(0);

        MasterServer.workersIP.set(1,MasterServer.workersIP.get(0));
        MasterServer.workersPort.set(1,MasterServer.workersPort.get(0));

        MasterWorkerClient mc = new MasterWorkerClient(port, IP, help.toString());
        mc.start();
        sleep(2000);


    }
}
