//Name: Jack Kelvin
//Student Number: z5413924
//Course: COMP6841

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//Allows the creation and thus connection of each client by making a client controller for every client
//Also used to change the settings of enigma and thus allows for the client controller to access the enigma service and detect if new settings are available
//Enigma service is instantiated here which allows the server to make and distribute settings
public class Server
{
    //constructor
    public Server(ServerSocket messageSocket, ServerSocket settingSocket)//contains the two sockets given as input
    {
        this.es = new EnigmaService();//Save enigma service locally
        es.generateSetting();
        //save socket locally
        this.messageSocket = messageSocket;
        this.settingSocket = settingSocket;

    }

    //begin server function
    public void startServer()
    {
        try//catches failures
        {
            TimerTask changeSettings = new TimerTask()//timer for setting generation, essentially a thread that will execute a local function within the server
            {
                @Override
                public void run()//run command for the thread
                {
                    es.generateSetting();//Enigma service function to make new settings
                    //return those settings to the console
                    System.out.println(es.getCurrentSetting());//this is in number form where A = 0, rotor 1 = 0 etc
                }
            };

            //For Demo - Regular change of settings for demonstration
            //ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            //scheduler.scheduleAtFixedRate(checkForSettings, 20000, 20, TimeUnit.MILLISECONDS);

            //For Daily settings - Similar to how Germans communicated in WW2 by changing settings close to midnight
            long delay = ChronoUnit.MILLIS.between(LocalTime.now(), LocalTime.of(23, 59, 55));//timer between the given date and right now stored in a long variable as milliseconds, this will be when the function is next run
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);//new Schedule execute service, allow for regular scheduling
            scheduler.scheduleAtFixedRate(changeSettings, delay, delay, TimeUnit.MILLISECONDS);

            while(!messageSocket.isClosed())//communication loop
            {
                Socket mSocket = messageSocket.accept();//handshake for both sockets when a new client attempts connection
                Socket sSocket = settingSocket.accept();
                //New client controller for the client that is requesting connection
                ClientController newClientController = new ClientController(mSocket, sSocket, es);
                Thread serverThread = new Thread(newClientController);//start as a thread to allow it to run by itself
                serverThread.start();//starting function for that thread
            }
        }
        catch(IOException e)//catch if IOException occurs
        {
            stopServer();//stop server
        }
    }



    //stop server function
    public void stopServer()
    {
        try
        {
            if(messageSocket != null)//check if null
            {
                messageSocket.close();//then attempt closing of the buffer
            }
            if(settingSocket != null)
            {
                settingSocket.close();
            }
        }
        catch (IOException e)//catch if error occurs closing the socket
        {
            e.printStackTrace();
        }
    }

    //private member data
    //store sockets and enigma service
    private final ServerSocket messageSocket;
    private final ServerSocket settingSocket;
    private final EnigmaService es;
}
