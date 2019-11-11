
public class Derivation {
	String derivation; //string that is derived
	double probability; //probability of occuring
	int counter=0; //number of times rule occurs (problem 12)
	
	public Derivation(String d, double p) {
		derivation=d;
		probability = p;
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
