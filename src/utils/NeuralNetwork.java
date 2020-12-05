package utils;

import java.io.Serializable;
import java.util.List;

public class NeuralNetwork implements Serializable{

    private static final long serialVersionUID = 1L;
    Matrix[] weights;
	Matrix[] biases;
	
	public NeuralNetwork(int...layers) {
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
	}
	
	public List<Float> propForward(Matrix input) {
		Matrix vector = input;
		for(int i = 0; i < weights.length; i++) {
			vector = Matrix.multiply(weights[i], vector); 
			vector = Matrix.add(vector, biases[i]);
			vector.sigmoid();
		}
		return vector.toArray();
	}
	
	public NeuralNetwork mutate(float mutationRate) {
		NeuralNetwork nn = new NeuralNetwork();
		nn.weights = new Matrix[this.weights.length];
		nn.biases = new Matrix[this.biases.length];
		
		for(int i = 0; i < this.weights.length; i++) {
			nn.weights[i] = Matrix.mutate(this.weights[i], mutationRate);
			nn.biases[i] = Matrix.mutate(this.biases[i], mutationRate);
		}
		return nn;
	}
}
