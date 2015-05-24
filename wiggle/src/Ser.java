import java.net.*; 
import java.util.Vector;
import java.io.*; 

import de.dfki.ccaal.gestures.Gesture;

public class Ser extends Thread
{ 
 protected Socket clientSocket;

 static Vector<float[]> Cache = new Vector<float[]>();
 static Gests gests = null;
 
 static int last = 0; 
 static int after = 0; 
 
 public static void main(String[] args) throws IOException 
   { 
    ServerSocket serverSocket = null; 
    gests = new Gests();
    try { 
    	InetAddress addr = InetAddress.getByName("192.168.43.98");
         serverSocket = new ServerSocket(); 
         serverSocket.bind(new InetSocketAddress("0.0.0.0", 10008));
         System.out.println ("Connection Socket Created");
         try { 
              while (true)
                 {
                  System.out.println ("Waiting for Connection");
                  new Ser (serverSocket.accept()); 
                 }
             } 
         catch (IOException e) 
             { 
              System.err.println("Accept failed."); 
              System.exit(1); 
             } 
        } 
    catch (IOException e) 
        { 
         System.err.println("Could not listen on port: 10008."); 
         System.exit(1); 
        } 
    finally
        {
         try {
              serverSocket.close(); 
             }
         catch (IOException e)
             { 
              System.err.println("Could not close port: 10008."); 
              System.exit(1); 
             } 
        }
   }

 private Ser (Socket clientSoc)
   {
	 System.out.println("test");    
	 clientSocket = clientSoc;
    start();
   }

 public void run()
   {
    System.out.println ("New Communication Thread Started");

    try { 
         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), 
                                      true); 
         BufferedReader in = new BufferedReader( 
                 new InputStreamReader( clientSocket.getInputStream())); 

         String inputLine; 

         while ((inputLine = in.readLine()) != null) 
             { 
        	String[] sl = inputLine.split(";");
        	float x = Float.parseFloat(sl[0]);
        	float y = Float.parseFloat(sl[1]);
        	float z = Float.parseFloat(sl[2]);
        	Integer r = Integer.parseInt(sl[3]);
        	
        	float[] f = new float[] {x,y,z};
			Ser.Cache.add(f);
        	
        	if (Ser.Cache.size() >= 40)
        	{
    			int w = Ser.gests.check(Ser.Cache);
//    			System.out.print(w);
//    			System.out.flush();
    			if(w != 0)
    			{
    				Ser.last = w;
    				Ser.after = 0;
    				System.out.print("Mamy " + w + "\n");
    			}
    			else if(w == 0)
    			{
    				Ser.after += 1;
    			}
    			Ser.after += 1;
    			if (Ser.after >= 100 && Ser.last != 0)
    				Ser.last = 0;
//    				System.out.print("NIE Mamy " + w + "\n");
    			Ser.Cache.remove(1);
    		}

        	
//        	System.out.print("\n ");
        	        	
        	
//        	if (Ser.Cache.size() >= 80)
//        	{
//        		System.out.println(Ser.gests.check(Ser.Cache));
//        		Ser.Cache.clear();
//        	}
//            System.out.println(x +" "+ y +" "+ z +" "+ r); 
        	if(r == 1)
              out.println(Ser.last);  
             } 

         out.close(); 
         in.close(); 
         clientSocket.close(); 
        } 
    catch (IOException e) 
        { 
         System.err.println("Problem with Communication Server");
         System.exit(1); 
        } 
    }
} 