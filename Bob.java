//This code retrieves the keys from Alice, encrypts the message before sending to Alice and also decrypts the cipher text from Alice using RSA algorithm
import java.net.*;  
import java.io.*;  
import java.math.BigInteger;
import java.util.Scanner;
import java.lang.Object;
import java.util.Random;
import java.security.SecureRandom;

public class Bob{
    private String[] message=new String[10000];
    private BigInteger[] message2=new BigInteger[10000];
    
    public static void main(String args[])throws Exception{  
        Bob msg1 = new Bob();
        ServerSocket ss=new ServerSocket(55555);  
        Socket s=ss.accept();  
        DataInputStream din=new DataInputStream(s.getInputStream());  
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
        System.out.println("Connection established with Alice!");
        String str="",str2="";
        str=din.readUTF();
        System.out.println("Alice has sent the public key as: "+str);
        String[] received = str.split(", ");
        BigInteger e = new BigInteger(received[0]);
        BigInteger n = new BigInteger(received[1]);
        BigInteger d = e.modInverse(new BigInteger(received[2]));
        while(!str2.equals("END")){  
            System.out.println("Type in your message to send: ");
            str2=br.readLine();
            for(int i=0; i<str2.length(); i++){
                char character = str2.charAt(i);
                int ascii = Character.hashCode(character);
                msg1.message[i] = String.valueOf(ascii);
                msg1.message2[i]=new BigInteger(msg1.message[i]);
                BigInteger ciphered = msg1.message2[i].modPow(e, n);
                msg1.message2[i] = ciphered;
            }
            String combine="";
            for(int k=0; k<str2.length(); k++){
                combine = combine+(msg1.message2[k].toString())+"N"; 
            }
            dout.writeUTF(combine);
            dout.flush();
            str=din.readUTF();  
            String[] aftersplit = str.split("N");
            String str_after_ascii="";
            for(int i=0; i<aftersplit.length; i++){
                BigInteger bi = new BigInteger(aftersplit[i]);
                char ch = (char) (bi.modPow(d, n)).intValue();
                str_after_ascii = str_after_ascii+ch;
            }
            System.out.println("Encrypting message... "+str);
            System.out.println("Alice says: "+str_after_ascii);  
        } 
        System.out.println("Coversation has ended! ");
        din.close();  
        s.close();  
        ss.close();  
    }
}  