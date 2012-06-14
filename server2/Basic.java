import java.net.*;
import java.io.*;
import nomads.v200.*;

public class Basic implements Runnable
{
    public Basic()
    {  
    }

    public void run()
    {  
	byte a,b,c,d,e,f;
	System.out.println("RUNNING!");

	d=e=f=0;
	a=1;
	b=2;
	c=3;
	
	d = combine(a,a);
	e = combine(b,b);
	f = combine(c,c);

	a = unpackL(d);
	a = unpackR(d);
	b = unpackL(e);
	b = unpackR(e);
	c = unpackL(f);
	c = unpackR(f);

	d = combine(a,b);
	a = unpackL(d);
	b = unpackR(d);
	
    }
    
    private byte combine(byte a, byte b) {
	    
	byte mask = 127;
	byte na = (byte)((a & mask) + 128);
	byte mask = 127;
	byte nb = (byte)(b & mask);
	byte rByte = (byte)(a+b);
	System.out.println("combine(" + a + "," + b + ") = " + rByte);
	return rByte;
    }
    
    private byte unpackL(byte a) {
	byte rb = (byte)(a>>4);
	System.out.println("unpackL(" + a + ") = " + rb);
	return rb;
    }

    private byte unpackR(byte a) {
	byte mask = 15;
	byte rb = (byte)(a & mask);
	System.out.println("unpackR(" + a + ") = " + rb);
	return rb;
    }

    public static void main(String args[])
    {  Basic client = null;
	if (args.length > 100)
	    System.out.println("Usage: java Basic port");
	else
	    client = new Basic();
	client.run();
	
    }
}
