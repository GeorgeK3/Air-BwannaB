package com.example.frontendds;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.*;

public class Client extends Thread{


    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray){
        this.jsonArray = jsonArray;
    }


    public JSONObject getJsonObj() {
        return jsonObj;
    }

    public void setJsonObj(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
    }

    JSONObject jsonObj;
    JSONArray jsonArray;
    String requestType;
    String rentRange;
    int managerID;

    public boolean isAnswerRecieved() {
        return answerRecieved;
    }

    boolean answerRecieved=false;


    Client(JSONObject b, String requestType, String rentRange) throws JSONException {
        this.jsonObj=b;
        this.requestType = requestType;
        answerRecieved=false;
        jsonObj.put("requestType", this.requestType);
        jsonObj.put("rentRange",rentRange);
        jsonObj.put("sender","app");
    }

    Client(JSONObject b, String requestType) throws JSONException {
        this.jsonObj=b;
        answerRecieved=false;
        this.requestType = requestType;
        jsonObj.put("requestType", this.requestType);
        jsonObj.put("sender","app");
    }


    Client(JSONObject b, String requestType, int managerID) throws JSONException {
        this.jsonObj=b;
        this.requestType = requestType;
        answerRecieved=false;
        this.managerID = managerID;
        jsonObj.put("requestType", requestType);
        if(requestType.equals("get") | requestType.equals("getRents")) {
            jsonObj.put("managerID", this.managerID);
        }else if(requestType.equals("getUser")){
            jsonObj.put("userID",this.managerID);
        }
        jsonObj.put("sender","app");
    }
    public void run() {
        Log.d("requestType",requestType);
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            Log.d("OUT",jsonObj.toString());
            requestSocket = new Socket("192.168.1.21", 5330);
            Log.d("REQUESTED SOCKET","success");
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
            out.writeObject(jsonObj.toString());
            out.flush();

            if (requestType.equals("get") | requestType.equals("filter")|requestType.equals("getUser") | requestType.equals("getRents") ){

                String answer = (String) in.readObject();
                System.out.println("Ap to client PHRA"+answer);
                setJsonArray(new JSONArray(answer));
                this.answerRecieved = true;
                System.out.println(answerRecieved);


            }
            if(requestType.equals("rent")){
                String answer = (String) in.readObject();
                System.out.println("Ap to Reducer PHRA"+answer);
                setJsonObj(new JSONObject(answer));
                this.answerRecieved = true;
            }

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException | JSONException e) {
            throw new RuntimeException(e);
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
