package coursework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import model.Individual;
import model.LunarParameters;
import model.NeuralNetwork;
import model.LunarParameters.DataSet;

public class Parameters {
 
	/**
	 * These parameter values can be changed 
	 * You may add other Parameters as required to this class 
	 * 
	 */
	public static int count = 0;
	
	private static int numHidden = 5; // number of hidden neurons per layer (josdyr)
	private static int numGenes = calculateNumGenes(); // default=48 (josdyr)
	public static double minGene = -3; // specifies minimum and maximum weight values 
	public static double maxGene = +3;
		
	public static int popSize = 200; //200???
	public static int maxEvaluations = 1000;
	
	public static double selectionPressure = 0.1; // 10%
	public static int k_amount = (int) Math.floor(selectionPressure * popSize); // defualt=4 (0.1 * 40)
	// public static int tournamentSize = (int) Math.floor(selectionPressure * popSize);
	
	// Parameters for mutation 
	// Rate = probability of changing a gene
	// Change = the maximum +/- adjustment to the gene value
	public static double mutateRate = 0.05; // mutation rate for mutation operator
	public static double mutateChange = 0.05; // delta change for mutation operator
	
	//Random number generator used throughout the application
	public static long seed = System.currentTimeMillis();
	public static Random random = new Random(seed);

	//set the NeuralNetwork class here to use your code from the GUI
	public static Class neuralNetworkClass = ExampleEvolutionaryAlgorithm.class;
	
	/**
	 * Do not change any methods that appear below here.
	 * 
	 */
	
	public static int getNumGenes() {					
		return numGenes;
	}

	
	private static int calculateNumGenes() {
		int num = (NeuralNetwork.numInput * numHidden) + (numHidden * NeuralNetwork.numOutput) + numHidden + NeuralNetwork.numOutput;
		return num;
	}

	public static int getNumHidden() {
		return numHidden;
	}
	
	public static void setHidden(int nHidden) {
		numHidden = nHidden;
		numGenes = calculateNumGenes();		
	}

	public static String printParams() {
		String str = "";
		
		if (Parameters.count == 0) {
			for(Field field : Parameters.class.getDeclaredFields()) {
				String name = field.getName();
				Object val = null;
				try {
					val = field.get(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				str += name + " \t" + val + "\r\n";
				
				if (name != "seed" & name != "random" & name != "neuralNetworkClass" & name != "count") {
					try {
			            // Open given file in append mode. 
			            BufferedWriter out = new BufferedWriter(new FileWriter("out.csv", true));
			            out.write(name.toString());
			            out.write(", ");
			            out.close();
			        } 
			        catch (IOException e) { 
			            System.out.println("exception occoured" + e);
			        }
				}
			}
			Parameters.count++;
			
			try {
	            // Open given file in append mode. 
	            BufferedWriter out = new BufferedWriter(new FileWriter("out.csv", true));
	            out.write("fitness_training");
	            out.write(", ");
	            out.write("fitness_testing");
	            out.write("\n");
	            out.close();
	        } 
	        catch (IOException e) { 
	            System.out.println("exception occoured" + e); 
	        }
		}
		
		for(Field field : Parameters.class.getDeclaredFields()) {
			String name = field.getName();
			Object val = null;
			try {
				val = field.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			str += name + " \t" + val + "\r\n";
			
			if (name != "seed" & name != "random" & name != "neuralNetworkClass" & name != "count") {
				try {
		            // Open given file in append mode. 
		            BufferedWriter out = new BufferedWriter(new FileWriter("out.csv", true));
		            out.write(val.toString());
		            out.write(", ");
		            out.close();
		        } 
		        catch (IOException e) { 
		            System.out.println("exception occoured" + e); 
		        }
			}
		}
		
		return str;
	}
	
	public static void setDataSet(DataSet dataSet) {
		LunarParameters.setDataSet(dataSet);
	}
	
	public static DataSet getDataSet() {
		return LunarParameters.getDataSet();
	}
	
	public static void main(String[] args) {
		printParams();
	}
}
