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
//PROBLEM 11 AND 12 HELPER CLASS
//stores an entire parse tree
//either stores an arraylist of a string being updated throughout the derivation (problem 11)
//or stores an arraylist of all the rules used during the derivation (problem 12)

public class DerivationList {
	double probability;
	ArrayList<String> derivationOrder = new ArrayList<>();
	ArrayList<Derivation> derivationObjOrder = new ArrayList<>();
	
	//problem 12 constructor
	public DerivationList(ArrayList<Derivation> d, double p, int a) {
		derivationObjOrder = d;
		probability=p;
	}
	
	//problem 11 constructor
	public DerivationList(ArrayList<String> d, double p) {
		derivationOrder=d;
		probability=p;
	}
	
	public ArrayList<String> getArrayList() {
		return derivationOrder;
	}
	
	public ArrayList<Derivation> getDerivationList() {
		return derivationObjOrder;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public void Add(Derivation d) {
		derivationObjOrder.add(d);
	}
	
	public String printArrayList() {
		String output="";
		for (int i=0; i<derivationOrder.size(); i++) {
			if (i<derivationOrder.size()-1) {
				output+=derivationOrder.get(i) + " -> ";
			}
			else {
				output+=derivationOrder.get(i);
			}
		}
		return output;
	}
	
	//checks if two derivations have identical rules
	public boolean isEqual(ArrayList<Derivation> other) {
		if (this.getDerivationList().size()!=other.size()) {
			return false;
		}
		for (int i=0; i<this.getDerivationList().size(); i++) {
			if (!this.getDerivationList().get(i).equals(other.get(i))) {
				return false;
			}
		}
		return true;
	}
}
