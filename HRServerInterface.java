/*Kourias Triantafyllos-Dimitrios cs141092*/

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HRServerInterface extends java.rmi.Remote {
//Declare server functions which client calls
    public int [][] list (String hostname)
    throws java.rmi.RemoteException;

    public String book (String hostname, int type, int number, String name,String answer)
    throws java.rmi.RemoteException;

    public String [][] guests (String hostname)
    throws java.rmi.RemoteException;


    public String cancel (String hostname, int type, int number, String name)
    throws java.rmi.RemoteException;

}
