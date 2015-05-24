import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import de.dfki.ccaal.gestures.Gesture;
import de.dfki.ccaal.gestures.classifier.DTWAlgorithm;
import de.dfki.ccaal.gestures.classifier.featureExtraction.NormedGridExtractor;

//import ca.uwo.csd.ai.nlp.kernel.KernelManager;
//import ca.uwo.csd.ai.nlp.kernel.LinearKernel;

public class CopyOfGests {

	public static void main(String[] args) throws NumberFormatException, IOException {

        
		Vector<Vector<float[]>> G = new Vector<Vector<float[]>>();
		Vector<String>  GL = new Vector<String>();
		Vector<Gesture>  Gests = new Vector<Gesture>();
		
//		String[] names = {"circle1","circle2","circle3", "fireball1", "fireball2", "fireball3", "horizontal_line1", "horizontal_line2", "horizontal_line3", "junk1", "junk2", "junk3"};
		String[] names = readFiles("C:/Users/Olek/workspace/wiggle/tests");
		for (String n : names)
		{
//			System.out.println(n);
			G.add(readG(n));
		}
		for (String n : names)
			GL.add(n);
		
		for (int i=0;i<GL.size();i++)
			Gests.add(new Gesture(G.get(i), GL.get(i)));

//		System.out.print("dupa;");
//		for (int i=0;i<GL.size();i++)
//			System.out.print(GL.get(i) + ";");
//		System.out.print("\n");
//		for (int i=0;i<GL.size();i++)
//		{
//			System.out.print(GL.get(i) + ";");
//			for (int j=0;j<GL.size();j++)
////				System.out.println("From " + GL.get(i) + " to " +  GL.get(j) + " Distance: " + DTWAlgorithm.calcDistance(Gests.get(i), Gests.get(j)));
//				System.out.print(new Integer(Math.round(DTWAlgorithm.calcDistance(Gests.get(i), Gests.get(j)))).toString() + ";");
//			System.out.print("\n");
//		}
		
//		System.out.print("\n\nNormedGridExtractor\n\n");
		
		for (int i=0;i<Gests.size();i++)
			System.out.println(Gests.get(i).length());
		
		NormedGridExtractor ex = new NormedGridExtractor();
		for (int i=0;i<Gests.size();i++)
		{
			Gesture g = ex.sampleSignal(Gests.get(i));
			Gests.set(i, g);
		}
		

//		Vector<Vector<int,int>> = best
		
		System.out.print("dupa;");
		for (int i=0;i<GL.size();i++)
			System.out.print(GL.get(i) + ";");
		System.out.print("\n");
		for (int i=0;i<GL.size();i++)
		{
			System.out.print(GL.get(i) + ";");
			for (int j=0;j<GL.size();j++)
//				System.out.println("From " + GL.get(i) + " to " +  GL.get(j) + " Distance: " + DTWAlgorithm.calcDistance(Gests.get(i), Gests.get(j)));
				if ((DTWAlgorithm.calcDistance(Gests.get(i), Gests.get(j)) + DTWAlgorithm.calcDistance(Gests.get(j), Gests.get(i)))/2 > 3)
					System.out.print(";");
				else
					System.out.print((DTWAlgorithm.calcDistance(Gests.get(i), Gests.get(j)) + DTWAlgorithm.calcDistance(Gests.get(j), Gests.get(i)))/2 + ";");
			System.out.print("\n");
		}
	}

	public static Vector<float[]> readG(String fileName) throws NumberFormatException, IOException
	{
		Vector<float[]> rv = new Vector<float[]>();
		BufferedReader file = new BufferedReader(new FileReader(fileName));
		String s;
		s = file.readLine();
		s = file.readLine();
		String[] sl = s.split(" ");
//		float[] lvec = new float[3];
//		lvec[0] = Float.parseFloat(sl[0]);
//		lvec[1] = Float.parseFloat(sl[1]);
//		lvec[2] = Float.parseFloat(sl[2]);
		
		while ((s = file.readLine()) != null) {
			sl = s.split(" ");
			float[] vec = new float[3];
//			vec[0] = lvec[0]-Float.parseFloat(sl[0]);
//			vec[1] = lvec[1]-Float.parseFloat(sl[1]);
//			vec[2] = lvec[2]-Float.parseFloat(sl[2]);
//			
			vec[0] = Float.parseFloat(sl[0]);
			vec[1] = Float.parseFloat(sl[1]);
			vec[2] = Float.parseFloat(sl[2]);
			
			
			rv.add(vec);
//			lvec = vec;
		}
		file.close();
		return rv;
	}
	
	public static String[] readFiles(String fileName) throws NumberFormatException, IOException
	{
		BufferedReader file = new BufferedReader(new FileReader(fileName));
		String s = file.readLine();
		String[] sl = s.split(" ");
		file.close();
		return sl;
	}
}
