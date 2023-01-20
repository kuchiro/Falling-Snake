
public class Coordinate {
	
	/*
	 * Classe qui a les coordonnées de toutes les entités du jeu 
	 * Contient un constructeur
	 * 			un getter/ setter 
	 * 
	 */
	
	private int x;
	
	private int y;
	
	public Coordinate(int x, int y) {
		this.y = y;
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

}
