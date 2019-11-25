package problem10_11_12;
import java.util.*;

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


