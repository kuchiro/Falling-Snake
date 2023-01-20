
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("unused")
public class Snake {
	
	/*
	 * Class du serpent
	 * Contient la taille du serpent 
	 * 			le corps du serpent 
	 * 			le jeu 
	 * 			la direction du serpent 
	 * 			le fait de savoir si le serpent est en vie
	 * 			si le serpent est invincible 
	 * 			des getters/ setters
	 */

	private int INITSIZE = 3;
	
	private ArrayList<Coordinate> body;
	
	private Game game;
	
	private Direction directionSnake;
	
	private boolean aliveSnake;
	
	private boolean invincible;

	public Snake(Game game, Coordinate start) {
		aliveSnake = true;
		invincible = false;
		this.game = game;
		directionSnake = Direction.Right;
		body = new ArrayList<>();
		for (int i = 0; i < INITSIZE; i++) {
			body.add(new Coordinate(start.getX() + i, start.getY()));
		}
	}

	public void setAliveSnake(boolean aliveSnake) {
		this.aliveSnake = aliveSnake;
	}
	
	public boolean isAliveSnake() {
		return aliveSnake;
	}

	public boolean isInvincible() {
		return invincible;
	}

	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}

	public void setSnakeDirection(Direction directionSnake) {
		this.directionSnake = directionSnake;
	}

	public ArrayList<Coordinate> getSnakeBody() {
		return body;
	}
	
	public Coordinate newNext(Coordinate current,Direction directionSnake) { // lorsque le prochain mouvement change
		return new Coordinate(current.getX() + directionSnake.getX(), current.getY() + directionSnake.getY());
	}

	@SuppressWarnings("static-access")
	public void snakeWin(Coordinate next) {
		if(next.getY() == game.NBCELL-1) { // Si le serpent arrive au niveau du joueur alors le serpent gagne
			game.getPlayer().setPlayerAlive(false);
			game.setGameEnd(true);
		}
	}
	
	@SuppressWarnings("static-access")
	public Coordinate extremRight(Coordinate current, Coordinate next) {
		if(!checkObstacle(next) && next.getX() == game.NBCELL) { // Si le serpent arrive a l'extrémité droite
			setSnakeDirection(Direction.Down);
			next = newNext(current,directionSnake);
			if(checkObstacle(next)) { //  on regarde s'il n'y a pas d'obstacle juste en dessous 
				next = obstacleCollision(current,next);
			}
			setSnakeDirection(Direction.Left);
			return next;
		}
		return next;
	}
	
	public Coordinate extremLeft(Coordinate current, Coordinate next) {
		if(!checkObstacle(next) && next.getX() == - 1) { // Si le serpent arrive a l'extrémité gauche 
			setSnakeDirection(Direction.Down);
			next = newNext(current,directionSnake);
			if(checkObstacle(next)) { // on regarde s'il n'y a pas d'obstacle juste en dessous 
				next = obstacleCollision(current,next);
			}
			setSnakeDirection(Direction.Right);
			return next;
		}
		return next;
	}
	
	public Coordinate boisCollision(Coordinate current, Coordinate next) { // Pour l'obstacle de bois on change la direction
		if(directionSnake == Direction.Right) { // Si c'est vers la droite alors on le fait descendre puis on le fait aller vers la gauche
			setSnakeDirection(Direction.Down);
			next = newNext(current,directionSnake);
			if(checkObstacle(next)) { // On vérifie qu'il n'y a pas un obstacle juste en dessous lors du changement de sens 
				next = obstacleCollision(current,next);
			}
			setSnakeDirection(Direction.Left);
			return next;
		} else if(directionSnake == Direction.Left) { // Si c'est vers la gauche alors on le fait descendre puis on le fait aller vers la droite
			setSnakeDirection(Direction.Down);
			next = newNext(current,directionSnake);
			if(checkObstacle(next)) { // On vérifie qu'il n'y a pas un obstacle juste en dessous lors du changement de sens
				next = obstacleCollision(current,next);
			}
			setSnakeDirection(Direction.Right);
			return next;
		}
		return next;
	}
	
	public boolean checkObstacle(Coordinate next) { // On vérifie si le serpent est en contact avec un obstacle
		for(int i = 0; i < game.getObstacles().size(); i++) {
			if(next.getX() == game.getObstacles().get(i).getCoordObstacle().getX() &&
					next.getY() == game.getObstacles().get(i).getCoordObstacle().getY() ) {
				return true;
			}
		}
		return false;
	}
	
	public Coordinate obstacleCollision(Coordinate current, Coordinate next) { // Pour chaque prochain obstacle 
		for(int i = 0;i<game.getObstacles().size();i++) {
			if(checkBois(i,next)) { 
				next = boisCollision(current,next);
				return next;
			} else if(checkFraise(i, next)){ 
				body.add(new Coordinate(current.getX(), current.getY()));
				game.getObstacles().remove(i);
			} else if(checkMyrtille(i,next)) { 
				blueBerry();
				game.getObstacles().remove(i);
			} else if(checkPieceOr(i,next)) { 
				goldenCoin();
				game.getObstacles().remove(i);
			}
		}
		return next;
	}
	
	public boolean checkMyrtille(int i, Coordinate next) { // On verifie si l'obstacle est une myrtille
		if(game.getObstacles().get(i).getNumberType() == 3 && next.getX() == game.getObstacles().get(i).getCoordObstacle().getX() &&
				next.getY() == game.getObstacles().get(i).getCoordObstacle().getY()) {
			return true;
		}
		return false;
	}
	
	public void blueBerry() { // Effet de la myrtille
		invincible = true;
		new Thread(new Runnable() {
            public void run() {
                try { 
                    Thread.sleep(5000);
                    invincible = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
	}
	
	
	public boolean checkBois(int i, Coordinate next) { // On verifie si l'obstacle est un bois
		if(game.getObstacles().get(i).getNumberType() == 1 && next.getX() == game.getObstacles().get(i).getCoordObstacle().getX() &&
					next.getY() == game.getObstacles().get(i).getCoordObstacle().getY()) {
			return true;
		}
		return false;
	}
	
	public boolean checkFraise(int i, Coordinate next) { // On verifie si l'obstacle est une fraise
		if(game.getObstacles().get(i).getNumberType() == 2 && next.getX() == game.getObstacles().get(i).getCoordObstacle().getX() &&
				next.getY() == game.getObstacles().get(i).getCoordObstacle().getY()) {
			return true;
		}
		return false;
	}
	

	
	
	public boolean checkPieceOr(int i, Coordinate next) { // On verifie si l'obstacle est une piece d'or
		if(game.getObstacles().get(i).getNumberType() == 4 && next.getX() == game.getObstacles().get(i).getCoordObstacle().getX() &&
				next.getY() == game.getObstacles().get(i).getCoordObstacle().getY()) {
			return true;
		}
		return false;
	}
	
	public void goldenCoin() { // Effet de la piece d'or
		game.initializeObstacle();
	}
	
	
	public void move() { // Fonction qui fait l'avancée du serpent et qui verifie s'il y a des obstacles
		Coordinate current = body.get(body.size() - 1);
		Coordinate next = new Coordinate(current.getX() + directionSnake.getX(), current.getY() + directionSnake.getY());
		snakeWin(next);
		next = extremRight(current,next);
		next = extremLeft(current,next);
		next = obstacleCollision(current,next);
		body.add(next);
		body.remove(0);
	}
}
