
import java.util.ArrayList;
import java.util.Random;

public class Game {

	/*
	 * La classe du jeu
	 * Contient le joueur
	 * 			le serpent
	 * 			les fruits
	 * 	 		si le jeu est finit
	 * 			les méthodes qui les initialisent
	 * 			les avancées du jeu
	 * 			les getters/ setters
	 */

	private Canon canon;

	private Snake snake;

	private ArrayList<Fruit> fruits;

	private boolean gameEnd;

	private Random rand;

	static final int FRAMESIZE = 600;

	static final int NBCELL = 30;

	static final int CELL_SIZE = FRAMESIZE/NBCELL;

	static final int TICK_MS = 100; // Durée d'un tour de jeu en millisecondes

	public Game(){
		rand = new Random();
		snake = new Snake(this, new Coordinate(0,0), 3);
		canon = new Canon(this, new Coordinate(NBCELL/2, NBCELL - 1));
		spawnFruits();
		gameEnd = false;
	}

	public boolean isGameEnd() {
		return gameEnd;
	}

	public void setGameEnd(boolean gameEnd) {
		this.gameEnd = gameEnd;
	}

	public Canon getPlayer() {
		return canon;
	}

	public Snake getSnake() {
		return snake;
	}

	public ArrayList<Fruit> getFruits() {
		return fruits;
	}

	public int getNbCell() {
		return NBCELL;
	}

	public int getCellSize() {
		return CELL_SIZE;
	}

	public Fruit fruitAt(Coordinate coord) { // Le fruit qui est sur cette case, ou null s'il n'y en a pas
		for(Fruit fruit : fruits) {
			if(fruit.getCoordFruit().equals(coord)) {
				return fruit;
			}
		}
		return null;
	}

	public void spawnFruits() { // On (re)crée tous les fruits à des positions aléatoires
		fruits = new ArrayList<>();
		for(int i = 0; i < NBCELL; i++) {
			Coordinate coord;
			do { // On évite la ligne du joueur, le serpent et les cases déjà prises
				coord = new Coordinate(rand.nextInt(NBCELL), rand.nextInt(NBCELL - 1));
			} while(snake.getSnakeBody().contains(coord) || fruitAt(coord) != null);
			fruits.add(randomFruit(coord));
		}
	}

	private Fruit randomFruit(Coordinate coord) { // On choisit aléatoirement un des 4 fruits
		switch(rand.nextInt(4)) {
			case 0:  return new Bois(coord);
			case 1:  return new Fraise(coord);
			case 2:  return new Myrtille(coord);
			default: return new PieceOr(this, coord);
		}
	}

	public void move() { // Un "tick" du jeu, appelé toutes les TICK_MS millisecondes par la boucle de App
		canon.move();
		snake.move();
	}
}
