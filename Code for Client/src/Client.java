//Name: Jack Kelvin
//Student Number: z5413924
//Course: COMP6841


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    //constructor
    public Client(Socket messageSocket, Socket settingSocket,  String name)//given sockets and string containing name of the client wishing to connect to the server
    {
        clientEnigma = new Enigma();//new enigma class stored in class as clientEnigma
        try//attempt
        {
            this.messageSocket = messageSocket;//save sockets locally
            this.settingSocket = settingSocket;
            this.name = name;//save name locally

            this.br = new BufferedReader(new InputStreamReader(messageSocket.getInputStream()));//save buffered reader locally
            this.bw = new BufferedWriter(new OutputStreamWriter(messageSocket.getOutputStream()));//save buffered writer locally
            this.settingBR = new BufferedReader(new InputStreamReader(settingSocket.getInputStream()));//save setting reader locally\
            //no need for setting writer for this class
        }
        catch (IOException e)//catch any error
        {
            closeClient();//function to close client
        }
    }


    //function that sends messages to server using buffered writer
    public void sendMessage()
    {
        try//attempt
        {
            bw.write(name);//write name to buffer for output on other clients and server console
            bw.newLine();
            bw.flush();//flush buffer for new data to avoid data being duplicated once used
            Scanner sc = new Scanner(System.in);//new scanner object to obtain messages that client wishes to send
            while(messageSocket.isConnected() && settingSocket.isConnected())//check while these two sockets are connected as we are using both for setting obtainment and message writing
            {
                String message = sc.nextLine();//obtain message given by console
                listenForSetting();//check for any settings periodically
                message = clientEnigma.encrypt_decrypt(message);//get the message and pass to the locally stored instance of the Enigma class under clientEnigma. Pass to function Encrypt_Decrypt which will encrypt or decrypt message with its current settings
                bw.write(name + ": " + message);//write message with name to the server by writing to the buffered writer
                bw.newLine();
                bw.flush();//flush the buffer
                System.out.println(message);//print the message to the console
            }
        }
        catch (IOException e)//catch any errors
        {
            closeClient();//function to close the client
        }
    }


    //listen for messages function
    //threaded so that it dynamically listens whilst sending so that we don't miss any given input from other clients
    public void listen()
    {
        new Thread(() ->//thread spawned with lambda which allows me to specify the method I want it to run
        {
            String message;//locally saved variable for any incoming message
            while (messageSocket.isConnected())//check connected which is the message socket, no need for setting socket as it does not get used for this data
            {
                try//attempt
                {
                    message = br.readLine();//message is assigned output from reading the buffered reader and all its content
                    int mark = 0;//mark index at 0;
                    if(message.contains("SERVER"))//if contains "server" in the name, then print the output as it is not needed to be passed into the decryption function
                    {
                        System.out.println(message);//print message to console
                    }
                    else//if it does not contain server in message
                    {
                        for (int i = 0; i < message.length(); i++)//loop from 0 to the length
                        {
                            if (message.charAt(i) == ':')//find when character is equal to colon character
                            {
                                mark = i + 2;//mark message start and 2 indexes from the index where colon is, this is always guaranteed and so it can always be run without issue
                            }
                        }
                        listenForSetting();// listen for setting again
                        String decryptedMsg = clientEnigma.encrypt_decrypt(message.substring(mark));//Pass the message to the function to be decrypted as a substring starting from the mark index. Save output to th 'decryptedMsg' variable
                        System.out.println(message);//print the original message
                        System.out.println("DECRYPTED: " + decryptedMsg);//print the decryption
                    }
                }
                catch (IOException e)//if failed
                {
                    closeClient();//close client function called
                }
            }
        }
        ).start();//start thread
    }


    //function to listen for any change to settings buffer
    //tried as a thread but gave issues. Checking before sending and receiving is good practice
    public void listenForSetting()
    {
        try//attempt
        {
            if(settingBR.ready())//check if the setting Buffered reader has some data to be read
            {
                String test = settingBR.readLine();//save to a string
                clientEnigma.giveSettings(test);//give to locally stored clientEnigma to apply
                clientEnigma.printFormattedSettings();//call function to print these settings to the screen for the user's viewing
            }
        }
        catch (IOException e)//catch any issues
        {
            closeClient();//function to close the client
        }
    }

    //function to close the client
    public void closeClient()
    {
        try//attempt
        {
            if(br != null)//check if buffered reader is not null
            {
                br.close();//close
            }
            if(settingBR != null)//check if setting Buffered reader is not null
            {
                settingBR.close();//close
            }

            if(bw != null)//check if buffered writer is not null
            {
                bw.close();//close
            }

            if(messageSocket != null)//check if message socket is not null
            {
                messageSocket.close();//close
            }
            if(settingSocket != null)//check if setting socket is not null
            {
                settingSocket.close();//close
            }
        }
        catch (IOException e)//if failed
        {
            e.printStackTrace();//print stack trace explaining why
        }
    }



    //private member data
    private Socket messageSocket;//store for message socket
    private Socket settingSocket;//store for setting socket
    private String name;//store for name string
    private BufferedReader br;//store for buffered reader for message socket
    private BufferedWriter bw;//store for buffered writer for message socket
    private BufferedReader settingBR;//store for buffered reader for setting socket
    private final Enigma clientEnigma;//store for Enigma Class
}
