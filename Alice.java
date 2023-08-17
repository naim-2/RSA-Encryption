//This file generates the keys, decrypts cipher text from Bob and also encrypts messages before sending them to Bob using RSA algorithm
import java.net.*;  
import java.io.*; 
import java.math.BigInteger;
import java.util.Scanner;
import java.lang.Object;
import java.util.Random;
import java.security.SecureRandom;
public class Alice{  
    private String[] message=new String[10000];
    private BigInteger[] message2=new BigInteger[10000];
    private String[] generateValues()
    {
        int bitLength = 512;
        int certainty = 50;
        SecureRandom rndm = new SecureRandom();
        BigInteger p = new BigInteger(bitLength, certainty, rndm);
        BigInteger q = new BigInteger(bitLength, certainty,rndm);
        BigInteger n= p.multiply(q);
        BigInteger one= new BigInteger("1");
        BigInteger etf = p.subtract(one).multiply(q.subtract(one));
        BigInteger e;
        do e = new BigInteger(etf.bitLength(), rndm);
        while (e.compareTo(BigInteger.ONE) <= 0
                || e.compareTo(etf) >= 0
                || !e.gcd(etf).equals(BigInteger.ONE));
        String[] values = {n.toString(), etf.toString(), e.toString()}; 
        return values;
    }
    public static void main(String args[])throws Exception{  
        Alice m1 = new Alice();
        String[] m2 = m1.generateValues();
        BigInteger n = new BigInteger(m2[0]);
        BigInteger etf = new BigInteger(m2[1]);
        BigInteger e = new BigInteger(m2[2]);
        BigInteger d = e.modInverse(etf);
        Socket s1 = new Socket("localhost",55555);  
        DataInputStream din=new DataInputStream(s1.getInputStream());  
        DataOutputStream dout=new DataOutputStream(s1.getOutputStream());
        BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));  
        System.out.println("Connection established with Bob! Sending Bob the Public Key");
        String str="",str2="";  
        dout.writeUTF(e.toString()+", "+n.toString()+", "+etf.toString()); 
        dout.flush();
        while(!str.equals("END")){    
            str2=din.readUTF();  
            String[] aftersplit = str2.split("N");
            String str2_after_ascii="";
            for(int i=0; i<aftersplit.length; i++){
                BigInteger bi = new BigInteger(aftersplit[i]);
                char ch = (char) (bi.modPow(d, n)).intValue();
                str2_after_ascii = str2_after_ascii+ch;
            }
            System.out.println("Encrypting message... "+str2);
            System.out.println("Bob says: "+str2_after_ascii);  
            System.out.println("Type in your message to send or type END to quit...");
            str=br1.readLine();
            for(int i=0; i<str.length(); i++){
                char character = str.charAt(i);
                int ascii = Character.hashCode(character);
                m1.message[i] = String.valueOf(ascii);
                m1.message2[i]=new BigInteger(m1.message[i]);
                BigInteger ciphered = m1.message2[i].modPow(e, n);
                m1.message2[i] = ciphered;
            }
            String combine="";
            for(int k=0; k<str.length(); k++){
                combine = combine+(m1.message2[k].toString())+"N"; 
            }
            dout.writeUTF(combine);
            dout.flush();
        }  
        dout.close();  
        s1.close();  
    }
}  
