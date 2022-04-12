//Name: Jack Kelvin
//Student Number: z5413924
//Course: COMP6841


//simple class for reflector
//I didn't need to make this, but I think it is a good idea for the future where I might add more functionality
//Might be useful with more reflectors in different types of Enigma machines like the naval one


public class Reflector
{
    //constructor
    public Reflector(String cipher)//given string 'cipher' which contains the match for each letter. Index 0 means A, which has a mapping to this cipher which means the letter stored at index 0.
    {
        this.cipher = cipher;//save cipher locally
    }

    public int encrypt(int character)//encrypt a letter, given as a char variable, and then return it to its match in the cipher. Minus 65 is used to find the actual integer from 0 to 26 which corresponds to letter order in alphabet
    {
        return (cipher.charAt(character) - 65);
    }

    //private member data
    //Cipher variable
    private final String cipher;
}
