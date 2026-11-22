
import java.util.ArrayList;
import java.util.Random;

public class Game {

	/*
	 * La classe du jeu
	 * Contient le joueur
	 * 			le serpent
	 * 			les fruits
	 * 			la difficulté choisie (taille du plateau, vitesse, longueur du serpent)
	 * 	 		si le jeu est finit
	 * 			les méthodes qui les initialisent
	 * 			les avancées du jeu
	 * 			les getters/ setters
	 */

	static final int FRAMESIZE = 600; // Taille de la fenêtre en pixels, la même pour tous les niveaux

	private final Difficulty difficulty;

	private final int nbCell; // Nombre de cases sur une ligne (dépend de la difficulté)

	private final Random rand;

	private Canon canon;

	private Snake snake;

	private ArrayList<Fruit> fruits;

	private boolean gameEnd;

	public Game(Difficulty difficulty){
		this.difficulty = difficulty;
		this.nbCell = difficulty.getNbCell();
		this.rand = new Random();
		snake = new Snake(this, new Coordinate(0,0), difficulty.getSnakeLength());
		canon = new Canon(this, new Coordinate(nbCell/2, nbCell - 1));
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

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public int getNbCell() {
		return nbCell;
	}

	public int getCellSize() { // Taille d'une case en pixels
		return FRAMESIZE / nbCell;
	}

	public int getTickMs() { // Durée d'un tour de jeu en millisecondes
		return difficulty.getTickMs();
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
		for(int i = 0; i < nbCell; i++) {
			Coordinate coord;
			do { // On évite la ligne du joueur, le serpent et les cases déjà prises
				coord = new Coordinate(rand.nextInt(nbCell), rand.nextInt(nbCell - 1));
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

	public void move() { // Un "tick" du jeu, appelé toutes les getTickMs() millisecondes par la boucle de jeu
		canon.move();
		snake.move();
	}
}
