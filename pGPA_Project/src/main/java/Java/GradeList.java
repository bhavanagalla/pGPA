package Java;

import java.util.HashMap;

public class GradeList {

	private static HashMap<Integer ,String> gradelist = new HashMap<Integer ,String >();
	
	GradeList(){
		gradelist.put(1, "A+");
		gradelist.put(2, "A-");
		gradelist.put(3, "B+");
		gradelist.put(4, "B-");
		gradelist.put(5, "C+");
		gradelist.put(6, "C-");
		gradelist.put(7, "D+");
		gradelist.put(8, "D-");
		gradelist.put(9, "pass");
		
	}
	public static HashMap<Integer,String> getInstance(){
        return gradelist;
	}
}
