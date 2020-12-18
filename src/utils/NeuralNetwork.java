package utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import simulation.Environment;

public class NeuralNetwork implements Serializable{

    private static final long serialVersionUID = 1L;
    int[] structure;
    Matrix[] weights;
	Matrix[] biases;
	
	public NeuralNetwork(int...layers) {
		structure = layers.clone();
		weights = new Matrix[layers.length - 1];
		biases = new Matrix[layers.length - 1];
		
		for(int i = 0; i < layers.length - 1; i++) {
			weights[i] = Matrix.random(layers[i+1], layers[i]);
			biases[i] = Matrix.random(layers[i+1], 1);
		}
	}
	
	public NeuralNetwork() {
		this.weights = null;
		this.biases = null;
		this.structure = new int[0];
	}
	
	public Matrix propForward(Matrix input) {
		Matrix vector = input;
		for(int i = 0; i < weights.length; i++) {
			vector = Matrix.multiply(weights[i], vector); 
			vector = Matrix.add(vector, biases[i]);
			vector.sigmoid();
		}
		return vector;
	}
	
	public NeuralNetwork mutate() {
		// TODO better mutation
		
		float mutationRate = Environment.traitMutationRate;
		
		NeuralNetwork nn = new NeuralNetwork();
		nn.structure = this.structure.clone();
		nn.weights = new Matrix[this.weights.length];
		nn.biases = new Matrix[this.biases.length];
		
		// mutate each weight and bias matrix
		for(int i = 0; i < this.weights.length; i++) {
			nn.weights[i] = Matrix.mutate(this.weights[i]);
			nn.biases[i] = Matrix.mutate(this.biases[i]);
		}
		
		return nn;
	}
	
	public float getWeight(int weightMatrix, int inputPosition, int outputPosition) {
		return this.weights[weightMatrix].getWeight(inputPosition, outputPosition);
	}
	
	public int[] getStructure() {
		return this.structure;
	}
}
