
public class Bullet {
	
	/*
	 * Classe d'une balle 
	 * Contient la direction de la balle 
	 * 			les coordonnées de la balle 
	 * 			un constructeur 
	 * 			un getter/ setter
	 */
	
	private Direction directionBullet;
	
	private Coordinate coordBullet;
	
	@SuppressWarnings("static-access")
	public Bullet(Coordinate coordBullet) {
		this.coordBullet = coordBullet; 
		this.directionBullet = directionBullet.Up;
	}
	
	public Direction getDirectionBullet() {
		return directionBullet;
	}
		
	public Coordinate getCoordBullet() {
		return coordBullet;
	}
	
	public void setCoordBullet(Coordinate coordBullet) {
		this.coordBullet = coordBullet;
	}
	
}
