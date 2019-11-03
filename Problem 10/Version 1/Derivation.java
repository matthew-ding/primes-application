
public class Derivation {
	String derivation;
	double probability;
	
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
}
