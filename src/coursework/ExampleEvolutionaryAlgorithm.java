package coursework;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Collections;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 *
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork}
 *
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {


	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */

		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want
			 * You must set the best Individual at the end of a run
			 *
			 */
//			System.out.println(Parameters.getNumHidden());
//			Parameters.setHidden(5);
//			System.out.println(Parameters.getNumHidden());
			

			// Select 2 Individuals from the current population. Currently returns random Individual
//			Individual father = select();
//			Individual mother = select();
			
			Individual father = selectTournament();
			Individual mother = selectTournament();

//			Individual father = selectRoulette();
//			Individual mother = selectRoulette();

//			ArrayList<Individual> parents = selectStochastic();
//			Individual father = parents.get(0);
//			Individual mother = parents.get(1);
			
//			Individual father = selectRandom();
//			Individual mother = selectRandom();
			
			
			

			// Generate a child by crossover. Not Implemented
//			ArrayList<Individual> children = reproduce(father, mother);
//			ArrayList<Individual> children = reproduceCrossOne(father, mother);
			ArrayList<Individual> children = reproduceCrossUni(father, mother);
//			ArrayList<Individual> children = reproduceCrossMulti(father, mother);
			
			
			

			//mutate the offspring
//			mutate(children);
//			mutateSwap(children);
			mutateScramble(children);
//			mutateInverse(children);

			// Evaluate the children
			evaluateIndividuals(children);

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();

			// Implemented in NN class.
			outputStats();
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}



	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 *
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 *
	 */
	private Individual getBest() {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 *
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	/**
	 * Selection --
	 *
	 * NEEDS REPLACED with proper selection this just returns a copy of a random
	 * member of the population
	 */
	private Individual select() {
		Individual parent = population.get(Parameters.random.nextInt(Parameters.popSize));
		return parent.copy();
	}
	
	private Individual selectTournament() {
		
		// select kIndividuals completely randomly from the population
		ArrayList<Individual> kIndividuals = new ArrayList<>();
		for (int i = 0; i < Parameters.kAmount; i++) {
			kIndividuals.add(population.get(Parameters.random.nextInt(Parameters.popSize)));
		}
		
		// select the one most fit individual from the kIndividuals
		Individual bestTournamentMember = kIndividuals.get(0);
		for (int i = 0; i < Parameters.kAmount; i++) {
			if (bestTournamentMember.fitness > kIndividuals.get(i).fitness) {
				bestTournamentMember = kIndividuals.get(i);
			}
		}
		
		// repeat until desiered number of individuals are desired
		return bestTournamentMember;
	}
	
	private Individual selectRoulette() {
		
		// sum up all the fitnesses from the population
		double fitnessSum = 0;
		for (int i = 0; i < Parameters.popSize; i++) {
			fitnessSum += population.get(i).fitness;
		}
		
		// make a random point between 0 and the fitness sum
		double randomNumber = Math.random() * fitnessSum;
		
		// iterate through the population again, until we reach the randomNumber
		double partialSum = 0;
		for (int i = 0; i < Parameters.popSize; i++) {
			partialSum += population.get(i).fitness;
			if (partialSum >= randomNumber) {
				return population.get(i); // return the individual at current index
			}
		}
		return null; // return null should never happen
		
	}
	
	private ArrayList<Individual> selectStochastic() {
		
		ArrayList<Individual> parents = new ArrayList<>();
		Individual father = null;
		Individual mother = null;
		
		// sum up all the fitnesses from the population
		double fitnessSum = 0;
		for (int i = 0; i < Parameters.popSize; i++) {
			fitnessSum += population.get(i).fitness;
		}
		
		// make two random points between 0 and the fitness sum
		double randomOne = Math.random() * fitnessSum;
		double randomTwo = Math.random() * fitnessSum;
		
		// iterate through the population again, until we reach the randomNumber
		double partialSumOne = 0;
		double partialSumTwo = 0;
		for (int i = 0; i < Parameters.popSize; i++) {
			partialSumOne += population.get(i).fitness;
			if (partialSumOne >= randomOne) {
				father = population.get(i);
				if (i == 0) {
					mother = population.get(i+1);
					break;
				} else {
					mother = population.get(i-1);
					break;
				}
			}
			partialSumTwo += population.get(i).fitness;
			if (partialSumTwo >= randomTwo) {
				father = population.get(i);
				if (i == 0) {
					mother = population.get(i+1);
					break;
				} else {
					mother = population.get(i-1);
					break;
				}
			}
		}
		
		//append the parents to the list
		parents.add(father);
		parents.add(mother);
		
		//return list of both parents
		return parents;
		
	}
	
	private Individual selectRandom() {
		
		//make a random int between 0 and the population-size-1
		int randomIndividual = Parameters.random.nextInt(population.size());
		
		//return the individual in that position
		return population.get(randomIndividual);
		
	}

	/**
	 * Crossover / Reproduction
	 *
	 * NEEDS REPLACED with proper method this code just returns exact copies of the
	 * parents.
	 */
	private ArrayList<Individual> reproduce(Individual father, Individual mother) {
		ArrayList<Individual> children = new ArrayList<>();
		children.add(father.copy());
		children.add(mother.copy());
		return children;
	}

	public ArrayList<Individual> reproduceCrossOne(Individual father, Individual mother) {
		Individual childOne = new Individual();
		Individual childTwo = new Individual();
		double crossPoint = Math.random() * father.chromosome.length; // make a crossover point

		for (int i = 0; i < father.chromosome.length; ++i) {
			if (i < crossPoint) {
				childOne.chromosome[i] = father.chromosome[i];
				childTwo.chromosome[i] = mother.chromosome[i];
			}
			else {
				childOne.chromosome[i] = mother.chromosome[i];
				childTwo.chromosome[i] = father.chromosome[i];
			}
		}
		ArrayList<Individual> children = new ArrayList<>();
		children.add(childOne);
		children.add(childTwo);
		return children;
	}
	
	public ArrayList<Individual> reproduceCrossUni(Individual father, Individual mother) {
		// uniform crossover: combining multiple (default two) candidate solutions to get new solutions (default two)
		// returns two sets of genes (chromosomes), both inherited from its parents
		
		Individual childOne = new Individual();
		Individual childTwo = new Individual();

		for (int i = 0; i < father.chromosome.length; ++i) {
			if (Parameters.random.nextBoolean()) { // chance is 50-50 from either parent
				childOne.chromosome[i] = father.chromosome[i];
				childTwo.chromosome[i] = mother.chromosome[i];
			}
			else {
				childOne.chromosome[i] = mother.chromosome[i];
				childTwo.chromosome[i] = father.chromosome[i];
			}
		}
		ArrayList<Individual> children = new ArrayList<>();
		children.add(childOne);
		children.add(childTwo);
		return children;
	}
	
	public ArrayList<Individual> reproduceCrossMulti(Individual father, Individual mother) {
		Individual childOne = new Individual();
		Individual childTwo = new Individual();
		int crossMulti = Parameters.crossMulti;
		
		// make crossMulti-1 random points, and append them to list
		ArrayList<Integer> crossPoints = new ArrayList<>();
		for (int i = 0; i < crossMulti-1; i++) {
			// append random numbers to list (from 0 to length of chromosome)
			crossPoints.add(Parameters.random.nextInt(father.chromosome.length-1));
		}

		// sort list
		Collections.sort(crossPoints);
		
		// append an infinitly high number to crossPoints
		crossPoints.add(father.chromosome.length+1); // one over length of chromosome
		
		int idx = 0;
		for (int i = 0; i < father.chromosome.length; i++) {
			if (i >= crossPoints.get(idx)) {
				if (i >= crossPoints.get(idx+1)) {
					idx += 2;
					childOne.chromosome[i] = father.chromosome[i];
					childTwo.chromosome[i] = mother.chromosome[i];
					continue;
				}
				childOne.chromosome[i] = mother.chromosome[i];
				childTwo.chromosome[i] = father.chromosome[i];
			} else {
				childOne.chromosome[i] = father.chromosome[i];
				childTwo.chromosome[i] = mother.chromosome[i];
			}
		}
		
		ArrayList<Individual> children = new ArrayList<>();
		children.add(childOne);
		children.add(childTwo);
		return children;
	}

	/**
	 * Mutation: original: based on the mutation-rate
	 * Either +3 or -3 (orig. parameter)
	 *
	 */
	private void mutate(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) { // orig: 99% chance
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}
	}
	
	private void mutateSwap(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			// select two genes to be swapped
			
			int randomOne = Parameters.random.nextInt(individual.chromosome.length-1);
			int randomTwo = Parameters.random.nextInt(individual.chromosome.length-1);
			
			double tempOne = individual.chromosome[randomOne];
			double tempTwo = individual.chromosome[randomTwo];
			
			individual.chromosome[randomOne] = tempTwo;
			individual.chromosome[randomTwo] = tempOne;
		}
	}
	
	private void mutateScramble(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			// make two random numbers equal to or under the length of the chromosome
			int randomLower = ThreadLocalRandom.current().nextInt(0, (individual.chromosome.length-1) + 1); // 0-max
			int randomHigher = ThreadLocalRandom.current().nextInt(randomLower, (individual.chromosome.length-1) + 1); // rnd-max
			int rangeLength = (randomHigher - randomLower) + 1; // both inclusive
			
			// initiate a tmpList of the length rangeLength
			double[] tmpList = new double[rangeLength];
			
			// populate tempList
			for (int i = 0; i < rangeLength; i++) {
				tmpList[i] = individual.chromosome[i + randomLower]; // add the chromosome from the corresponding index
			}
			
			// swap two random values scrambleCount times (just like in mutateSwap)
			for (int i = 0; i < Parameters.scrambleCount; i++) {
				int randomOne = Parameters.random.nextInt(rangeLength);
				int randomTwo = Parameters.random.nextInt(rangeLength);
				
				double tempOne = tmpList[randomOne];
				double tempTwo = tmpList[randomTwo];
				
				tmpList[randomOne] = tempTwo;
				tmpList[randomTwo] = tempOne;
			}
			
			// overwrite the initial values with the scrambled values
			for (int i = 0; i < rangeLength; i++) {
				individual.chromosome[i + randomLower] = tmpList[i];
			}
		}
	}
	
	private void mutateInverse(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			// make two random numbers equal to or under the length of the chromosome
			int randomLower = ThreadLocalRandom.current().nextInt(0, (individual.chromosome.length-1) + 1); // 0-max
			int randomHigher = ThreadLocalRandom.current().nextInt(randomLower, (individual.chromosome.length-1) + 1); // rnd-max
			int rangeLength = (randomHigher - randomLower) + 1; // both inclusive
			
			// initiate a tmpList of the length rangeLength
			double[] tmpList = new double[rangeLength];
			
			// populate tempList
			for (int i = 0; i < rangeLength; i++) {
				tmpList[i] = individual.chromosome[i + randomLower]; // add the chromosome from the corresponding index
			}
			
			// overwrite the initial values with the scrambled values
			for (int i = 0; i < rangeLength; i++) {
				individual.chromosome[randomHigher - i] = tmpList[i];
			}
		}
	}

	/**
	 *
	 * Replaces the worst member of the population
	 * (regardless of fitness)
	 *
	 */
	private void replace(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex();
			population.set(idx, individual);
		}
	}



	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i;
			}
		}
		return idx;
	}

	@Override
	public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
//		return Math.tanh(x);
		return Math.max(0, x); // ReLU: Rectified Linear Unit
	}
}
