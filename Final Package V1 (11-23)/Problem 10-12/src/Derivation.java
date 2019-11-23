
public class Derivation {
	String key; //problem 12
	String derivation; //string that is derived
	double probability; //probability of occuring
	int counter=0; //number of times rule occurs (problem 12)
	
	public Derivation(String d, double p) {
		derivation=d;
		probability = p;
	}
	
	//problem 12
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
