/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mqtt.lele.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author lele
 */
public class MqttBroker_Lele implements MqttCallback {
    private static MqttBroker_Lele instance = null;
    private Process mqtt = null;
        
    private static String START;
    private static String PROCESS;
    private static String STOP;
    
    private String broker = "tcp://localhost:1883";
    private String clientId = "server";
    private boolean ignore = false; // ?????
    private MqttClient sampleClient = null;    
    
    private static void init_Linux(){
        START = "ps -C mosquitto";
        PROCESS = "mosquitto";
        STOP = "pkill mosquitto";
    }
    
    private static void init_win(){
        START = "./mosquitto/mosquitto.exe";
        PROCESS = "mosquitto.exe *32";
        STOP = "taskkill /F /IM";        
    }
    
    public static MqttBroker_Lele getInstance() {
        if (instance == null) {
            instance = new MqttBroker_Lele();
            return instance;
        } else {
            return instance;
        }
    } 
    
    private MqttBroker_Lele() {
        super();
        init_Linux();
//        init_win();
    }
    
    public String isProcessRunning() throws IOException{
        Process p = Runtime.getRuntime().exec(START);
        BufferedReader reader = 
            new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        String line;
        
        while ((line = reader.readLine()) != null) {

            System.out.println(line);
            if (line.contains(PROCESS)) {
                return "process found!";
            }
        }
       
        return null;
    }
    
    public String connect()  {
        try {

            if ( mqtt == null || isProcessRunning() == null ){
                mqtt = Runtime.getRuntime().exec(PROCESS);
                System.out.println("MQTT connected");
                System.out.println("subscribing all");
                MemoryPersistence persistence = new MemoryPersistence();

                sampleClient = new MqttClient(broker, clientId, persistence);

                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                System.out.println("Connecting to broker: " + broker);
                sampleClient.connect(connOpts);
                System.out.println("Connected");

                sampleClient.setCallback(instance);
                sampleClient.subscribe("chat");
                sampleClient.subscribe("#");
           
                System.out.println("Disconnected");                
                
            }
            return "Connect";
        } catch (MqttException xe) {
            System.out.println("reason " + xe.getReasonCode());
            System.out.println("msg " + xe.getMessage());
            System.out.println("loc " + xe.getLocalizedMessage());
            System.out.println("cause " + xe.getCause());
            System.out.println("excep " + xe);
            xe.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(MqttBroker_Lele.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String disconnect() {
        try {          
            Runtime.getRuntime().exec(STOP);
            return "Disconnect";
        } catch (IOException ex) {
            Logger.getLogger(MqttBroker_Lele_OLD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    

    @Override
    public void connectionLost(Throwable thrwbl) {
        System.out.println("we have a problem!");
        thrwbl.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        System.out.println("message received -> " + mm);
        System.out.println("topic: " + topic);
       
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        System.out.println("delivery complete");
    }
    
    public void publish(String topic, String message) throws MqttException{
        sampleClient.publish(topic, new MqttMessage(message.getBytes()));
    }
}
