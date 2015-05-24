import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.dfki.ccaal.gestures.Gesture;
import de.dfki.ccaal.gestures.classifier.DTWAlgorithm;
import de.dfki.ccaal.gestures.classifier.featureExtraction.NormedGridExtractor;

//import ca.uwo.csd.ai.nlp.kernel.KernelManager;
//import ca.uwo.csd.ai.nlp.kernel.LinearKernel;

public class Gests {

	Vector<Vector<float[]>> G = new Vector<Vector<float[]>>();
	Vector<String>  GL = new Vector<String>();
	Vector<Gesture>  Gests = new Vector<Gesture>();
	
	NormedGridExtractor ex; 
	
	public Gests() throws NumberFormatException, IOException {

		
		String[] names = readFiles("C:/Users/Olek/workspace/wiggle/tests");
		for (String n : names)
		{
			G.add(readG(n));
		}
		for (String n : names)
		{
//			if(n.contains("alttab"))
//				GL.add("1");
//			if(n.contains("back"))
//				GL.add("2");
//			if(n.contains("play"))
//				GL.add("3");
			if(n.contains("multi"))
				GL.add("4");
		}
		for (int i=0;i<GL.size();i++)
			Gests.add(new Gesture(G.get(i), GL.get(i)));

//		for (int i=0;i<Gests.size();i++)
//			System.out.println(Gests.get(i).length());
		
		ex = new NormedGridExtractor();
		for (int i=0;i<Gests.size();i++)
		{
			Gesture g = ex.sampleSignal(Gests.get(i));
			Gests.set(i, g);
		}
		
//
//		System.out.print("dupa;");
//		for (int i=0;i<GL.size();i++)
//			System.out.print(GL.get(i) + ";");
//		System.out.print("\n");
//		for (int i=0;i<GL.size();i++)
//		{
//			System.out.print(GL.get(i) + ";");
//			for (int j=0;j<GL.size();j++)
//				if ((DTWAlgorithm.calcDistance(Gests.get(i), Gests.get(j)) + DTWAlgorithm.calcDistance(Gests.get(j), Gests.get(i)))/2 > 3)
//					
//					System.out.print(";");
//				else
//					System.out.print((DTWAlgorithm.calcDistance(Gests.get(i), Gests.get(j)) + DTWAlgorithm.calcDistance(Gests.get(j), Gests.get(i)))/2 + ";");
//			System.out.print("\n");
//		}
	}
	
	public int check(Vector<float[]> f){
		Gesture g = new Gesture(f, "-1");
		g = ex.sampleSignal(g);
		
		Map<Integer,Integer> dic = new HashMap<Integer,Integer>();
		for(int i = 1; i <= 4; i++)
			dic.put(i, 0);

//		toDelete = 10
		for (int i=0;i<Gests.size();i++)
		{
			Gesture gt = Gests.get(i);
			
			if ((DTWAlgorithm.calcDistance(gt, g) + DTWAlgorithm.calcDistance(g, gt))/2 < 1.2)
			{
				System.out.println((DTWAlgorithm.calcDistance(gt, g) + DTWAlgorithm.calcDistance(g, gt))/2);
				int c = Integer.parseInt(gt.getLabel());
//				if(dic.containsKey(c))
				dic.put(c, dic.get(c) + 1);
//				else
					
			}		
		}
		int change = -1;
		int best = 1;
		for(int i = 1; i <= 4; i++)
			if(dic.get(i) > dic.get(best))
			{
				change = 1;
				best = i;
			}
		if(change != -1)
			return best;
		else
			return 0;
	}
	

	public static Vector<float[]> readG(String fileName) throws NumberFormatException, IOException
	{
		Vector<float[]> rv = new Vector<float[]>();
		BufferedReader file = new BufferedReader(new FileReader(fileName));
		String s;
		s = file.readLine();
		s = file.readLine();
		String[] sl = s.split(" ");
		
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
