/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mqtt.lele.logic.event;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author lele
 */
public class Dispatcher {
    private static Dispatcher instance = null;
    private List <Listener> listeners = new ArrayList<>();

    
    public static Dispatcher getInstance(){
        if ( instance == null ){
            instance = new Dispatcher();
        }
        return instance;
    }    
    
    public void addEvent(Listener listener){
        listeners.add(listener);
    }
    
    public void update(String value){
        for (Listener listener : listeners) {
            listener.update(value);
        }
    }
    
    public Dispatcher() {
    }
}
