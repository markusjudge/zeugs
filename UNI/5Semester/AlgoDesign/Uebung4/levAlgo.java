public class LevDistance {
	
	//Kosten
	private static final double C1 = 1.0;
	private static final double C2 = 1.0;


	public double lev(String s, String t) {
		double D[][] = new double[s.length() + 1][t.length() + 1];
		
	    //Initiales Fuellen
		
		//Zwei leere Woerter werden verglichen:
		D[0][0]=0;
		
		//Zellen D_{0,1} bis D_{1,|t|}
		for (int j = 1; j < t.length(); j++) {
			D[0][j] = j;
		}
		
		//Zellen D_{1,0} bis D_{|s|,0}
		for (int i = 1; i < s.length(); i++) {
			D[i][0] = i;
		}
		for (int i = 1; i <= s.length(); i++) {
			for (int j = 1; j <= t.length(); j++) { 
				// Loeschen
				double delete = D[i - 1][j] + C1; 
				// Einfuegen
				double insert = D[i][j - 1] + C1; 
				// Ersetzen
				double replace = D[i - 1][j - 1];
				if (s.charAt(i - 1) != t.charAt(j - 1)) {
					replace += C2;
				} // Nur das Minimus wird aber ausgewaehlt!
				D[i][j] = Math.min(Math.min(delete, insert), replace);
			}
		} // Das Ergebnis befindet sich in Zelle D_{|s|}{|t|}
		return D[s.length()][t.length()];
	}
}
