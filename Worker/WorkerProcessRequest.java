import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class WorkerProcessRequest extends Thread{


    private ObjectInputStream in;
    private ObjectOutputStream out;
    public WorkerProcessRequest(Socket connection) {
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        }catch(IOException e){
            System.out.println("---------------------------------------------------\nPing from Master received! \n---------------------------------------------------");
        }
    }

    public void run() {
        try {
            String jsonStr = (String) in.readObject();
            JSONObject jsonObj = new JSONObject(jsonStr); // Reads JSON from Master

            if (jsonObj.getString("requestType").equals("room")) {

                System.out.println("I RECEIVED A ROOM");

                WorkerServer.addRoom(jsonObj);

                System.out.println("I ADDED THE ROOM TO MY DATABASE");
                System.out.println(jsonObj.toString());

            } else if (jsonObj.getString("requestType").equals("get")) {
                JSONArray managerRooms;

                System.out.println("I RECEIVED THE GET REQUEST");

                managerRooms = new JSONArray(WorkerServer.getRooms(jsonObj.getInt("managerID")));

                System.out.println(managerRooms.toString());


                jsonObj.put("array", managerRooms);
                WorkerReducerClient wrc = new WorkerReducerClient(jsonObj.toString());
                wrc.start();

            } else if (jsonObj.getString("requestType").equals("getUser")) {

                System.out.println("I RECEIVED THE GET USER REQUEST");

                JSONArray rentedRooms;

                rentedRooms = new JSONArray(WorkerServer.getRents(jsonObj.getInt("userID")));

                System.out.println(rentedRooms.toString());


                jsonObj.put("array", rentedRooms);
                WorkerReducerClient wrc = new WorkerReducerClient(jsonObj.toString());
                wrc.start();

            }
            else if (jsonObj.getString("requestType").equals("getRents")) {

                System.out.println("I RECEIVED THE GET RENTS REQUEST");

                JSONArray rentedRooms;

                rentedRooms = new JSONArray(WorkerServer.getManagerRents(jsonObj.getInt("managerID")));

                System.out.println(rentedRooms.toString());


                jsonObj.put("array", rentedRooms);
                WorkerReducerClient wrc = new WorkerReducerClient(jsonObj.toString());
                wrc.start();

            }else if (jsonObj.getString("requestType").equals("update")) {

                System.out.println("I RECEIVED THE UPDATE REQUEST");
                System.out.println("Before : \n" + WorkerServer.searchRoom(jsonObj.getString("roomName")).toString());

                WorkerServer.updateRoom(jsonObj.getString("roomName"), jsonObj, WorkerServer.rooms);

                System.out.println("After : \n" + WorkerServer.searchRoom(jsonObj.getString("roomName")).toString());
            } else if (jsonObj.getString("requestType").equals("updateReview")) {

                System.out.println("I RECEIVED THE UPDATE REVIEW REQUEST");
                System.out.println("Before : \n" + WorkerServer.searchRoom(jsonObj.getString("roomName")).toString());

                WorkerServer.updateRoomReview(jsonObj.getString("roomName"), jsonObj, WorkerServer.rooms);

                System.out.println("After : \n" + WorkerServer.searchRoom(jsonObj.getString("roomName")).toString());

            } else if (jsonObj.getString("requestType").equals("filter")) {

                System.out.println("I RECEIVED THE FILTER REQUEST");

                JSONArray filteredRooms = new JSONArray(WorkerServer.filterRooms(jsonObj));

                System.out.println("THIS ROOMS WHERE CHOSEN: \n");
                System.out.println(filteredRooms.toString());


                jsonObj.put("array", filteredRooms);
                WorkerReducerClient wrc = new WorkerReducerClient(jsonObj.toString());
                wrc.start();

            } else if (jsonObj.getString("requestType").equals("rent")) {

                System.out.println("I RECEIVED THE RENT REQUEST");

                boolean rented = WorkerServer.rent(jsonObj.getString("roomName"), jsonObj);
                jsonObj.put("success", rented);
                WorkerReducerClient wrc = new WorkerReducerClient(jsonObj.toString());
                wrc.start();

            } else if (jsonObj.getString("requestType").equals("backup")) {

                System.out.println("I RECEIVED A BACKUP");
                WorkerServer.backup.put(jsonObj);

            } else if (jsonObj.getString("requestType").equals("updateBackup")) {

                System.out.println("I AM UPDATING A BACKUP");

                WorkerServer.updateRoom(jsonObj.getString("roomName"), jsonObj, WorkerServer.backup);

            } else if (jsonObj.getString("requestType").equals("loadBackup")) {

                System.out.println("LOADING BACKUP");

                JSONArray jsonA = new JSONArray(WorkerServer.rooms);

                System.out.println("ROOMS Before BACKUP : " + jsonA.toString());

                for (int i = 0; i < WorkerServer.backup.length(); i++) {
                    jsonA.put(WorkerServer.backup.getJSONObject(i));
                }
                System.out.println("ROOMS After BACKUP: " + jsonA.toString());
                WorkerServer.rooms = new JSONArray(jsonA);

            }


        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){}
        finally {
            try {
                in.close();
                out.close();
            }
            catch (NullPointerException e){}
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
