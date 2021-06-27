package problem10_11_12;
import java.util.*;

/*
Development Framework:
Eclipse IDE for Java Developers
Version: 2019-09 R (4.13.0)
Build id: 20190917-1200

Platform:
macOS 10.14.5

Language:
Java Version 8 Update 221 (build 1.8.0_221-b11)
 */
//PROBLEM 11 and 12 HELPER CLASS
//stores a backpointer of a node that points to both child nodes in the CYK parse forest/array
//b and c represent the two nonterminals that are generated
//a represents the row in the CYK array of the "left" child

public class BackPointer {
	ArrayList<ArrayList<Integer>> pointer = new ArrayList<ArrayList<Integer>>();
	
	public BackPointer() {
	}
	
	public void Add(int a, int b, int c) {
		pointer.add(new ArrayList<Integer>() {
			{
				add(a);
				add(b);
				add(c);
			}
		});
	}
	
	public int getSize() {
		return pointer.size();
	}
	
	public ArrayList<ArrayList<Integer>> getPointer() {
		return pointer;
	}
	
	public ArrayList<Integer> getPointer(int i) {
		return pointer.get(i);
	}
}


