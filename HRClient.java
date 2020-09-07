/*Kourias Triantafyllos-Dimitrios cs141092*/

import java.io.*;
import java.rmi.*;
import java.net.MalformedURLException;
import java.util.Scanner;

public class HRClient {
    public static void main(String[] args) {
      //Declare variables,arrays
    	int [][] Rooms = new int [5][2];             //array returned from server with available rooms and its cost,used in function list
    	String [][] Guestlist = new String [115][4]; //array returned from server with guests' name, type of room, number of rooms, total cost. Used in function guests
    	int type=0;                                  //type of room entered by client in args
      String servermessage = "";                   //message returned by server,used in most functions, used in functions book & cancel
      String answer = null;                        //answer sent to server when there are not enough rooms,if client want to book the remaining rooms

      try {
            HRServerInterface hr = (HRServerInterface) Naming.lookup("rmi://localhost:7500/HotelReservetionService");


            if (args.length==0){
                  //Checks if client has arguments,if not show the correct arguments
                  System.out.println("\tUsage:");
                  System.out.println("\tjava HRClient list <hostname>");
                  System.out.println("\tjava HRClient book <hostname> <type> <number> <name>");
                  System.out.println("\tjava HRClient guests <hostname>");
                  System.out.println("\tjava HRClient cancel <hostname> <type> <number> <name>");
                  System.exit(0);

            } else {

                  if(args[0].equals("list")){

                    //if client chooses to see list of available rooms, checks first for correct arguments else print the correct usage
                    if(args.length==2){

                                //Call server function to get available room array by type and its cost and print the array
						                    Rooms = hr.list(args[0]);
						                    System.out.println("\tΑvailable Rooms:");
						                    for (int i=0;i<5;i++) {
							                           System.out.println("\t"+Rooms[i][0]+ " available rooms of type " +(char) (i+65)+ " costing " +Rooms[i][1]+ " €/night");
						                    }

                        } else {
                            System.out.println("\tUsage: java HRClient list <hostname>");
                            System.exit(0);
                        }


                  //if client chooses to book a room, checks first for correct arguments else print the correct usage
                  } else if(args[0].equals("book")){
                      if(args.length==5){

                              //checks type of room string input and transforms it to an integer variable
                              if(args[2].equals("A")||args[2].equals("a")||args[2].equals("1")){type=1;}
                              else if(args[2].equals("B")||args[2].equals("b")||args[2].equals("2")){type=2;}
                              else if(args[2].equals("C")||args[2].equals("c")||args[2].equals("3")){type=3;}
                              else if(args[2].equals("D")||args[2].equals("d")||args[2].equals("4")){type=4;}
                              else if(args[2].equals("E")||args[2].equals("e")||args[2].equals("5")){type=5;}

                              //Call server function to book rooms with given input and print the message returned (the answer variable is null)
							                servermessage = hr.book(args[1],type,Integer.parseInt(args[3]),args[4],answer);
							                System.out.println("\t"+servermessage);

                              //if server returned a question then wait for client answer-input and call the function again
                              //but this time with the answer as parameter with a value and not null
                              //then print if the booking was a success or aborted
                              //this part of code runs if client request more rooms than the available and asks him if he want to book the available rooms
							                if (servermessage.substring(servermessage.length() - 8).equals("(Yes/No)"))
                    					{
                      				      Scanner input = new Scanner(System.in);
                      				      Scanner scanner = new Scanner( System. in);
                      				      answer = scanner. nextLine();
                    					      servermessage = hr.book(args[1],type,Integer.parseInt(args[3]),args[4],answer);
                    					      System.out.println("\t" +servermessage);
                  							 }
                       }else {
                              System.out.println("\tUsage: java HRClient book <hostname> <type> <number> <name>");
                              System.exit(0);
                        }


                  //if client chooses to see all guests of the hotel, checks first for correct arguments else print the correct usage
                  } else if(args[0].equals("guests")) {
                        if(args.length==2){

                            //Call server function which returns an array with all the booked entries,containing name, type of room, number of rooms, total cost
                            //then print the array and count the guests and print them too

              							Guestlist = hr.guests(args[1]);
              							System.out.println("\tName\tType\tRooms\tCost");
              							int guestcounter=0;
              							for(int i=0;i<115;i++){
                								if (Guestlist[i][0] != null)
                								{
                									guestcounter++;
                									System.out.println("\t"+Guestlist[i][0]+ "\t  " +Guestlist[i][1]+ "\t" +Guestlist[i][2]+ "\t" +Guestlist[i][3]+ "€/night");
                								}else
                                {break;} //print only the part of the array that is filled,not the rest which is null
							              }
							              System.out.println("\tTotal Guests: " +guestcounter);
                            
                        } else {
                            System.out.println("\tUsage: java HRClient guest <hostname>");
                            System.exit(0);
                      }


                  //if client chooses to cancel a booking or cancel some rooms of a booking, checks first for correct arguments else print the correct usage
                  } else if(args[0].equals("cancel")) {
                        if(args.length==5){

                            //checks type of room string input and transforms it to an integer variable
            							  if(args[2].equals("A")||args[2].equals("a")||args[2].equals("1")){type=1;}
            							  else if(args[2].equals("B")||args[2].equals("b")||args[2].equals("2")){type=2;}
            							  else if(args[2].equals("C")||args[2].equals("c")||args[2].equals("3")){type=3;}
            							  else if(args[2].equals("D")||args[2].equals("d")||args[2].equals("4")){type=4;}
            							  else if(args[2].equals("E")||args[2].equals("e")||args[2].equals("5")){type=5;}
            							  else {System.out.println("\tNo such room type");System.exit(0);}

                            //call cancel server function,server returns a message which either contains not found servermessage
                            //either if the cancellation was a success and the other bookings under the same name
							              servermessage = hr.cancel(args[1],type,Integer.parseInt(args[3]),args[4]);
							              System.out.println(servermessage);

						            }else{
                            System.out.println("\tUsage: java HRClient cancel <hostname> <type> <number> <name>");
                            System.exit(0);
                        }

                  }
				          System.out.print("\n");
          }

     }
      catch (MalformedURLException murle) {
          System.out.println();
          System.out.println(
					"MalformedURLException");
          System.out.println(murle);
      }
      catch (RemoteException re) {
          System.out.println();
          System.out.println(
                    "RemoteException");
          System.out.println(re);
      }
      catch (NotBoundException nbe) {
          System.out.println();
          System.out.println(
                    "NotBoundException");
          System.out.println(nbe);
      }
    }
}
