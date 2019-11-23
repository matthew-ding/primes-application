
public class Derivation implements Comparable<Derivation> {
	String derivation;
	
	public Derivation(String s) {
		derivation=s;
	}
	
	public int compareTo(Derivation s) {
		if (this.derivation.length()<s.derivation.length()) {
			return -1;
		}
		else if (this.derivation.length()>s.derivation.length()) {
			return 1;
		}
		else {
			return this.derivation.compareTo(s.derivation);
		}
	}
}
