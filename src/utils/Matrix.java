package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import simulation.Environment;

public class Matrix implements Serializable {
    private static final long serialVersionUID = 1L;
        
    public int M;
	public int N;
	public float[][] data;
		
	public Matrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new float[M][N];
    }
	
	// multiply two matrices
	public static Matrix multiply(Matrix a, Matrix b) {
        Matrix result = new Matrix(a.M,b.N);
        for(int i = 0;i < result.M; i++) {
            for(int j = 0; j < result.N; j++) {
                float sum = 0;
                for(int k = 0; k < a.N; k++) {
                    sum += a.data[i][k] * b.data[k][j];
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }
	
	// create a new random matrix
	public static Matrix random(int M, int N) {
        Matrix A = new Matrix(M, N);
    	for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                A.data[i][j] = Environment.networkValueRandRange * randNum();
            }
        }	
        return A;
    }
	
	// generate a random float uniformly from -1 to 1
	private static float randNum() {
		Random rand = new Random();
		return 2 * rand.nextFloat() - 1;
	}
	
	// return a net matrix from an array of floats
	public static Matrix fromArray(float[] x) {
		Matrix temp = new Matrix(x.length , 1);
		for(int i = 0 ; i < x.length; i++) {
			temp.data[i][0]=x[i];
		}
		
		return temp;
	}
	
	// apply the sigmoid function to each cell of the matrix
	public void sigmoid() {
        for(int i = 0; i < this.M; ++i) {
            for(int j = 0; j < this.N; ++j) {
                this.data[i][j] = (float) (1/(1+Math.exp(-this.data[i][j]))); 
            }
        }
    }
	
	// return the shape of the matrix
	public int[] shape() {
		return new int[]{this.M, this.N};
	}
	
	// return list form of matrix
	public List<Float> toArray() {
		List<Float> temp= new ArrayList<Float>()  ;
		for (int i = 0; i < this.M; ++i) {
			for (int j = 0; j < this.N; ++j) {
				temp.add(data[i][j]);
			}
		}
		return temp;
	}
	
	// multiply the matrix by a constant
	public void multiply(float a) {
        for(int i = 0; i < this.M; ++i) {
            for(int j = 0; j < this.N; ++j) {
                this.data[i][j] *= a;
            }
        }
    }
	
	// add two matrices
	public static Matrix add(Matrix a, Matrix b) {
		assert(a.N == b.N && a.M == b.M);

		Matrix result = new Matrix(a.M, a.N);	    
	    for(int i = 0; i < a.M; i++) {
	        for(int j = 0; j < a.N; j++) {
	            result.data[i][j] = a.data[i][j] += b.data[i][j];
	        }
	    }
	    
	    return result;
	}
	
	public static Matrix mutate(Matrix m) {
		Random rand = new Random();
		Matrix output = m.copy();
		
		// chance of not mutating at all
		if (rand.nextFloat() < Environment.networkMutationProbability) {
			// loop thru each cell
			for (int i = 0; i < m.M; ++i) {
				for (int j = 0; j < m.N; ++j) {
					float num = rand.nextFloat();
					
					// perturb
					if (num < Environment.networkPerturbationProbability) {
						output.data[i][j] += Environment.networkPerturbationAmount * randNum();
					// give new weight
					} else if (num - Environment.networkPerturbationProbability < Environment.networkNewValueProbability) {
						output.data[i][j] = Environment.networkValueRandRange * randNum();
					}
				}
			}
		}

		return output;
	}
	
	// return a copy of the matrix
	public Matrix copy() {
		Matrix output = new Matrix(M, N);
		for (int i = 0; i < M; ++i) {
			for (int j = 0; j < N; ++j) {
				output.data[i][j] = data[i][j];
			}
		}
		return output;
	}
	
	@Override
	public String toString(){  
		  return Arrays.deepToString(this.data);
	}
	
	// get the weight in a specific cell
	public float getWeight(int i, int j) {
		return this.data[i][j];
	}
}
