//Name: Jack Kelvin
//Student Number: z5413924
//Course: COMP6841

import java.io.*;
import java.net.ServerSocket;



public class Main
{
    //Server
    //Contains functionality to allow messages to and from different clients that are connected
    //Also allows for an Enigma Service to generate and distribute settings for the enigma machine to allow for clients to
    //communicate with encrypted messages. Settings Dynamically change at 5 seconds to midnight
    //Demo will contain a different mechanism to allow for settings to change at a faster pace for demonstration of these setting changes
    public static void main(String[] args) throws IOException
    {
        //Two ports for two different types of data.
        //Learnt a lot about network programming and this is one of the ways I could differentiate between the two different data sets
        //Ran on two different ports as I was having issues with the same port. Need to learn more about this
        ServerSocket messageSocket = new ServerSocket(1000);//1000 port
        ServerSocket settingSocket = new ServerSocket(1001);//1001 port
        //New Server class with both sockets as input
        Server myServer = new Server(messageSocket, settingSocket);
        //Server run command
        myServer.startServer();
    }
}
