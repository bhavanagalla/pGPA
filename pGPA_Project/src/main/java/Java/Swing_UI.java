package Java;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import Java.App;

public class Swing_UI {
	JButton submit,clear;
	JScrollPane spane;
	JTextField student_id;
	JLabel Student_ID;
	JTextArea result;
	BufferedReader br;
	PrintWriter pw;
	public void output(){
		//private NumberFormat number_format;
		Student_ID=new JLabel();
		submit=new JButton();
		clear=new JButton();
		spane = new JScrollPane();
		//student_id = new JFormattedTextField(numberformat);
		student_id = new JTextField();
		result = new JTextArea();
		
		
		Student_ID.setText("Enter Student_ID");
		student_id.setColumns(10);
		student_id.setFocusable(true);
		
		result.setColumns(35);
		result.setRows(10);
		result.setLineWrap(true);
		result.setWrapStyleWord(true);
		result.setEditable(false);
		result.setFocusable(true);
		spane.setViewportView(result);
		submit.setText("ENTER");
		clear.setText("CLEAR");
		submit.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					submitAction(event);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		clear.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				// TODO Auto-generated method stub
				try {
					clearAction(evt);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		JFrame frame= new JFrame("pGPA");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout(10, 15));
		JScrollPane areaScrollPane = new JScrollPane(result);
		areaScrollPane.getVerticalScrollBar().setValue(0);
		//areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(450, 300));
		JViewport jv=areaScrollPane.getViewport();  
		jv.setViewPosition(new Point(0,0));
		JPanel panel = new JPanel();
		panel.add(Student_ID);
		panel.add(student_id);
		panel.add(submit);
		panel.add(clear);
		//panel.add(jv);
		//panel.add(result);
		panel.add(areaScrollPane);
		panel.add(spane);
		frame.add(panel);
		frame.setPreferredSize(new Dimension(500,400));
		frame.pack();
		frame.setVisible(true);
		
		
		//numberformat = NumberFormat.getNumberInstance(); 
		
		
		
	}
	public void clearAction (ActionEvent event) throws Exception
	{
		result.setText("");
		student_id.setText("");
	}
	public void submitAction (ActionEvent event) throws Exception
	{
		App app=new App();
		String input = student_id.getText();
		System.out.println("ID is"+input);
		String message = "Please enter in Numeric";
		StringBuffer sb= new StringBuffer();
		
		//Reader in = null;
		long takeID=0;
		//BufferedReader br = new BufferedReader(new InputStreamReader(student_id.getText()));
		//BufferedReader br;

		if(NumberUtils.isNumber(input))
		{
			//takeID= Long.parseLong(input);
			InputStream in = IOUtils.toInputStream(input, "UTF-8");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			takeID = br.read();
			System.out.println("ID is"+takeID);
		}
		else
		{
			JOptionPane.showMessageDialog(student_id, message);
		}
		if(takeID != 0)
		{
			
			DataModel model;
			model = new FileDataModel(new File("data/pGPA.csv"));
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);   	
			UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
			List<RecommendedItem> recommendations = recommender.recommend(takeID, 100);
			result.append("Thanks ! Please find your recommended Courses and Predicted Grades\n");
	    	for (RecommendedItem recommendation : recommendations) {
	    		result.append(" \n");
	    		result.append("Recommended CourseID:"+recommendation.getItemID()+"\n");
	    		result.append("Course Name:\n");
	    	  app.findCourseName((int)recommendation.getItemID());
	    	  result.append(" Predicted GPA:"+recommendation.getValue()+"\n");
	    		int i=(int)recommendation.getValue();
	    		String grd = "PASS";
	    		
	    		switch(i)
	    		{
	    		case 5:
	    			grd = "A";
	    			break;
	    		case 4:
	    			grd = "A";
	    			break;
	    		case 3:
	    			grd = "B";
	    			break;
	    		case 2:
	    			grd = "C";
	    			break;
	    		case 1:
	    			grd = "D";
	    			break;
	    		case 0:
	    			grd = "E";
	    			break;
	    			
	    		}
	    		result.append(" Predicted Grade:"+grd+"\n");
	    	  	}
	    	
	    	//result.append(sb);
	    	
		}
		
	}
	
}