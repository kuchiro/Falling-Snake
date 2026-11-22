
public class Bullet {

	/*
	 * Classe d'une balle
	 * Contient la direction de la balle
	 * 			les coordonnées de la balle
	 * 			les coordonnées au tick précédent (pour animer le déplacement)
	 * 			un constructeur
	 * 			un getter/ setter
	 */

	private Direction directionBullet;

	private Coordinate coordBullet;

	private Coordinate previousCoordBullet;

	public Bullet(Coordinate coordBullet) {
		this.coordBullet = coordBullet;
		this.directionBullet = Direction.Up;
		// La balle part du canon, juste en dessous de sa première case
		this.previousCoordBullet = new Coordinate(coordBullet.getX() - directionBullet.getX(), coordBullet.getY() - directionBullet.getY());
	}

	public Direction getDirectionBullet() {
		return directionBullet;
	}

	public Coordinate getCoordBullet() {
		return coordBullet;
	}

	public Coordinate getPreviousCoordBullet() {
		return previousCoordBullet;
	}

	public void setCoordBullet(Coordinate coordBullet) {
		this.previousCoordBullet = this.coordBullet;
		this.coordBullet = coordBullet;
	}

}
