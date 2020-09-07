/*Kourias Triantafyllos-Dimitrios cs141092*/

import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class HRServerImpl extends java.rmi.server.UnicastRemoteObject
  implements HRServerInterface {

  //array with available rooms and its cost,used in function list
  public int [][] Rooms = new int [][]{{30,50},{45,70},{25,80},{10,120},{5,150}};

  //array filled with clients' names (115 because the hotel has 115 rooms combined,so can afford max 115 clients)
  public String [] Clients = new String [115];

  //array filled booked entries. Clients name's are stored in Clients[] because are strings ,
  //in the same position in this array are stored: |  Room Type | Rooms | Total Cost |
  public int [][] Bookings = new int [115] [3];

  // the index variable points the number that has the last entry of the bookings,so its value is also and the total bookings
  public int index = 0;

  public HRServerImpl() throws RemoteException{}  //Constructor

  //Function that returns the array of available rooms
  public int [][] list(String hostname)
	  throws java.rmi.RemoteException {

		    System.out.println("Client asked for available Rooms");
        return Rooms;
    }

  /*Functions that book the request rooms by type.There are 3 conditions:
    if there are no available rooms in requested type so it returns the appropriate servermessage
    if the booking can be done so it returns a success servermessage
    if the booking can be done but not for the requested rooms,only for the rooms that have left,it returns to client a message
    that asks the client if he wants to book only the remaining rooms,he answers and then calls the function again with the variable answer filled,
    if yes the booking happens if no aborted.
    When the bookings can be done the server update the available rooms array and add the name,type,rooms,cost in the entries array and increase the index
  */
	public String book (String hostname, int type, int number, String name, String answer)
      throws java.rmi.RemoteException{

		      String servermessage = "";
          if(Rooms[type-1][0] == 0){
              //if the booking can be done,becase there are no more rooms
			         servermessage = "No available rooms in type " +(char) (type+64);
		      }else if (Rooms[type-1][0] - number < 0){
                if (answer == null){
  				            servermessage = "There are only " +Rooms[type-1][0]+ " rooms,do you want to book the last " +Rooms[type-1][0]+ "? (Yes/No)";
  			        }
                //if the booking can be done not for all the requested rooms but only for the availables
          			else if (answer.equals("yes") || answer.equals("y") || answer.equals("Y")){
          				Clients[index] = name;
          				Bookings[index][0] = type-1;
          				Bookings[index][1] = Rooms[type-1][0];
          				Bookings[index][2] = Rooms[type-1][1] * number;
          				Rooms[type-1][0] = 0;

          				System.out.println("Client " +Clients[index]+ " booked " +Bookings[index][1]+ " rooms type " +(char) (Bookings[index][0]+65)+ " of total cost " +Bookings[index][2]+ "€/night");

          				servermessage = Clients[index]+ ", you have succesfully booked " +Bookings[index][1]+ " rooms type " +(char) (Bookings[index][0]+65)+ " of total cost " +Bookings[index][2]+ "€/night";

          				index++;
          			}else
          				{servermessage = "Booking aborted";}
		      }else{
                //if the booking can be done normally
          			Clients[index] = name;
          			Bookings[index][0] = type-1;
          			Bookings[index][1] = number;
          			Bookings[index][2] = Rooms[type-1][1] * number;
          			Rooms[type-1][0] = Rooms[type-1][0] - number;
          			System.out.println("Client " +Clients[index]+ " booked " +Bookings[index][1]+ " rooms type " +(char) (Bookings[index][0]+65)+ " of total cost " +Bookings[index][2]+ "€/night");
          			servermessage = Clients[index]+ ", you have succesfully booked " +Bookings[index][1]+ " rooms type " +(char) (Bookings[index][0]+65)+ " of total cost " +Bookings[index][2]+ "€/night";
          			index++;
          }
		      return servermessage;
	 }


  //Function that fills a string array (guests[]) with all the client's data(name,type,rooms,cost) and then returns the array to client
	public String [][] guests (String hostname)
		throws java.rmi.RemoteException{

    String [][] Guestlist = new String [115][4];
		System.out.println("Client asked to see the booking list.");

		for(int i=0;i<index;i++){
				Guestlist[i][0] = Clients[i];
				Guestlist[i][1] = String.valueOf((char) (Bookings[i][0]+65));
				Guestlist[i][2] = String.valueOf(Bookings[i][1]);
				Guestlist[i][3] = String.valueOf(Bookings[i][2]);
		}
		return Guestlist;
  }

  //Function that either cancels complete a booking with specific type under a name or cancels some rooms in an entry
	public String cancel (String hostname, int type, int number, String name)
		throws java.rmi.RemoteException{

    //varibles used in seach
		int foundflag = 0;
		int pos = 0;
    String message = "";

    //search the client's array for the name and the type and store its position
    //if the name has been found but has booked also other types of rooms, it stores all the other entries in a message to inform the client
		for(int i=0;i<index;i++){
			if(Clients[i].equals(name) && Bookings[i][0] == type-1 && Bookings[i][1] >= number ) {
				foundflag= 1;
				pos=i;
			}
			if(Clients[i].equals(name) && Bookings[i][0] != type-1) {
				message = String.format("\t%s\n\t%s\t%s\t%s\t%s",message,Clients[i], String.valueOf((char) (Bookings[i][0]+65)),String.valueOf(Bookings[i][1]), String.valueOf(Bookings[i][2]));
			}
		}

    //if the search hasn't found the name return the message
		if(foundflag == 0){
			message = "\tNo booking with that data found";
		}else{

      //if the message contains other booking of the client add columns title to the message
			if (message != null){
				message = String.format("\tOther bookings of client:\n\tName\tType\tRooms\tCost %s \n",message);
			}

      //if the client has requested to cancel fewer room that the rooms that has booked,the substract the rooms from his entry and the rooms array
			if(Bookings[pos][1] > number){
				Bookings[pos][1] = Bookings[pos][1] - number;
				System.out.println("Client cancelled " +number+ " rooms for " +name);
        Rooms[type-1][0] = Rooms[type-1][0] + number;
				message = String.format("\t%s rooms type %s cancelled for %s \n",number,String.valueOf((char) (type+64)),name);

			}else{

        //if the client wants too cancel all his room of a type,then move the entries arrays one position up and the last position of the array make it null
				System.out.println("Client cancelled booking type " +String.valueOf((char) (type+64))+ " for " +name);
				message = String.format("\t%s cancelled booking in all rooms type %s \n%s", name, String.valueOf((char) (type+64)), message);

				for(int i=pos+1;i<index;i++){
          Rooms[type-1][0] = Rooms[type-1][0] + number;
					Clients[i-1] = Clients[i];
					Bookings[i-1][0] = Bookings[i][0];
					Bookings[i-1][1] = Bookings[i][1];
					Bookings[i-1][2] = Bookings[i][2];
				}

				Clients[index] = null;
				Bookings[index][0] = 0;
				Bookings[index][1] = 0;
				Bookings[index][2] = 0;
				index--;
			}
		}
		return message;
	}
}
