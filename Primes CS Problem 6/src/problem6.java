import java.util.*;
import java.io.*;

public class problem6 {

	public static void main(String[] args) throws FileNotFoundException{
		Scanner in = new Scanner(new File("problem6.in"));
		in.useDelimiter(":|\\n");
		Grammar grammar = new Grammar();
		do  {
			String ruleleft = in.next();
			String ruleright = in.next();
			grammar.Add(ruleleft,ruleright);
		} while (in.hasNextLine());
		
		grammar.CNFConvert();
		
	    in.close();
	    
	    //for (int i=0; i<10; i++) {
	    //	System.out.println(grammar.newNonTerminal());
	    //}
	    //int result = 0;
	    //PrintWriter out = new PrintWriter(new File("problem6.out"));
	    //out.println(result);
	    //out.close();

	}
}
