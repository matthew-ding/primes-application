package problem10_11_12;

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
//PGRAMMAR HELPER CLASS
//stores either a derivation and associated probability (in PGrammar), constructor A
//or stores a single rule (key-value pair) in the rule distribution counting of Problem 12

public class Derivation {
	String key; //problem 12
	String derivation; //string that is derived
	double probability; //probability of occuring
	int counter=0; //number of times rule occurs (problem 12)
	
	//constructor A (derivation and probability)
	public Derivation(String d, double p) {
		derivation=d;
		probability = p;
	}
	
	//problem 12, constructor B, key-value pair
	public Derivation(String k, String d) {
		key = k;
		derivation = d;
	}
	
	public void setKey(String k) {
		key = k;
	}
	
	public String getKey() {
		return key;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public String getDerivation() {
		return derivation;
	}
	
	public void setProbability(double p) {
		probability=p;
	}
	
	public void appendCounter() {
		counter++;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public void setCounter(int i) {
		counter=i;
	}
}
