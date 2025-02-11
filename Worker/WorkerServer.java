import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.*;

public class WorkerServer extends Thread {

    public static JSONArray getroo() {
        return rooms;
    }

    public static JSONArray rooms;
    public static JSONArray backup;
    private final int workerPort;
    Socket connection = null;
    ServerSocket serverSocket;
    static String reducerIP;
    static int reducerPort;

    public static void main(String[] args) throws IOException {
        String file = args[0];
        String data = new String(Files.readAllBytes(Paths.get(file)));

        new WorkerServer(data).openServer();
    }

    public WorkerServer(String data) {
        JSONObject jsonMaster= new JSONObject(data);
        reducerIP = jsonMaster.getString("IP");
        reducerPort = jsonMaster.getInt("Port");
        this.workerPort = jsonMaster.getInt("workerPort");;
        rooms = new JSONArray();
        backup = new JSONArray();
    }

    public void openServer() {
        try {
            serverSocket = new ServerSocket(workerPort);
            System.out.println("Worker started and listening on port " + workerPort);

            while (true) {
                connection = serverSocket.accept();

                System.out.println("NEW CLIENT!!!!!");

                Thread t = new WorkerProcessRequest(connection);
                t.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void addRoom(JSONObject room){
        rooms.put(room);
    }
    public static JSONObject searchRoom(String roomName){
        for (int i = 0; i< rooms.length(); i+=1){
            JSONObject jsonObject = new JSONObject(rooms.get(i).toString());
            if (jsonObject.getString("roomName").equals(roomName)){
                return jsonObject;
            }
        }
        return null;
    }

    public static void updateRoom(String roomName,JSONObject updatedRoom,JSONArray rooms){
        for (int i = 0; i< rooms.length(); i+=1){
            JSONObject jsonObject = new JSONObject(rooms.get(i).toString());
            if (jsonObject.getString("roomName").equals(roomName)){
                rooms.remove(i);

                if (jsonObject.getString("dates").equals("")){

                    jsonObject.put("dates",updatedRoom.getString("dateFrom")+" - " + updatedRoom.getString("dateTo"));

                }else{

                    String dates = jsonObject.getString("dates");
                    jsonObject.put("dates",dates + " , " + updatedRoom.getString("dateFrom") + " - " + updatedRoom.getString("dateTo"));

                }
                rooms.put(jsonObject);
                return;
            }
        }
    }

    public static void updateRoomReview(String roomName,JSONObject review,JSONArray rooms){
        for (int i = 0; i< rooms.length(); i+=1){
            JSONObject jsonObject = new JSONObject(rooms.get(i).toString());
            if (jsonObject.getString("roomName").equals(roomName)){
                rooms.remove(i);

                double reviews = jsonObject.getDouble("stars");
                int noOfReviews = jsonObject.getInt("noOfReviews");

                double newReview = ((reviews * noOfReviews) + review.getInt("review"))/(noOfReviews + 1);

                DecimalFormat df = new DecimalFormat("#.#");
                newReview = Math.round(newReview * 10) / 10.0;
                jsonObject.put("stars",newReview);
                jsonObject.put("noOfReviews",noOfReviews+1);

                rooms.put(jsonObject);

                return;
            }
        }
    }

    public static JSONArray getRooms(int managerID){
        JSONArray managerRooms = new JSONArray();
        for (int i = 0; i < rooms.length();i+=1){
            JSONObject jsonObject = new JSONObject(rooms.get(i).toString());

            if (jsonObject.getInt("managerID") == managerID){
                managerRooms.put(jsonObject);
            }
        }
        return  managerRooms;

    }

    public static JSONArray getRents(int userID){
        JSONArray rentedRooms = new JSONArray();
        for (int i = 0; i < rooms.length();i+=1){
            JSONObject jsonObject = new JSONObject(rooms.get(i).toString());

            try{

                String rents = jsonObject.getString("rents");
                String[] pairs = rents.split(";");

                for (String pair : pairs) {
                    String cleanPair = pair.substring(1, pair.length() - 1);

                    String[] elements = cleanPair.split(",");

                    int ID = Integer.parseInt(elements[0]);

                    if (ID == userID){
                        rentedRooms.put(jsonObject);
                    }
                }
            }catch (Exception e){
                continue;
            }
        }
        return  rentedRooms;

    }

    public static JSONArray getManagerRents(int managerID){
        JSONArray rentedRooms = new JSONArray();
        for (int i = 0; i < rooms.length();i+=1){
            JSONObject jsonObject = new JSONObject(rooms.get(i).toString());
            if(jsonObject.getInt("managerID") == managerID) {
                try {

                    String rents = jsonObject.getString("rents");
                    if (!rents.isEmpty()){
                        rentedRooms.put(jsonObject);
                    }

                } catch (Exception e) {
                    continue;
                }
            }
        }
        return  rentedRooms;

    }

    public static JSONArray filterRooms(JSONObject filters) {
        JSONArray filteredRooms = new JSONArray();
        JSONObject filtered = new JSONObject(filters.toString());
        filtered.remove("requestID");
        filtered.remove("requestType");
        Map<String, Object> map = filtered.toMap();

        for (int i = 0; i < rooms.length(); i++) {
            JSONObject jsonObject = rooms.getJSONObject(i);
            int checks = 0;

            if (jsonObject.getString("dates").equals("")) {
                continue;
            }

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = (String) entry.getValue();

                if (value.equals("-")) {
                    checks++;
                    continue;
                } else {
                    switch (key) {
                        case "area":
                            if (jsonObject.getString("area").equals(value)) {
                                checks++;
                            }
                            break;

                        case "date":
                            String[] dateParts = value.split(" - ");
                            LocalDate startDate = LocalDate.parse(dateParts[0], DateTimeFormatter.ofPattern("d/M/yyyy"));
                            LocalDate endDate = LocalDate.parse(dateParts[1], DateTimeFormatter.ofPattern("d/M/yyyy"));

                            String[] individualRanges = jsonObject.getString("dates").split(" , ");
                            boolean dateMatch = false;

                            for (String dateRange : individualRanges) {
                                String[] dates = dateRange.split(" - ");
                                LocalDate dateFrom = LocalDate.parse(dates[0], DateTimeFormatter.ofPattern("d/M/yyyy"));
                                LocalDate dateTo = LocalDate.parse(dates[1], DateTimeFormatter.ofPattern("d/M/yyyy"));

                                // Check if the room's date range starts on or after the requested start date and ends on or before the requested end date
                                if (!dateFrom.isBefore(startDate) && !dateTo.isAfter(endDate)) {
                                    dateMatch = true;
                                    break;
                                }
                            }

                            if (dateMatch) {
                                checks++;
                            }
                            break;

                        case "noOfPersons":
                            if (jsonObject.getInt("noOfPersons") >= Integer.parseInt(value)) {
                                checks++;
                            }
                            break;

                        case "price":
                            if (jsonObject.getDouble("price") <= Double.parseDouble(value)) {
                                checks++;
                            }
                            break;

                        case "stars":
                            if (jsonObject.getDouble("stars") >= Double.parseDouble(value)) {
                                checks++;
                            }
                            break;
                    }
                }
            }

            if (checks == 5) {
                filteredRooms.put(jsonObject);
            }
        }
        return filteredRooms;
    }

    public static boolean rent(String roomName, JSONObject jsonRent) {
        JSONObject room;
        try {
            room = new JSONObject(searchRoom(roomName).toString());
        } catch (JSONException e) {
            throw new RuntimeException("Error parsing room JSON", e);
        }

        String[] individualRanges;
        try {
            individualRanges = room.getString("dates").split(" , ");
        } catch (JSONException e) {
            throw new RuntimeException("Error getting dates from room JSON", e);
        }

        String[] dates = new String[individualRanges.length - 1];
        String rentRange;
        try {
            rentRange = jsonRent.getString("rentRange");
        } catch (JSONException e) {
            throw new RuntimeException("Error getting rentRange from JSON rent", e);
        }

        for (int i = 0; i < individualRanges.length; i++) {
            if (individualRanges[i].equals(rentRange)) {
                int k = 0;
                for (int j = 0; j < individualRanges.length; j++) {
                    if (j != i) {
                        dates[k] = individualRanges[j];
                        k++;
                    }
                }

                String dateRanges = String.join(" , ", dates);

                for (int p = 0; p < rooms.length(); p++) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(rooms.get(p).toString());
                    } catch (JSONException e) {
                        throw new RuntimeException("Error parsing room from rooms array", e);
                    }

                    try {
                        if (jsonObject.getString("roomName").equals(roomName)) {
                            rooms.remove(p);
                            jsonObject.put("dates", dateRanges);

                            String newRent = "[" + jsonRent.getInt("userID") + "," + rentRange + "]";
                            String rents;
                            try {
                                rents = jsonObject.getString("rents");
                            } catch (JSONException e) {
                                rents = "";
                            }

                            // Check if the rent already exists
                            if (!rents.contains(newRent)) {
                                if (!rents.isEmpty()) {
                                    rents += " ; " + newRent;
                                } else {
                                    rents = newRent;
                                }
                                jsonObject.put("rents", rents);
                            }

                            rooms.put(jsonObject);
                            return true;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException("Error updating room JSON", e);
                    }
                }
            }
        }

        return false;
    }




}

