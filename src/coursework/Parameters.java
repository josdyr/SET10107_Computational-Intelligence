package coursework;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;

import model.LunarParameters;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

public class Parameters {
 
	/**
	 * These parameter values can be changed 
	 * You may add other Parameters as required to this class 
	 * 
	 */
	public static int count = 0;
	public static String fileName = "best_relu.csv";
	
	private static int numHidden = 5; // number of hidden neurons per layer (josdyr) 3 5 8 15 30
	private static int numGenes = calculateNumGenes(); // default=48 (josdyr)
	public static double minGene = -3; // specifies minimum and maximum weight values 1 3 8 20
	public static double maxGene = +3;
		
	public static int popSize = 100; // 20, 50, 200, 400, 1000
	public static int maxEvaluations = 20000;
	
	public static double selectionPressure = 0.02; // 10%
	public static int kAmount = (int) Math.floor(selectionPressure * popSize); // defualt=20 (0.1 * 200)
	// public static int tournamentSize = (int) Math.floor(selectionPressure * popSize);
	
	// Parameters for mutation 
	// Rate = probability of changing a gene
	// Change = the maximum +/- adjustment to the gene value
	public static double mutateRate = 0.01; // mutation rate for mutation operator
	public static double mutateChange = 0.05; // delta change for mutation operator
	
	public static int crossMulti = 4; // by default: 4 alternating swapping segments
	public static int scrambleCount = 5; // iterations to scramble the tmpList
	
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
				
				if (name != "seed" & name != "random" & name != "neuralNetworkClass" & name != "count" & name != "fileName") {
					try {
			            // Open given file in append mode. 
			            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
			            String currentOutValue = "\\rot{" + name.toString() + "}" + ", ";
			            out.write(currentOutValue);
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
	            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
//	            out.write("\\rot{" + "train" + "}" + ", ");
	            out.write("\\rot{" + "avgTrain" + "}" + ", ");
	            out.write("\\rot{" + "varTrain" + "}" + ", ");
	            out.write("\\rot{" + "stdTrain" + "}" + ", ");
//	            out.write("\\rot{" + "test" + "}" + ", ");
	            out.write("\\rot{" + "avgTest" + "}" + ", ");
	            out.write("\\rot{" + "varTest" + "}" + ", ");
	            out.write("\\rot{" + "stdTest" + "}" + "\n");
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
			
			if (name != "seed" & name != "random" & name != "neuralNetworkClass" & name != "count" & name != "fileName") {
				try {
		            // Open given file in append mode. 
		            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
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
