//Name: Jack Kelvin
//Student Number: z5413924
//Course: COMP6841


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ClientController implements Runnable
{
    //Constructor with sockets and Enigma Service passed to function
    public ClientController(Socket messageSocket, Socket settingSocket, EnigmaService es)
    {
        try//attempt to set up and store provided data locally
        {
            this.es = es;//enigma service stored locally. One Individual service passed to every threaded instance of client controller to ensure that when "generateSettings" is called, the result is the same for every controller
            //sockets stored locally
            this.messageSocket = messageSocket;
            this.settingSocket = settingSocket;
            //setup string to store settings that get given to clients upon retrieval of the settings from the enigma service
            //this works backwards where the enigma service has a function to retrieve the data which is called within this function
            setting = "";

            //setup buffers for message socket
            this.br = new BufferedReader(new InputStreamReader(messageSocket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(messageSocket.getOutputStream()));

            //setup buffers for setting socket
            this.settingBr = new BufferedReader(new InputStreamReader(settingSocket.getInputStream()));
            this.settingBw = new BufferedWriter(new OutputStreamWriter(settingSocket.getOutputStream()));

            //read the line in the chat and save locally as the client name that wants to join as that "Name"
            this.clientName = br.readLine();
            checkESForNewSetting();//check for new setting at instantiation of this client

            distributeMessage("[SERVER] " + clientName + " joined");//call function to announce the joining of #name to the server
            System.out.println("[SERVER] " + clientName + " joined");//print locally to the server window
            listOfClients.add(this);//add this instance of a client to the list of clients that is statically loaded for all clients. Allows them to communicate by addressing each one individually

        }
        catch (IOException e)//catch any exception
        {
            e.printStackTrace();//and print
        }
    }

    //run function used to execute threads, runs until thread is stopped which would be when the client disconnects
    public void run()
    {
        //New timer task for checking for new settings. This worked best and didn't cause any issues
        //Periodically checks every 20ms instead of constantly prompting with a recurring function that executes at the speed of computer
        TimerTask checkForSettings = new TimerTask()
        {
            @Override
            public void run()
            {
                checkESForNewSetting();//check for new settings function
            }
        };


        //Scheduler with 20ms as the execution time that will regularly run 20ms
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(checkForSettings, 20, 20, TimeUnit.MILLISECONDS);

        //Message saved as a string
        String message;
        while(messageSocket.isConnected() && settingSocket.isConnected())//while both sockets are connected
        {
            try
            {
                //Save characters to message String which is read from the BufferedReader
                message = br.readLine();
                //Pass message to distributeMessage Function
                distributeMessage(message);
            }
            catch(IOException e)//catch if error occurs
            {
                shutdownClientController();//shutdown function to close Client down
                break;//break out of loop
            }
        }
    }

    public void distributeMessage(String message)//distribute message function
    {
        for(ClientController cl: listOfClients)//for loop for every clientController in listOfClients
        {
            try//attempt
            {
                //if statement to write to any client that does not have the same name as the current client
                //essentially prevents writing to the current client but also prevents duplicates
                //This could be changed to a compare objects by their reference instead but then a visual check should be applied to handle
                //Clients with the same name to know which one sent the message
                if(!cl.clientName.equals(clientName))
                {
                    cl.bw.write(message);//write the message to the client cl's buffer
                    cl.bw.newLine();//write new line
                    cl.bw.flush();//flush (empty) the buffer
                }
            }
            catch (IOException e)//catch if error occurs
            {
                shutdownClientController();//Shutdown function to close client down
            }
        }
    }
    //function to check for new EnigmaService settings and then write them to the buffer so that Clients
    //may access them to ensure every client can communicate to each other with the same settings
    public void checkESForNewSetting()
    {
        try
        {
            if (es.getCurrentSetting() == null)//safety check if the settings in the Enigma Service are null
            {
                es.generateSetting();//Then generate some
            }
            if (!setting.equals(es.getCurrentSetting()))//if local settings of string in class does not equal current settings
            {//in the service, then assume that settings in the service are new and obtain them
                setting = es.getCurrentSetting();//save them locally from ES
                settingBw.write(setting);//write the settings for the current client
                settingBw.newLine();
                settingBw.flush();
            }
        }
        catch (IOException e)//
        {
            shutdownClientController();//Shutdown function to close client down
        }
    }





    //Shutdown function to close client down
    public void shutdownClientController()
    {
        //Function to remove Client Controller from list of all Client Controllers
        removeClientController();
        try//attempt
        {
            if(br != null)//check null
            {
                br.close();//close reader
            }
            if(bw != null)//check null
            {
                bw.close();//close writer
            }
            if(settingBr != null)//check null
            {
                settingBr.close();//close reader
            }
            if(settingBw != null)//check null
            {
                settingBw.close();//close writer
            }
            if(messageSocket != null)//check null
            {
                messageSocket.close();//close message socket
            }
            if(settingSocket != null)//check null
            {
                settingSocket.close();//close setting socket
            }
        }
        catch (IOException e)//catch failure
        {
            e.printStackTrace();//print error
        }
    }

    //Function to remove Client Controller from list of all Client Controllers
    public void removeClientController()
    {
        listOfClients.remove(this);//call to remove object with 'this' keyword which affects specific thread of a client controller
        distributeMessage("[SERVER] " + clientName + " has left");//notification that client has left with name passed to distributeMessage function
        System.out.println("[SERVER] " + clientName + " has left");//same as above but printed locally to server console
    }

    //statically load a list of clients so that any client can can communicate to any client within the list
    public static ArrayList<ClientController> listOfClients = new ArrayList<>();

    //private member data
    //Enigma Service variable
    private EnigmaService es;
    //Message and Setting socket variable
    private Socket messageSocket;
    private Socket settingSocket;
    //String for name and setting variable
    private String clientName;
    private String setting;

    //bufferedReader and bufferedWriter variables for message and setting sockets
    private BufferedReader br;
    private BufferedWriter bw;

    private BufferedReader settingBr;
    private BufferedWriter settingBw;
}
