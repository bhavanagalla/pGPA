package Java;
/*

import java.util.*;
import java.io.*;
import java.lang.Math;
import java.util.Random;

public class CV
{
	public static class Pair<A,B>
	{
		public final A a;
		public final B b;
		
		public Pair(A a, B b)
		{
			this.a = a;
			this.b = b;		
		}
	}
	public static final String inputfile = "Data/pGPA.csv";

	public static void KFold(String inputFile, int r, float rate, float lambda, int K)throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		StringTokenizer st = null;
		String row;
		int[][] data = new int[87768][3];
		int num = 0;
		while ((row = br.readLine()) != null)
		{
			st = new StringTokenizer(row, ",");
			while(st.hasMoreTokens())
                	{
				data[num][0] = Integer.parseInt(st.nextToken());
				data[num][1] = Integer.parseInt(st.nextToken());
				data[num][2] = Integer.parseInt(st.nextToken());
				st.nextToken();
			}
			num += 1;
		}

		int numFold = data.length/K;
		
		int[][][] dataFold = new int[numFold][3][K];

		for (int i = 0; i < K; ++i)
		{
			for (int j = 0; j < numFold; ++j)
			{
				dataFold[j][0][i] = data[i*numFold+j][0];
				dataFold[j][1][i] = data[i*numFold+j][1];
				dataFold[j][2][i] = data[i*numFold+j][2];
			}
		}

		double rmse = 0.0;

		for (int k = 0; k < K; ++k)
		{
			int[][] train = new int[numFold*(K-1)][3];
			int[][] test = new int[numFold][3];
			int numtrain = 0;
			int numtest = 0;
			for (int m = 0; m < K; ++m)
			{
				for (int n = 0; n < numFold; ++n)
				{
					if (m == k)
					{
						test[numtest][0] = dataFold[n][0][m];
						test[numtest][1] = dataFold[n][1][m];
						test[numtest][2] = dataFold[n][2][m];
						numtest++;
					}
					else
					{
						train[numtrain][0] = dataFold[n][0][m];
						train[numtrain][1] = dataFold[n][1][m];
						train[numtrain][2] = dataFold[n][2][m];
						numtrain++;
					}
				}
			}

			float[][] matrix = new float[1000][2069];
			for (int i = 0; i<matrix.length; ++i)
			{
				for (int j = 0; j<matrix[0].length; ++j)
				{
					matrix[i][j] = -1f;
				}
			}

			for (int i = 0; i < train.length; ++i)
			{
				int user = train[i][0];
				int movie = train[i][1];
				float rating = (float)train[i][2];
				matrix[user-1][movie-1] = rating;
			}

			int[][] testInput = new int[numFold][2];
			float[] testOutput = new float[numFold];

			for (int i = 0; i < test.length; ++i)
			{
				testInput[i][0] = test[i][0];
				testInput[i][1] = test[i][1];
				testOutput[i] = (float)test[i][2];
			}

			Pair<float[][], float[][]> p = myRecommender(matrix,r,rate,lambda);
			float[] predictOutput = PredictRating(p.a,p.b,testInput);
			rmse += findRMSE(testOutput,predictOutput);
		}
		rmse /= K;
		System.out.println(rmse);
	}

	public static Pair<float[][], float[][]> myRecommender(float[][] matrix, int r, float rate, float lambda)
	{
		int maxIter = 1000;
		int n1 = matrix.length;
		int n2 = matrix[0].length;

		float[][] U = new float[n1][r];
		float[][] V = new float[n2][r];

		// Initialize U and V matrix
		Random rand = new Random();
		for (int i = 0; i < U.length; ++i)
		{
			for (int j = 0; j < U[0].length; ++j)
			{
				U[i][j] = rand.nextFloat()/(float)r;
			}
		}

		for (int i = 0; i < V.length; ++i)
		{
			for (int j = 0; j < V[0].length; ++j)
			{
				V[i][j] = rand.nextFloat()/(float)r;
			}
		}


		// Gradient Descent
		for (int iter = 0; iter < maxIter; ++iter)
		{
			float[][] prodMatrix = new float[n1][n2];
			for (int i = 0; i < n1; ++i)
			{
				for (int j = 0; j < n2; ++j)
				{
					for (int k = 0; k < r; ++k)
					{
						prodMatrix[i][j] += U[i][k]*V[j][k];
					}
				}
			}		

			float[][] errorMatrix = new float[n1][n2];
			for (int i = 0; i < n1; ++i)
			{
				for (int j = 0; j < n2; ++j)
				{
					if (matrix[i][j] == -1f)
					{
						errorMatrix[i][j] = 0f;
					}
					else
					{
						errorMatrix[i][j] = matrix[i][j] - prodMatrix[i][j];
					}
				}
			}

			float[][] UGrad = new float[n1][r];
			for (int i = 0; i < n1; ++i)
			{
				for (int j = 0; j < r; ++j)
				{
					for (int k = 0; k < n2; ++k)
					{
						UGrad[i][j] += errorMatrix[i][k]*V[k][j]; 
					}
				}
			}

			float[][] VGrad = new float[n2][r];
			for (int i = 0; i < n2; ++i)
			{
				for (int j = 0; j < r; ++j)
				{
					for (int k = 0; k < n1; ++k)
					{
						VGrad[i][j] += errorMatrix[k][i]*U[k][j];
					}
				}
			}	

			float[][] Un = new float[n1][r];
			for (int i = 0; i < n1; ++i)
			{
				for (int j = 0; j < r; ++j)
				{
					Un[i][j] = (1f - rate*lambda)*U[i][j] + rate*UGrad[i][j];
				}
			}

			float[][] Vn = new float[n2][r];
			for (int i = 0; i < n2; ++i)
			{
				for (int j = 0; j < r; ++j)
				{
					Vn[i][j] = (1f - rate*lambda)*V[i][j] + rate*VGrad[i][j];
				}
			}
			
			U = Un;
			V = Vn;
		}		
		Pair<float[][], float[][]> p = new Pair<float[][], float[][]>(U,V);
		return p;
	}
	
	public static float[] PredictRating(float[][] U, float[][] V, int [][] test)
	{
		int n1 = U.length;
		int n2 = V.length;
		int r = V[0].length;

		float[][] prodMatrix = new float[n1][n2];
		for (int i = 0; i < n1; ++i)
		{
			for (int j = 0; j < n2; ++j)
			{
				for (int k = 0; k < r; ++k)
				{
					prodMatrix[i][j] += U[i][k]*V[j][k];
				}
			}
		}
		
		float[] opMatrix = new float[test.length];
		for (int i = 0; i < test.length; ++i)
		{
			int user = test[i][0]-1;
			int movie = test[i][1]-1;
			opMatrix[i] = prodMatrix[user][movie];
		}
		return opMatrix;
	}

	public static double findRMSE(float[] actualMatrix, float[] opMatrix)
	{
		double rmse = 0.0;		
		for (int i = 0; i < opMatrix.length; ++i)
		{
			rmse += (double)(actualMatrix[i] - opMatrix[i])*(actualMatrix[i] - opMatrix[i]);
		}
		rmse = Math.sqrt(rmse/opMatrix.length);
		return rmse;
	}
	public static void main(String args[])throws IOException
	{
		
		System.out.println("Recommendation System Ratings!!!");
		  int argslen=args.length;
	        int argsValue[] = new int[argslen];
	        System.out.println(argslen);
	        for(int i = 0; i < 606; i++){
	            //System.out.println("index[" + i + "], value:" + argsValue[i]);
	        	String inputfile = args[i];
	        
				
	    		String input = args[i+1]; 
	    		
	    		int r = Integer.parseInt(input);
	    		input = args[i+2];
	    		float rate = Float.parseFloat(input);
	    		input = args[i+3];
	    		float lambda = Float.parseFloat(input);
	    		input = args[i+4];
	    		int K = Integer.parseInt(input);
	    		KFold(inputfile, r, rate, lambda, K);
	        }


		
	}
}*/
import scala.Tuple2;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.SparkConf;
// $example off$

public class CV {
  public static void main(String[] args) {
    // $example on$
    //SparkConf conf = new SparkConf().setAppName("Java Collaborative Filtering Example");
	  SparkConf conf = new SparkConf().setAppName("Java Collaborative Filtering ").setMaster("local[2]").set("spark.executor.memory","1g");
    JavaSparkContext jsc = new JavaSparkContext(conf);

    // Load and parse the data
    String path = "Data/pGPA.csv";
    JavaRDD<String> data = jsc.textFile(path);
    JavaRDD<Rating> ratings = data.map(
      new Function<String, Rating>() {
        public Rating call(String s) {
          String[] sarray = s.split(",");
          return new Rating(Integer.parseInt(sarray[0]), Integer.parseInt(sarray[1]),
            Double.parseDouble(sarray[2]));
        }
      }
    );

    // Build the recommendation model using ALS
    int rank = 10;
    int numIterations = 10;
    MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(ratings), rank, numIterations, 0.01);

    // Evaluate the model on rating data
    JavaRDD<Tuple2<Object, Object>> userProducts = ratings.map(
      new Function<Rating, Tuple2<Object, Object>>() {
        public Tuple2<Object, Object> call(Rating r) {
          return new Tuple2<Object, Object>(r.user(), r.product());
        }
      }
    );
    JavaPairRDD<Tuple2<Integer, Integer>, Double> predictions = JavaPairRDD.fromJavaRDD(
      model.predict(JavaRDD.toRDD(userProducts)).toJavaRDD().map(
        new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
          public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
            return new Tuple2<>(new Tuple2<>(r.user(), r.product()), r.rating());
          }
        }
      ));
    JavaRDD<Tuple2<Double, Double>> ratesAndPreds =
      JavaPairRDD.fromJavaRDD(ratings.map(
        new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
          public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
            return new Tuple2<>(new Tuple2<>(r.user(), r.product()), r.rating());
          }
        }
      )).join(predictions).values();
    double MSE = JavaDoubleRDD.fromRDD(ratesAndPreds.map(
      new Function<Tuple2<Double, Double>, Object>() {
        public Object call(Tuple2<Double, Double> pair) {
          Double err = pair._1() - pair._2();
          return err * err;
        }
      }
    ).rdd()).mean();
    System.out.println("Mean Squared Error = " + MSE);

    // Save and load model
    model.save(jsc.sc(), "target/tmp/myCollaborativeFilter");
    MatrixFactorizationModel sameModel = MatrixFactorizationModel.load(jsc.sc(),
      "target/tmp/myCollaborativeFilter");
    // $example off$

    jsc.stop();
  }
}