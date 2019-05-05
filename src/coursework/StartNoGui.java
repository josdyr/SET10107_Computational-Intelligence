package coursework;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import model.Fitness;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;
import coursework.Statistics;

/**
 * Example of how to to run the {@link ExampleEvolutionaryAlgorithm} without the need for the GUI
 * This allows you to conduct multiple runs programmatically 
 * The code runs faster when not required to update a user interface
 *
 */
public class StartNoGui {

	public static void main(String[] args) {
		/**
		 * Train the Neural Network using our Evolutionary Algorithm 
		 * 
		 */
		
		final int EPISODES = 10;
		
		double[] train = new double[EPISODES];
		double[] test = new double[EPISODES];
		
		for (int i = 0; i < EPISODES; i++) {
			
			/*
			 * Set the parameters here or directly in the Parameters Class.
			 * Note you should use a maximum of 20,0000 evaluations for your experiments 
			 */
//			Parameters.maxEvaluations = 20000; // Used to terminate the EA after this many generations
//			Parameters.popSize = 200; // Population Size

			//number of hidden nodes in the neural network
			Parameters.setHidden(5);
			
			//Set the data set for training 
			Parameters.setDataSet(DataSet.Training);
			
			//Create a new Neural Network Trainer Using the above parameters 
			NeuralNetwork nn = new ExampleEvolutionaryAlgorithm();
			
			//train the neural net (Go and make a coffee) 
			nn.run();
			
			/* Print out the best weights found
			 * (these will have been saved to disk in the project default directory) 
			 */
			System.out.println("\n---");
			
			System.out.println("Fitness on " + Parameters.getDataSet() + " " + nn.best.fitness);
			train[i] = nn.best.fitness;
			
			double meanTrain = 0;
			double varianceTrain = 0;
			double stdTrain = 0;
			if (i == EPISODES-1) {
				Statistics stat = new Statistics(train);
				meanTrain = stat.getMean();
				varianceTrain = stat.getVariance();
				stdTrain = stat.getStdDev();
				System.out.println("meanTrain: " + meanTrain + " varianceTrain: " + varianceTrain + " stdTrain: " + stdTrain);
			}
			
			try {
	            // Open given file in append mode. 
	            BufferedWriter out = new BufferedWriter(new FileWriter(Parameters.fileName, true));
	            out.write(String.valueOf(Math.floor(nn.best.fitness * 10000) / 10000).toString() + ", ");
	            out.write(String.valueOf(Math.floor(meanTrain * 10000) / 10000).toString() + ", ");
	            out.write(String.valueOf(Math.floor(varianceTrain * 10000) / 10000).toString() + ", ");
	            out.write(String.valueOf(Math.floor(stdTrain * 10000) / 10000).toString() + ", ");
	            out.close();
	        }
	        catch (IOException e) {
	            System.out.println("exception occoured" + e);
	        }
			
			/**k
			 * We now need to test the trained network on the unseen test Set
			 */
			Parameters.setDataSet(DataSet.Test);
			test[i] = Fitness.evaluate(nn);
			System.out.println("Fitness on " + Parameters.getDataSet() + " " + test[i]);
			
			double meanTest = 0;
			double varianceTest = 0;
			double stdTest = 0;
			if (i == EPISODES-1) {
				Statistics stat = new Statistics(test);
				meanTest = stat.getMean();
				varianceTest = stat.getVariance();
				stdTest = stat.getStdDev();
				System.out.println("meanTest: " + meanTest + " varianceTest: " + varianceTest + " stdTest: " + stdTest);
			}
			
			try {
	            // Open given file in append mode. 
	            BufferedWriter out = new BufferedWriter(new FileWriter(Parameters.fileName, true));
	            out.write(String.valueOf(Math.floor(test[i] * 10000) / 10000).toString() + ", ");
	            out.write(String.valueOf(Math.floor(meanTest * 10000) / 10000).toString() + ", ");
	            out.write(String.valueOf(Math.floor(varianceTest * 10000) / 10000).toString() + ", ");
	            out.write(String.valueOf(Math.floor(stdTest * 10000) / 10000).toString() + ", ");
	            out.write(String.valueOf("\n").toString());
	            out.close();
	        } 
	        catch (IOException e) { 
	            System.out.println("exception occoured" + e); 
	        }
			System.out.println("---\n");
		}
		
		
		/**
		 * Or We can reload the NN from the file generated during training and test it on a data set 
		 * We can supply a filename or null to open a file dialog 
		 * Note that files must be in the project root and must be named *-n.txt
		 * where "n" is the number of hidden nodes
		 * ie  1518461386696-5.txt was saved at timestamp 1518461386696 and has 5 hidden nodes
		 * Files are saved automatically at the end of training
		 *  
		 *  Uncomment the following code and replace the name of the saved file to test a previously trained network 
		 */
		
//		NeuralNetwork nn2 = NeuralNetwork.loadNeuralNetwork("1234567890123-5.txt");
//		Parameters.setDataSet(DataSet.Random);
//		double fitness2 = Fitness.evaluate(nn2);
//		System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness2);
		
		
		
	}
}
