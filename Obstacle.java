
public class Obstacle {
	
	/*
	 * Classe d'Obstacles 
	 * Contient le type d'obstacle 
	 * 			les coordonnées de l'obstacle
	 */
	
	private int numberType;
	
	private Coordinate coordObstacle;
	
	public Obstacle(int type, Coordinate coordObstacle){
		this.numberType = type;
		this.coordObstacle = coordObstacle;
	}
	
	public int getNumberType() {
		return numberType;
	}
		
	public void setNumberType(int type){
		this.numberType = type;
	}
	
	public Coordinate getCoordObstacle() {
		return coordObstacle;
	}

}
