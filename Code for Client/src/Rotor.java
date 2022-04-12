//Name: Jack Kelvin
//Student Number: z5413924
//Course: COMP6841


import java.util.ArrayList;

public class Rotor
{

    //class contains functions for rotors and their encryption mechanisms

    //constructor
    public Rotor(int notch, String cipherEquivalence, int value)//given as input the notch location, cipher as a string and int for what rotor this particular one is which is used later for printing
    {
        this.value = value;//save value to private member variable
        this.notch = notch;//save notch to private member variable
        cipherMatch = new ArrayList<>();//new Arraylist for storing cipher to mutate it with the ring settings. More later in the class
        currentWheelPos = 0;//current rotor wheel setting, starting at 0 or rather 'A'
        cipher = cipherEquivalence;//save cipher to a final variable
        init_rotor();//initialize this rotor
    }

    //function for initializing this rotor
    public void init_rotor()
    {
        //loop from 0 to cipher length
        for(int i = 0; i < cipher.length(); i++)
        {
            int letterNumeric = cipher.charAt(i) - 65;//get letter by subtracting 65 and saving the char at index with the math to the variable 'letterNumeric'
            cipherMatch.add(i, letterNumeric);//add this letter in numeric form to the index at i
        }
    }

    //change wheel position, increment the position of the wheel by 1 by looping and calling the incrementPos() function by as many times as the given input
    public void changeWheelPosSetting(int value)//times to increment wheel by given as input
    {
        currentWheelPos = 0;//current value
        for(int i = 0; i < value; i++)//loop bounded by the value variable
        {
            incrementPos();//increment function
        }
    }

    //change ring setting function
    //awesome function
    //ring basically grabs the list of integers which represent letters in numeric form and increments each number,
    //and then it adds the value at the end of the list to the beginning!
    //it will do this as many times as the given input value
    //A ring setting of J means do the above operation 10 times
    //Also makes it harder because if a letter is say 25, then adding one will give 26 which is not a letter in this case
    //using mod fixes this problem and allows for the letter to roll back to A, thus making this a circular alphabet
    //this is why it is much easier to just reload the settings in the Enigma machine instead of resetting each of the parameters
    public void changeRingSetting(int value)//integer given as input
    {
        currentRingSetting = value;//save that value locally
        init_rotor();//reset the rotor arraylist for safety
        if(value < 26)//check if value is less than 26 for safety
        {
            for(int i = 0; i < value; i++)//loop from 0 to value
            {
                for (int j = 0; j < 26; j++)//loop from 0 to 26 for each letter in arraylist
                {
                    cipherMatch.set(j, (cipherMatch.get(j) + 1) % 26);//increment each letter by 1 and apply mod 26 to catch any strays that go past 26 in value, thus resetting them back to A which is how Enigma works
                }
                //removes the 25th index of the list which is the last
                int endPoint = cipherMatch.remove(25);
                //and then places it at the beginning of the list!
                cipherMatch.add(0, endPoint);
            }
        }
    }

    //function to encrypt right to left with a given character as an integer
    public int encryptRightToLeft(int characterInNumericalForm)
    {
        //passes to 2 functions, middle function is essentially the main function
        //calcInput and Output is for calculating the affect of the wheels being rotated which affects what it 'enters' the wheel at
        characterInNumericalForm = calcInput(characterInNumericalForm);//give character and reassign the output of this function
        characterInNumericalForm = cipherMatch.get(characterInNumericalForm);//use the character in question in the list of the cipher text as an index and return the value stored at that index
        characterInNumericalForm = calcOutput(characterInNumericalForm);//give character and reassign the output of this function
        //return the result of this to the caller of the function
        return characterInNumericalForm;
    }

    //similar to above with the calcInput functions but uses a different way to match the cipher text
    public int encryptLeftToRight(int characterInNumericalForm)
    {
        characterInNumericalForm = calcInput(characterInNumericalForm);//give character and reassign the output of this function
        //for this time we will use the given character as the character we need to find in the array and then return the index
        //very cool!
        for(int i = 0; i < 26; i++)//loop through arraylist with index i
        {
            if(cipherMatch.get(i) == characterInNumericalForm)//if a match is found where the character inputted is matched with a value at index i
            {
                //use the character at i as the input for calcOutput and then return value
                return calcOutput(i);
            }
        }
        //error when failure
        return -1;
    }


    //calc input function
    //essentially calculates the addition of the letter in question with the current wheel setting
    //then we get mod 26 of that sum and return it
    //formula is: p + k % 26 ,where p = character and k = wheel position
    public int calcInput(int numericalValueOfLetter)
    {
        return (numericalValueOfLetter + currentWheelPos) % 26;
    }


    //calc output function
    //essentially calculates letter in question after the subtraction of the current wheel setting
    //then we get mod 26 of that sum and return it
    //formula is: p - k % 26 ,where p = character and k = wheel position
    //had to do some research on how to get values with the mod function to work where they are negative
    public int calcOutput(int numericalValueOfLetter)
    {
        int value = numericalValueOfLetter - currentWheelPos;//simple subtraction
        while (value < 0)//loop until greater than or equal to 0, could have used a single instruction here but for safety, I kept the loop
        {
            value += 26;//add 26
        }
        return value % 26;//return that value mod 26
    }

    //simple check if the current wheel position is equal to the notch for this rotor
    public boolean canMoveLeft()
    {
        return currentWheelPos == notch;
    }

    //increment pos function that has carry over
    //say z gets incremented, then it will carry over and go back to a, or in this case '0'
    public void incrementPos()
    {
        currentWheelPos = currentWheelPos + 1 % 26;//mod for carry over
    }

    //return the wheel setting stored locally to the caller as an integer
    public int getCurrentWheelPos()
    {
        return currentWheelPos;
    }
    //return the wheel ring setting stored locally to the caller as an integer
    public int getCurrentRingSetting()
    {
        return currentRingSetting;
    }

    //returns the cipher that is in use by the current wheel as a string
    public String getCipher()
    {
        return cipher;
    }

    //get rotor function that returns a string of what rotor this is
    public String getRotor()
    {
        StringBuilder sb = new StringBuilder();//new string builder
        if(value < 3)//check if the locally stored value is less than 3
        {
            sb.append("I".repeat(Math.max(0, value + 1)));//append to string builder the letter "I" as many times as the value
        }
        else if(value == 3)//else, if 3 then return "IV" as we are starting at 0
        {
            sb.append("IV");
        }
        else//else append "V"
        {
            sb.append("V");
        }
        return sb.toString();//return the string builder as a string
    }


    //private member data
    //ring settings and positions
    private int currentWheelPos;//store of the current wheel setting as an integer
    private int currentRingSetting;//store of the current wheel ring setting as an integer
    private final int value;//store of the current wheel's value to determine the wheel number in Roman Numerals
    private final int notch;//store of the current wheel's notch as an integer
    private final String cipher;//store of the current wheel's cipher as a string
    private final ArrayList<Integer> cipherMatch;//store of the cipher above but in an Arraylist for easier manipulation of data for the above methods

}
