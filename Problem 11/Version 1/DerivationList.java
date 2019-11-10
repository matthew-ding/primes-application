public class DerivationList {
	String derivationList;
	String derivation;
	double probability;
	
	public DerivationList(String list, String s, double p) {
		derivationList = list;
		derivation=s;
		probability=p;
	}
	
	public String getDerivationList() {
		return derivationList;
	}
	
	public String getDerivation() {
		return derivation;
	}
	
	public double getProbability() {
		return probability;
	}
}
