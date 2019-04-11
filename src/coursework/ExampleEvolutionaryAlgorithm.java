package coursework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

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

			// Select 2 Individuals from the current population. Currently returns random Individual
//			Individual father = select_tournament();
//			Individual mother = select_tournament();
//			Individual father = select_roulette();
//			Individual mother = select_roulette();
			Individual father = select_stochastic();
			Individual mother = select_stochastic();

			// Generate a child by crossover. Not Implemented
			// ArrayList<Individual> children = reproduce(father, mother);

			// ArrayList<Individual> children = reproduce_cross_one(father, mother);
			ArrayList<Individual> children = reproduce_cross_uni(father, mother);

			//mutate the offspring
			mutate_swap(children);

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
	
	private Individual select_tournament() {
		
		// select k_individuals completely randomly from the population
		ArrayList<Individual> k_individuals = new ArrayList<>();
		//System.out.println(population);
		//System.out.println(population.toArray().length);
		for (int i = 0; i < Parameters.k_amount; i++) {
			k_individuals.add(population.get(Parameters.random.nextInt(Parameters.popSize)));
		}
		//System.out.println(population);
		//System.out.println(k_individuals.toArray().length);
		
		// select the one most fit individual from the k_individuals
		//System.out.println(k_individuals);
		Individual best_tournament_member = k_individuals.get(0);
		for (int i = 0; i < Parameters.k_amount; i++) {
			if (best_tournament_member.fitness < k_individuals.get(i).fitness) {
				best_tournament_member = k_individuals.get(i);
			}
		}
		//System.out.println(best_tournament_member);
		
		// repeat until desiered number of individuals are desired
		return best_tournament_member;
	}
	
	private Individual select_stochastic() {
		
		// sum up all the fitnesses from the population
		double fitnessSum = 0;
		for (int i = 0; i < Parameters.popSize; i++) {
			fitnessSum += population.get(i).fitness;
		}
		
		Individual father = null;
		Individual mother = null;
		
		// make two random points between 0 and the fitness sum
		double random_one = Math.random() * fitnessSum;
		double random_two = Math.random() * fitnessSum;
		
		// iterate through the population again, until we reach the random_number
		double partialSum_one = 0;
		double partialSum_two = 0;
		for (int i = 0; i < Parameters.popSize; i++) {
			partialSum_one += population.get(i).fitness;
			if (partialSum_one >= random_one) {
				father = population.get(i);
			}
			partialSum_two += population.get(i).fitness;
			if (partialSum_two >= random_two) {
				mother = population.get(i);
			}
		}
		
		return null; // return null should never happen
		
	}
	
	private Individual select_roulette() {
		
		// sum up all the fitnesses from the population
		double fitnessSum = 0;
		for (int i = 0; i < Parameters.popSize; i++) {
			fitnessSum += population.get(i).fitness;
		}
		
		// make a random point between 0 and the fitness sum
		double random_number = Math.random() * fitnessSum;
		
		// iterate through the population again, until we reach the random_number
		double partialSum = 0;
		for (int i = 0; i < Parameters.popSize; i++) {
			partialSum += population.get(i).fitness;
			if (partialSum >= random_number) {
				return population.get(i); // return the individual at current index
			}
		}
		return null; // return null should never happen
		
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

	public ArrayList<Individual> reproduce_cross_one(Individual father, Individual mother) {
		Individual child_one = new Individual();
		Individual child_two = new Individual();
		double crossPoint = Math.random() * father.chromosome.length; // make a crossover point

		for (int i = 0; i < father.chromosome.length; ++i) {
			if (i < crossPoint) {
				child_one.chromosome[i] = father.chromosome[i];
				child_two.chromosome[i] = mother.chromosome[i];
			}
			else {
				child_one.chromosome[i] = mother.chromosome[i];
				child_two.chromosome[i] = father.chromosome[i];
			}
		}
		ArrayList<Individual> children = new ArrayList<>();
		children.add(child_one);
		children.add(child_two);
		return children;
	}
	
	public ArrayList<Individual> reproduce_cross_uni(Individual father, Individual mother) {
		// uniform crossover: combining multiple (default two) candidate solutions to get new solutions (default two)
		// returns two sets of genes (chromosomes), both inherited from its parents
		
		Individual child_one = new Individual();
		Individual child_two = new Individual();

		for (int i = 0; i < father.chromosome.length; ++i) {
			if (Parameters.random.nextBoolean()) { // chance is 50-50 from either parent
				child_one.chromosome[i] = father.chromosome[i];
				child_two.chromosome[i] = mother.chromosome[i];
			}
			else {
				child_one.chromosome[i] = mother.chromosome[i];
				child_two.chromosome[i] = father.chromosome[i];
			}
		}
		ArrayList<Individual> children = new ArrayList<>();
		children.add(child_one);
		children.add(child_two);
		return children;
	}

	/**
	 * Mutation
	 *
	 *
	 */
	private void mutate(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}
	}
	
	private void mutate_swap(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			// select two genes to be swapped
			
			int random_one = Parameters.random.nextInt(individual.chromosome.length-1);
			int random_two = Parameters.random.nextInt(individual.chromosome.length-1);
			
			double temp_one;
			double temp_two;
			temp_one = individual.chromosome[random_one];
			temp_two = individual.chromosome[random_two];
			
			individual.chromosome[random_one] = temp_two;
			individual.chromosome[random_two] = temp_one;
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
		return Math.tanh(x);
	}
}
