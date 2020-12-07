package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Matrix implements Serializable{

    private static final long serialVersionUID = 1L;
    private int M;
	private int N;
	private float[][] data;

	public Matrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new float[M][N];
    }
	
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
	
	public static Matrix random(int M, int N) {
        Matrix A = new Matrix(M, N);
        	for(int i = 0; i < M; i++) {
                for(int j = 0; j < N; j++) {
                    A.data[i][j]=(float) (Math.random()*2-1);
                }
            }
        return A;
    }
	
	public static Matrix fromArray(float[]x)
	{
		Matrix temp = new Matrix(x.length , 1);
		for(int i = 0 ; i < x.length; i++)
			temp.data[i][0]=x[i];
		return temp;
	}
	
	public void sigmoid() {
        for(int i=0;i<this.M;i++)
        {
            for(int j = 0; j < this.N; j++)
                this.data[i][j] = (float) (1/(1+Math.exp(-this.data[i][j]))); 
        }
    }
	
	public int[] shape() {
		return new int[]{this.M, this.N};
	}
	
	public String toString(){//overriding the toString() method  
		  return Arrays.deepToString(this.data);
	}
	
	public List<Float> toArray() {
		List<Float> temp= new ArrayList<Float>()  ;
		
		for(int i=0;i<this.M;i++)
		{
			for(int j=0;j<this.N;j++)
			{
				temp.add(data[i][j]);
			}
		}
		return temp;
	}
	
	public void multiply(float a) {
        for(int i=0;i<this.M;i++)
        {
            for(int j=0;j<this.N;j++)
            {
                this.data[i][j]*=a;
            }
        }
        
    }

	public static Matrix add(Matrix a, Matrix b)
	{	
		Matrix result = new Matrix(a.M, a.N);
		
	    if(a.N != b.N || a.M != b.M) {
	        System.out.println("Shape Mismatch");
	        return null;
	    }
	    
	    for(int i = 0; i < a.M; i++) {
	        for(int j = 0; j < a.N; j++) {
	            result.data[i][j] = a.data[i][j] += b.data[i][j];
	        }
	    }
	    
	    return result;
	}
	
	public static Matrix mutate(Matrix m, float mutationRate) {
		Matrix toAdd = Matrix.random(m.M, m.N);
		toAdd.multiply(mutationRate);
		return Matrix.add(m, toAdd);
	}
	
	public float getWeight(int i, int j) {
		return this.data[i][j];
	}
}
