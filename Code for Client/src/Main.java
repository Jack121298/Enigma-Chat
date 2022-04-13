//Name: Jack Kelvin
//Student Number: z5413924
//Course: COMP6841


import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class Main
{
    //TODO
    // Video + powerpoint
    // Blog with resources
    // Cleanup + README
    // Testing!!!

    //main function
    public static void main(String[] args) throws IOException
    {
        //scanner class for entering a name into the system
        Scanner sc = new Scanner(System.in);
        //prompt user to enter name by printing prompt to the console
        System.out.println("Enter Name: ");
        //save name to a local variable called 'name'
        String name = sc.nextLine();
        //open socket on local host ip and at port 49150 and 49151
        //2 sockets for 2 data streams and easy differentiation of these streams. Message socket for messages, settings socket for moving a data
        Socket messageSocket = new Socket("localhost", 49150);
        Socket settingSocket = new Socket("localhost", 49151);

        //New client instance, with sockets and name variable passed in
        Client client = new Client(messageSocket, settingSocket, name);
        client.listen();//start thread for listening
        client.sendMessage();//start thread for sending messages
    }
}
