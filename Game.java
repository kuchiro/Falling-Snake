
import java.util.ArrayList;
import java.util.Random;

public class Game {
	
	/*
	 * La classe du jeu 
	 * Contient le joueur
	 * 			le serpent
	 * 			les obstacles
	 * 	 		si le jeu est finit 
	 * 			les méthodes qui les initialisent 
	 * 			les avancées du jeu
	 * 			les getters/ setters
	 */
	
	private Canon canon;
	
	private Snake snake;
	
	private ArrayList<Obstacle> obstacles;
	
	private boolean gameEnd;
	
	@SuppressWarnings("unused")
	private Random rand;
	
	static final int FRAMESIZE = 600;
	
	static final int NBCELL = 30;
	
	static final int CELL_SIZE = FRAMESIZE/NBCELL;
	
	public Game(){
		rand = new Random();
		initializeSnake();
		initializePlayer();
		initializeObstacle();
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

	public ArrayList<Obstacle> getObstacles() {
		return obstacles;
	}
	
	public void initializeSnake() { // On crée le serpent
		snake = new Snake(this, new Coordinate(0,0));
	}

	public void initializePlayer(){ // On crée le joueur
		canon = new Canon(this, new Coordinate(NBCELL/2, NBCELL - 1));
	}
	
	public void initializeObstacle() { // On crée les obstacles
		obstacles = new ArrayList<>();
		for(int i = 0; i < NBCELL; i++) {
			int randChooseObstacle = rand.nextInt(4); // On choisit aléatoirement entre 0 et 3 entre les 4 obstacles 
			int randPosX = rand.nextInt(NBCELL - 1); // On choisit aléatoire entre 0 et le nombre de case pour la position X
			int randPosY = rand.nextInt(NBCELL - 1); // On choisit aléatoire entre 0 et le nombre de case - 1 pour la position Y
			for(int j = 0; j < snake.getSnakeBody().size();j++) {
				while(randPosX == snake.getSnakeBody().get(j).getX() && randPosY == snake.getSnakeBody().get(j).getY()) { // On ne veut pas d'un obstacle qui est sur le serpent directement
					randPosX = rand.nextInt(NBCELL - 1);
					randPosY = rand.nextInt(NBCELL - 1);
				}
			}
			initializeObstacleAux(randChooseObstacle,randPosX,randPosY);
		}
	}
	
	public void initializeObstacleAux(int randChooseObstacle, int randPosX, int randPosY) {
		if(randChooseObstacle == 0) { // Si c'est 0 alors on ajoute un obstacle de bois 
			obstacles.add(new Obstacle(1,(new Coordinate(randPosX,randPosY))));
		} else if(randChooseObstacle == 1) { // Si c'est 1 alors on ajoute un obstacle de fraise
			obstacles.add(new Obstacle(2,(new Coordinate(randPosX,randPosY)))); 
		} else if(randChooseObstacle == 2) { // Si c'est 2 alors on ajoute un obstacle de myrtille
			obstacles.add(new Obstacle(3,(new Coordinate(randPosX,randPosY)))); 
		} else if(randChooseObstacle == 3) { // Si c'est 3 alors on ajoute un obstacle piece d'or
			obstacles.add(new Obstacle(4,(new Coordinate(randPosX,randPosY)))); 
		} 
	}
		
	public void move() {
		canon.move();
		snake.move();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}