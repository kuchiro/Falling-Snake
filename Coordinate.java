
public class Coordinate {

	/*
	 * Classe qui a les coordonnées de toutes les entités du jeu
	 * Contient un constructeur
	 * 			un getter/ setter
	 * 			translate : la case voisine dans une direction
	 * 			equals / hashCode : deux coordonnées sont égales si elles ont le même x et le même y
	 * 			(ça permet d'utiliser list.contains(), list.indexOf(), ...)
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

	public Coordinate translate(Direction direction) {
		return new Coordinate(x + direction.getX(), y + direction.getY());
	}

	@Override
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(!(other instanceof Coordinate)) {
			return false;
		}
		Coordinate c = (Coordinate) other;
		return x == c.x && y == c.y;
	}

	@Override
	public int hashCode() {
		return 31 * x + y;
	}

}
