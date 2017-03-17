package Java;
/*import java.io.File;
import java.util.List;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class MatrixFactorization {
	 public static void main(String[] args) throws Exception
     {
  
     DataModel model = new FileDataModel( new File( "data/pGPA.csv" ) );
     UserSimilarity similarity = new PearsonCorrelationSimilarity( model );
     UserNeighborhood neighborhood = new NearestNUserNeighborhood( 2, similarity, model );
     Recommender recommender = new GenericUserBasedRecommender( model, neighborhood, similarity );
     List recommendations = recommender.recommend(100,3);
     for (Object recommendation : recommendations)
         {
         	System.out.println(recommendation);
          }
     }
}*/
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.VectorWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.mahout.math.Vector;
 
public class MatrixFactorization
{
public static void main(String[] args) throws IOException
    {
    Configuration con = new Configuration();
    FileSystem fs = FileSystem.get(con);
    String input;
            String output;
            String line;
            input = "Data/pGPA.csv";
            output = "Data/output.csv";

            // Create a file reader object
    BufferedReader reader = new BufferedReader(new FileReader(input));
            // Create a SequenceFile writer object
    SequenceFile.Writer writer = new SequenceFile.Writer(fs,con,new Path(output), IntWritable.class, VectorWritable.class);
     
            // Read lines of input files, one record at a time
    while ((line = reader.readLine()) != null)
                {
                String[] rec;  int j;                         // A string array. 
                rec = line.split(",");                  // Split line at comma delimiter and fill the string array 
                double[] d = new double[rec.length];    // A double array of dimension rec.length
               // d[0] = Double.parseDouble(rec[0]);      // Double conversion needed for creating vector 
                //for (int i = 0; j < 50; j++) {
                    try{
                    	 d[0] = Double.parseDouble(rec[0]); 
                    	}catch(NumberFormatException ex){}
                     //  d[1] = Double.parseDouble(rec[1]);
                    try{
                    	d[1] = Double.parseDouble(rec[1]);
                   	}catch(NumberFormatException ex){}
               
                catch(ArrayIndexOutOfBoundsException exception)
                   {
                		//double dd= Double.parseDouble(rec[1]);
                	 //System.out.println("SubjectID: "+Double.parseDouble(rec[1]));
                   }
               // }
               /*      
                String str="N/A";
					try {
    			int val=Integer.parseInt(str);
					}catch (NumberFormatException e){
   					System.out.println("not a number"); 
					} */
                // We will print, per record, lots of outputs to bring clarity 
                System.out.println("------------------");
                System.out.println("rec array length: "+rec.length);
                System.out.println("StudentID: "+rec[0]);
                try{
                System.out.println("SubjectID: "+Double.parseDouble(rec[1]));}
                catch(ArrayIndexOutOfBoundsException exception)
                {
             		//double dd= Double.parseDouble(rec[1]);
             	 //System.out.println("SubjectID: "+Double.parseDouble(rec[1]));
                }
               //Create a Random access sparse vector.
                //A random access sparse vector
                // Create a Random access sparse vector. A random access sparse vector
                //  only stores non-zero values and any value can be accessed randomly
                //    as against sequential access.
                // Class, RandomAccessSparseVector, implements vector to 
                //   store non-zero values of type doubles
                // We may either create a RandomAccessSparseVector object of size just 1, as:
                //   Vector vec = new RandomAccessSparseVector(1);
                // Or, create a RandomAccessSparseVector of size 2, as:
                Vector vec = new RandomAccessSparseVector(rec.length);
                 
                // Method ,assign(), applies the function to each element of the receiver
                //   If RandomAccessSparseVector size is just 1, we may assign to it
                //      either studentID or SubjectID. For example: vec.assign(d[1]) ;. 
                // Argument to assign() can only be a double array or a a double value
                //   or a vector but not integer or text.
                vec.assign(d);      // Assign a double array to vector
              
                // Prepare for writing the vector in sequence file
                //    Create an object of class VectorWritable and set its value
                VectorWritable writable = new VectorWritable();
                writable.set(vec);
                                     
                // Check vector size
                System.out.println("Vector size: "+ vec.size());
                // Check vector value
                System.out.println("Vector value: "+ vec.toString());
                // Check what is actually being written to sequence file
                System.out.println("Vector value being written: "+writable.toString());
                System.out.println("Key value being written: "+d[0]);
                 
                // Mahout sequence file for 'recommend factorized' requires that key be of class IntWritable
                //   and value which is ignored be of class VectorWritable.
                // Append now line-input to sequence file in either way:
                //   writer.append( new IntWritable(Integer.valueOf(rec[0])) , writable);
                //    OR
                writer.append( new IntWritable( (int) d[0]) , writable);
                // Note: As value part of sequence file is ignored, we could have written just
                //       any arbitrary number to it instead of rec-array.
                }
            writer.close();
            }
    }