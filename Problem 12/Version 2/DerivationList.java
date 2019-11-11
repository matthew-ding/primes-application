import java.util.*;

public class DerivationList {
	String derivationList;
	String derivation;
	double probability;
	ArrayList<Derivation> derivationOrder = new ArrayList<>();
	
	public DerivationList(String list, String s, double p) {
		derivationList = list;
		derivation=s;
		probability=p;
	}
	
	//problem 12
	public DerivationList(ArrayList<Derivation> d, String s, double p) {
		derivationOrder=d;
		derivation=s;
		probability=p;
	}
	
	public String getDerivationList() {
		return derivationList;
	}
	
	public ArrayList<Derivation> getArrayList() {
		return derivationOrder;
	}
	
	public String getDerivation() {
		return derivation;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public void Add(Derivation d) {
		derivationOrder.add(d);
	}
}
