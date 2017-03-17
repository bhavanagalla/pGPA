package Java;


//package org.objectledge.maven.plugins.jsc;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import java.io.Serializable;
import java.io.IOException; 
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;


public  class R implements Serializable {
private int studentId;
private int courseId;
private float rating;
private long timestamp;

public R() {}

public R(int studentId, int courseId, float rating, long timestamp) {
  this.studentId = studentId;
  this.courseId = courseId;
  this.rating = rating;
  this.timestamp = timestamp;
}

public int getstudentId() {
  return studentId;
}

public int getcourseId() {
  return courseId;
}

public float getRating() {
  return rating;
}

public long getTimestamp() {
  return timestamp;
}

public static  Rating parseRating(String str) {
	 
	   
  String[] fields = str.split("::");
  if (fields.length != 4) {
    throw new IllegalArgumentException("Each line must contain 4 fields");
  }
  int studentId = Integer.parseInt(fields[0]);
  int courseId = Integer.parseInt(fields[1]);
  float rating = Float.parseFloat(fields[2]);
  long timestamp = Long.parseLong(fields[3]);
  return new Rating(studentId, courseId, rating, timestamp);
}
public static void main(String[] args) throws IOException, Exception {
	  SparkConf conf = new SparkConf().setAppName("JavaWordCount").setMaster("local[2]").set("spark.executor.memory","1g");
//SparkConf conf = new SparkConf();//.setAppName("Simple Application").setMaster("spark://myhost:7077");
//Spark.eventLog.enabled=true;
// SparkConf conff = new SparkConf();//.setAppName("Simple Application").setMaster("spark://myhost:7077");
	// SparkConf conff = new SparkConf().setAppName("JavaWordCount").setMaster("local[2]").set("spark.executor.memory","1g");
JavaSparkContext jsc = new JavaSparkContext(conf);
JavaSparkContext sc = new JavaSparkContext(conf);
 SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
  //JavaRDD<String> inFIleRDD = jsc.textFile(filePath);
  JavaRDD<Rating> ratingsRDD = jsc.textFile("data/pGPA.csv").map(Rating::parseRating);
		DataFrame ratings = sqlContext.createDataFrame(ratingsRDD,Rating.class);
		DataFrame[] splits = ratings.randomSplit(new double[]{0.8, 0.2});
		DataFrame training = splits[0];
		DataFrame test = splits[1];

		// Build the recommendation model using ALS on the training data
		ALS als = new ALS()
		  .setMaxIter(5)
		  .setRegParam(0.01)
		  .setUserCol("studentId")
		  .setItemCol("courseId")
		  .setRatingCol("rating");
		ALSModel model = als.fit(training);

		// Evaluate the model by computing the RMSE on the test data
		DataFrame rawPredictions = model.transform(test);
		DataFrame predictions = rawPredictions
		  .withColumn("rating", rawPredictions.col("rating").cast(DataTypes.DoubleType))
		  .withColumn("prediction", rawPredictions.col("prediction").cast(DataTypes.DoubleType));
		RegressionEvaluator evaluator = new RegressionEvaluator()
		  .setMetricName("rmse")
		  .setLabelCol("rating")
		  .setPredictionCol("prediction");
		Double rmse = evaluator.evaluate(predictions);
		
		System.out.println("Root-mean-square error = " + rmse);
		//System.out.println("Root-mean-square error = " + rmse");

}	
}