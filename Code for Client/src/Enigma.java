//Name: Jack Kelvin
//Student Number: z5413924
//Course: COMP6841

import java.util.ArrayList;
import java.util.Locale;

public class Enigma
{

    //constructor
    public Enigma()
    {
        //rotors array with size 5 for each rotor from I to V
        rotors = new Rotor[5];
        //reflectors array with size 2 for each reflector from UKW-B to UKW-C
        reflectors = new Reflector[2];
        //2 Key arraylist. Int in key1 at index I matches to index i in key2. Integers are letters in numerical form
        key1 = new ArrayList<>();
        key2 = new ArrayList<>();
        //formatted settings as an arraylist. Used arraylist because of dynamically sized plugboard settings
        formattedSettings = new ArrayList<>();
        //initialized each rotor and reflector with their respective ciphers
        initRotorsAndReflectors();

    }


    //function to instantiate each rotor and reflector
    //loads their ciphers and stores locally within their respective class
    //provides where the notches are for each rotor
    //also gives a value to each rotor for what rotor it is. 0 -> I, 3 -> IV, 4 -> V
    public void initRotorsAndReflectors()
    {
        //load a rotor at index 0 to 5 with each rotor and give, notch data, cipher and the rotor number which corresponds to what it is for later printing
        //save within the rotors array
        rotors[0] = new Rotor(16, "EKMFLGDQVZNTOWYHXUSPAIBRCJ", 0);
        rotors[1] = new Rotor(4, "AJDKSIRUXBLHWTMCQGZNPYFVOE",1);
        rotors[2] = new Rotor(21, "BDFHJLCPRTXVZNYEIWGAKMUSQO",2);
        rotors[3] = new Rotor(9, "ESOVPZJAYQUIRHXLNFTGKDCMWB",3);
        rotors[4] = new Rotor(25, "VZBRGITYUPSDNHLXAWMJQOFECK",4);

        //create 2 reflectors and pass the cipher text to be saved locally within their classes
        //store in an array called reflectors
        reflectors[0] = new Reflector("YRUHQSLDPXNGOKMIEBFZCWVJAT");
        reflectors[1] = new Reflector("FVPJIAOYEDRZXWGCTKUQSBNMHL");

    }


    //encrypt and decrypt function
    //both are the same as encrypting a message X with Y settings results in Z cipher text
    //running Z back through any Enigma machine with Y settings will result in X -> Decryption!
    public String encrypt_decrypt(String messageToEncrypt)//pass the string into the function to be encrypted
    {
        if(messageToEncrypt.equals(""))//check empty string
        {
            return "";//return empty string and stop execution function
        }
        messageToEncrypt = messageToEncrypt.toUpperCase(Locale.ROOT);//convert to uppercase lettering, keeping to Enigma standards. Save back to string
        messageToEncrypt = removeNonAlphabetChars(messageToEncrypt);//remove any symbol from string. Save back to string

        StringBuilder output = new StringBuilder();//new String builder
        for(int i = 0; i < messageToEncrypt.length(); i++)//loop through message and get one character at a time
        {
            char nextCharacter = messageToEncrypt.charAt(i);//save char to 'nextCharacter' variable
            if(nextCharacter != 32)//check if not whitespace
            {
                int x = letterEncyrptDecrypt((int) messageToEncrypt.charAt(i) - 65);//encrypt letter by passing in the int equivalent of the character in question to the "letterEncryptDecrypt" function. Subtract 65 to get letter ordering!
                output.append((char) (x + 65));//append back to string builder variable, casting to char and adding 65 to get specific character in letter form
            }
            else
            {
                output.append(" ");//append whitespace back. NOT GENUINE TO ENIGMA!!! But a design decision for demonstration of the functionality for this project. Allows for decryption to line up perfectly!
            }
        }
        loadData();//reset the machine by reverting rotor start positions and ring settings. Loads data from a saved string of what the settings should be
        return output.toString();//return the string builder as a string to the caller of this function
    }

    //Main function of per letter encryption
    //very complicated and has a series of steps to be successfully
    //function gets given an integer and returns an integer
    //essentially a finite state machine that changes states per letter of given input

    //characters go through plugboard, then the rotors from the right side first, hitting each rotor from 1 to 3, then the reflector and then back left to right from 3 to 1, then the plugboard for a final time before being "displayed"
    public int letterEncyrptDecrypt(int charToBeEncrypted)
    {

        //main logic to determine if a rotor can move
        //rotor move based on if a notch has been arrived at to from its right neighbour
        //right-most rotor, or rotor 1, moves freely with no restrictions
        //implements double stepping where if a rotor moves, then the rotor to its right moves as well. Rotor 1 is not bounded by this and is free moving on any key press
        if(rotorPos2.canMoveLeft())//first check if rotor2 has reached its notch and can allow for rotor 3 to move
        {
            //if true
            //move all 3 rotors as rotor 1 can move freely with no prerequisites and rotor 2 moves from
            rotorPos2.incrementPos();
            rotorPos3.incrementPos();
            rotorPos1.incrementPos();
        }
        else if(rotorPos1.canMoveLeft())//else check if rotor 1 has reached its notch and can allow for rotor 2 to move
        {
            //if true then move rotor 1 and 2
            rotorPos2.incrementPos();
            rotorPos1.incrementPos();
        }
        else//just move rotor 1 if above logic is not met. Freely moving and so no condition must be met except a letter to be given to the machine which will always happen in this function
        {
            //move rotor 1
            rotorPos1.incrementPos();
        }


        //run the character given to function through the plugboard and save back to that variable. May or may not change if a pair-conversion exists with that letter for the machines current settings
        charToBeEncrypted = plugBoard(charToBeEncrypted);

        //pass character through the rotors and encrypt them with function "encrypt right to left"
        charToBeEncrypted = rotorPos1.encryptRightToLeft(charToBeEncrypted);//rotor 1 encryption of character, right to left
        charToBeEncrypted = rotorPos2.encryptRightToLeft(charToBeEncrypted);//rotor 2 encryption of character, right to left
        charToBeEncrypted = rotorPos3.encryptRightToLeft(charToBeEncrypted);//rotor 3 encryption of character, right to left

        //reflector encryption
        charToBeEncrypted = reflectorCurrent.encrypt(charToBeEncrypted);

        //pass character through the rotors and encrypt them with function "encrypt right to left"
        charToBeEncrypted = rotorPos3.encryptLeftToRight(charToBeEncrypted);//rotor 1 encryption of character, left to right
        charToBeEncrypted = rotorPos2.encryptLeftToRight(charToBeEncrypted);//rotor 2 encryption of character, left to right
        charToBeEncrypted = rotorPos1.encryptLeftToRight(charToBeEncrypted);//rotor 3 encryption of character, left to right

        //run the character given to function through the plugboard again for the last time and save back to that variable. May or may not change if a pair-conversion exists with that letter for the machines current settings
        charToBeEncrypted = plugBoard(charToBeEncrypted);

        return charToBeEncrypted;//return character which will have changed several times throughout this function

    }

    //plugboard function
    //find match to a character and then displays it
    public int plugBoard(int charToBeEncrypted)
    {
        for(int i = 0; i < key1.size(); i++)//loop through key1 arraylist
        {
            if(key1.get(i) == charToBeEncrypted)//check if exists in key1
            {
                charToBeEncrypted = key2.get(i);//apply conversion by looking at letter in key2 at the same index
            }
            else if(key2.get(i) == charToBeEncrypted)//check if exists in key2
            {
                charToBeEncrypted = key1.get(i);//apply conversion by looking at letter in key1 at the same index
            }
        }
        return charToBeEncrypted;//return to caller
    }

    //function to provide settings to the machine given as a string
    public void giveSettings(String settings)
    {
        this.settings = settings;//save to a string
        loadData();//load the settings to be used
    }



    //function to load settings of the machine by viewing the locally stored settings string variable
    //used to reset as having to dial back ring settings and rotor positions is quite extensive
    //this is much easier to deal with and a lot less buggy
    public void loadData()
    {
        if(!settings.equals(""))//check empty and prevent execution if it is the case
        {
            //ArrayList formatted settings is assigned a value by passing through settings string variable through the "removeCommaAndBracket" Function
            //which is then given as an input to the extract data function which converts the mutated string into an arraylist storing integers
            //then save that output to formattedSettings
            //easier to work with here!
            formattedSettings = extractData(removeCommaAndBracket(settings));

            //load what rotors to use in the rotor slots in the machine
            rotorPos3 = rotors[formattedSettings.get(0)];//load the rotor at index 0 and save into the left-most slot
            rotorPos2 = rotors[formattedSettings.get(1)];//load the rotor at index 1 and save into the middle slot
            rotorPos1 = rotors[formattedSettings.get(2)];//load the rotor at index 2 and save into the right-most slot

            //load rotor starting positions for each rotor
            rotorPos3.changeWheelPosSetting(formattedSettings.get(3));//save rotor position to left-most rotor in left-most slot. Lookup data at index 3
            rotorPos2.changeWheelPosSetting(formattedSettings.get(4));//save rotor position to middle rotor in middle slot. Lookup data at index 4
            rotorPos1.changeWheelPosSetting(formattedSettings.get(5));//save rotor position to right-most rotor in right-most slot. Lookup data at index 5

            //load rotor ring settings for each rotor
            rotorPos3.changeRingSetting(formattedSettings.get(6));//save rotor ring settings to left-most rotor in left-most slot. Lookup data at index 6
            rotorPos2.changeRingSetting(formattedSettings.get(7));//save rotor ring settings to middle rotor in middle slot. Lookup data at index 7
            rotorPos1.changeRingSetting(formattedSettings.get(8));//save rotor ring settings to right-most rotor in right-most slot. Lookup data at index 8

            //load reflector setting
            reflectorCurrent = reflectors[formattedSettings.get(9)];//Use value at index 9 to determine what reflector to use

            init_plugboard();//initialize the plugboard, keeps function shorter and a bit more readable
        }
    }


    //function to load plugboard with correct settings
    public void init_plugboard()
    {
        //Store pairs in individual indexes. Key1 at index i maps to key2 at index i
        key1 = new ArrayList<>();//new arraylist, easy way to clear previous list and let GC handle cleanup of the old list
        key2 = new ArrayList<>();
        //loop through list from 10 to size of array
        //index starts at 10 because that is when plugboard settings start at in settings string
        //dynamic size given as bound as we can have from 0 to 10 pairs given in the settings for enigma
        //increment +2 for each loop as we use i+1 for key2
        //this is because index 10 and 11 are pairs, then 12 and 13, then 14 and 15 and so on
        //would have loved to have used a hashmap but needed it to work both ways like A <-> G and not A -> G.
        for(int i = 10; i < formattedSettings.size(); i+=2)
        {
            key1.add(formattedSettings.get(i));//add to list data at i
            key2.add(formattedSettings.get(i+1));//add to list data at i + 1, thus forming a pair
        }
    }



    //Remove and comma or bracket from string which is given to function
    //used because this string is converted from an int array which carries over these brackets and commas for each element which is very useful for knowing what is what
    public String removeCommaAndBracket(String settings)
    {
        StringBuilder sb = new StringBuilder();//new string builder
        for(int i = 0; i < settings.length(); i++)//loop from 0 to length of string
        {
            if(settings.charAt(i) != ',' && settings.charAt(i) != '[' && settings.charAt(i) != ']')//if char at index is not equal to a comma or any type of bracket, left or right then append to string builder
            {
                sb.append(settings.charAt(i));//add character once guaranteed not to be a comma or a bracket
            }
        }
        return sb.toString();//return to result back to caller
    }

    //convert data string to an ArrayList
    public ArrayList<Integer> extractData(String data)
    {
        ArrayList<Integer> fData = new ArrayList<>();//new arraylist
        int right;//right pointer
        for(int left = 0; left < data.length(); left++)//loop from 0 to length of string. Uses left index for readability
        {
            if(data.charAt(left) != ' ')//if char at left pointer is not whitespace
            {
                right = left;//right is left
                while(right < data.length() && data.charAt(right) != ' ')//while loop to increment right pointer on the basis that it keeps finding data that is not whitespace
                {
                    right++;//increment when whitespace is not found at right pointer
                }
                //get substring from left to right pointers, convert to an integer and then save to Integer Arraylist fdata
                fData.add(Integer.parseInt(data.substring(left, right)));
                left = right;//bump left to right pointer to search for more data
            }
        }
        return fData;//return arraylist
    }


    //function that removes any characters in a string that aren't part of alphabet
    public String removeNonAlphabetChars(String oldMessage)
    {
        StringBuilder message = new StringBuilder();//new String builder
        for(int i = 0; i < oldMessage.length(); i++)//loop from 0 to string message length
        {
            int ascii_val = oldMessage.charAt(i);//save char at index i to an integer
            if((ascii_val >= 65 && ascii_val <= 90) ||  (ascii_val == 32))//check if integer is an alphabet character or whitespace
            {
                message.append(oldMessage.charAt(i));//append that character to string builder
            }
        }
        return message.toString();//return character
    }

    //function to easily display settings so the user can make sense of them for their own use
    public void printFormattedSettings()
    {
        //string builder to format settings in a good way
        //was going to use my white space generated to allow text to line up with top part being the slots and that slots respective data
        //hard when:
        //ROTOR SLOTS(LR):   3  2  1
        //ROTOR CHOICES(LR): III  I  II
        //which could look like this instead:
        //******ROTOR SLOTS(LR): 3      2      1
        //****ROTOR CHOICES(LR): III    I      II
        //ran out of time to implement
        StringBuilder sb = new StringBuilder();
        //heading and newline
        sb.append("*************NEW*SETTINGS*************");
        sb.append(System.getProperty("line.separator"));

        //newline and printing of slot numberings
        //information below that slow belongs to that slot
        sb.append("ROTOR SLOTS(LR):  3   2   1");
        sb.append(System.getProperty("line.separator"));

        //Rotor choices printing and newline
        //get slots from 3 to 1 and print what rotor they are. i.e. 'V'
        sb.append("ROTOR CHOICES(LR): ").append(rotorPos3.getRotor());
        sb.append("  ").append(rotorPos2.getRotor()).append("  ");
        sb.append(rotorPos1.getRotor());
        sb.append(System.getProperty("line.separator"));

        //cipher printing of each rotor and newline at each stage
        //get each slot and print the cipher of the rotor in that slot from slot 3 to 1
        sb.append("CIPHER OF SLOT 3: ").append(rotorPos3.getCipher());
        sb.append(System.getProperty("line.separator"));
        sb.append("CIPHER OF SLOT 2: ").append(rotorPos2.getCipher());
        sb.append(System.getProperty("line.separator"));
        sb.append("CIPHER OF SLOT 1: ").append(rotorPos1.getCipher());
        sb.append(System.getProperty("line.separator"));

        //printing of each rotor in each slot, and its current position and newline
        //was going to add notches which is kind of important but information got very dense
        //added + 65 to get the right lettering and cast to a char variable for printing of that letter
        sb.append("ROTOR POSITIONS(LR): ");
        sb.append((char)(rotorPos3.getCurrentWheelPos() + 65));
        sb.append("  ");
        sb.append((char)(rotorPos2.getCurrentWheelPos() + 65));
        sb.append("  ");
        sb.append((char)(rotorPos1.getCurrentWheelPos() + 65));
        sb.append(System.getProperty("line.separator"));

        //Ring settings for each rotor and newline
        //Very similar to above in output, just different data
        sb.append("ROTOR RING SETTINGS(LR): ");
        sb.append((char)(rotorPos3.getCurrentRingSetting() + 65));
        sb.append("  ");
        sb.append((char)(rotorPos2.getCurrentRingSetting() + 65));
        sb.append("  ");
        sb.append((char)(rotorPos1.getCurrentRingSetting() + 65));
        sb.append(System.getProperty("line.separator"));


        //line for the reflector in use
        //was going to add the cipher for this but decided against it due to information overload
        sb.append("ROTOR REFLECTOR: ");
        if(reflectorCurrent == reflectors[0])//logic for printing which reflector is what
        {
            sb.append("UKW-B");//append this if the current rotor is equal to rotor in array at index 0
        }
        else
        {
            sb.append("UKW-C");//else, append this instead
        }
        sb.append(System.getProperty("line.separator"));//newline


        //plugboard printing in the form like:
        //AG FT KL PO RD EV...
        //dynamically sized due to random amount of these pairs from 0 to 10
        sb.append("PLUGBOARD: ");//append the title
        for(int i = 0; i < key1.size(); i++)//loop throughout key1 which is the same size as key2
        {
            //add letter at key1 and then key2 without space
            //cast to char and added 65 to get right letter
            sb.append((char) (key1.get(i) + 65));
            sb.append((char) (key2.get(i) + 65));
            //append whitespace for gap
            sb.append("  ");
        }
        //print the output to the console for viewing
        System.out.println(sb);
    }


    //private member variables
    private Rotor rotorPos1;//store for the rotor currently in use, right-most slot
    private Rotor rotorPos2;//store for the rotor currently in use, middle slot
    private Rotor rotorPos3;//store for the rotor currently in use, left-most slot

    private Reflector reflectorCurrent;//store for the reflector currently in use

    private final Rotor[] rotors;//array of rotors to choose from
    private final Reflector[] reflectors;//array of reflectors to choose from

    private ArrayList<Integer> formattedSettings;//list of formatted settings for printing
    //list of plugboard settings, index at 0 in key1 maps to key2 and so on. Tried to use a hashmap, but I needed it to be objective where I could look up key from both sides and find output
    private ArrayList<Integer> key1;
    private ArrayList<Integer> key2;

    private String settings;//store for current settings which is easily used for detecting differences in settings from the client controller

}
