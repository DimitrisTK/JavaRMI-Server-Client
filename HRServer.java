/*Kourias Triantafyllos-Dimitrios cs141092*/

import java.rmi.Naming;
import java.io.*;
import java.rmi.*;

public class HRServer {

    public HRServer () {
        try{
            HRServerInterface hr = new HRServerImpl();
            Naming.rebind("rmi://localhost:7500/HotelReservetionService", hr);
			System.out.println("Server is up");
			System.out.println("Waiting for client request...");
        }
        catch (Exception e) {
          System.out.println("Trouble: " + e);
        }
    }

    public static void main(String[] args) {
        new HRServer();
    }
}
