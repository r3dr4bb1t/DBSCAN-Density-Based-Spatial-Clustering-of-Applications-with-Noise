
package DBSCAN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dbscan {
	
	// Input file number
	private static String v;
	
	private static double[][]table;

	// Number of Index
	private static int n = 0;

	// Number of Cluster
	private static int C = 0;

	// Max Cluster
	private static int N;

	public static void main(String[] args) {

		N = Integer.parseInt(args[1]);
		int Eps = Integer.parseInt(args[2]);
		int MinPts = Integer.parseInt(args[3]);

		
		String input = args[0];

		//Find input file number
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(input);
		while (m.find()) {
		  v= m.group();
		}

		// get points
		double[][] table = getInput(input);

		// DBSCAN
		int[] label = scan(table, Eps, MinPts);

		getOutput(label);

	}

	private static void getOutput(int[] label) {
	
		
		
		String[][] s = new String[C][2];
		int m = 0;
		for(int i = 0;i<label.length;i++)
		{
			// Ignore outlier
			if (label[i]==-1)
				continue;
			s[label[i]-1][0] += 1+" ";
			s[label[i]-1][1] += " "+i;
		}
		
		//Find small clusters
		int []t = new int[C];
		int []original = new int [C];
		for (int i = 0; i<C;i++)
		{	
			String[]count = s[i][0].split(" ");
			for(int k = 1;k<count.length;k++)
				t[i]+=Integer.parseInt(count[k]);
			original[i]=t[i];
		}
		
		Arrays.sort(t);
		int stopvalue =t[C-N];


		int newidx=0;
		for (int j = 0; j< C;j++)
		{
			if(original[j]<stopvalue)
				continue;
				
			String [] str = s[j][1].split(" ");
			File outfile = new File("input"+v+"_cluster_"+newidx+".txt");
			try {
			BufferedWriter b = new BufferedWriter(new FileWriter(outfile));
			for ( int k = 1;k<str.length;k++)
			{
				b.write(str[k]);
				b.newLine();
			}
			b.close();
			}
			catch(IOException e)
			{
				System.out.println("File error");
			}
			newidx++;
		}
		

	}

	static double[][] getInput(String fileName) {
		
		BufferedReader linecount=null;
		try {
		linecount = new BufferedReader(new FileReader(fileName));
		String sl;
		while((sl=linecount.readLine())!=null)
			n++;
		linecount.close();
		}
		catch (IOException e )
		{System.out.println("no file");}
		
		table = new double[n][2];

		BufferedReader inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader(fileName));
			String s;
			int i=0;
			while ((s = inputStream.readLine()) != null)
			{
				String[] arrayOfString = s.split("\\s+");
				table[i][0]=Double.parseDouble(arrayOfString[1]);
				table[i][1]=Double.parseDouble(arrayOfString[2]);

		i++;
			}
		} catch (IOException e) {
			System.err.println("no input");
			System.exit(1);
		}
		
		
		return table;
	}

	private static int[] scan(double[][] table, int eps, int minPts) {

		int label[] = new int[n];
		for (int point = 0; point < n; point++) {
			if (label[point] != 0)
				continue;

			// Find neighbors
			ArrayList<Integer> neighbors = RangeQuery(table, point, eps);
			
			// Density Check
			if (neighbors.size() < minPts) {
				
				// Label as Noise
				label[point] = -1;
				continue;
			}

			// Dense enough
			C++;

			// Label initial point
			label[point] = C;

			// Neighbors to expand
			ArrayList<Integer> seed = new ArrayList<Integer>();
			seed.addAll(neighbors);

			// Process every seed points
			for (int i = 0; i < seed.size(); i++) {
				int Q = seed.get(i);
				
				// Change Noise to border point
				if (label[Q] == -1) {
					label[Q] = C;
				}

				// Previously processed
				if (label[Q] != 0)
					continue;

				// Label neighbor
				label[Q] = C;

				// Find Density reachable neighbors
				neighbors = RangeQuery(table, Q, eps);

				if (neighbors.size() >= minPts)
					seed.addAll(neighbors);

			}
			
		}
		return label;
	}

	public static ArrayList<Integer> RangeQuery(double[][] table, int Q, int eps) {
		ArrayList<Integer> neighbors = new ArrayList<Integer>();

		// Scan all points in the database
		for (int i = 0; i < n; i++) {
			double[] p = table[i];
		
		
			// Compute distance and check epsilon
			if (distFunc(p, table[Q]) <= eps && i != Q) {
			
				// Add to neighbor
				neighbors.add(i);
			}
	
		}
	
		return neighbors;
	}

	public static double distFunc(double[] x, double[] y) {
		double dist = 0d;
		for (int i = 0; i < x.length; i++) {
			dist += (x[i] - y[i]) * (x[i] - y[i]);
		}
		return Math.sqrt(dist);
	}
}
