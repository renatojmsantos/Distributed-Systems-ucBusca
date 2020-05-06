package MulticastServer;

import RMIServer.Inter;

import java.io.Serializable;
import java.util.ArrayList;
import java.lang.*;
import java.rmi.RemoteException;


/**
 * @author renatosantos
 * @author pedrosimoes
 */

public class User implements Serializable {
    private String username;
    private String password;

    Inter inter;

    private boolean admin;
    public ArrayList<String> history;
    private boolean notification;

    public User(){
    }

    public User(Inter inter, String username){
        this.username = username;
        this.inter = inter;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.admin = false;
        this.history = new ArrayList<String>();
        this.notification = false;
        this.inter = inter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void addHistory(String s){
        history.add(s);
    }

    public void setHistory(ArrayList<String> history) {
        this.history = history;
    }

    public boolean getNotification() {
        return notification;
    }

    public void setNotification(boolean n) {
        this.notification = n;
    }


    public void notifica() {
        try {
            inter.notificadmin("Agora é administrador!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", admin=" + admin +
                ", history=" + history +
                ", notification=" + notification +
                '}';
    }
}
