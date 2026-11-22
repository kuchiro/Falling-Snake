
import java.util.ArrayList;

public class Snake {

	/*
	 * Class du serpent
	 * Contient le corps du serpent (le dernier élément est la tête)
	 * 			le corps au tick précédent (pour l'animation)
	 * 			le jeu
	 * 			la direction du serpent
	 * 			le fait de savoir si le serpent est en vie
	 * 			jusqu'à quand le serpent est invincible
	 * 			les actions que les fruits peuvent déclencher (grow, makeInvincible, turnDown)
	 * 			des getters/ setters
	 */

	private ArrayList<Coordinate> body;

	private ArrayList<Coordinate> previousBody; // Le corps au tick précédent, pour animer le déplacement

	private Game game;

	private Direction directionSnake;

	private boolean aliveSnake;

	private long invincibleUntil; // Heure (en ms) jusqu'à laquelle le serpent est invincible

	public Snake(Game game, Coordinate start, int length) {
		aliveSnake = true;
		invincibleUntil = 0;
		this.game = game;
		directionSnake = Direction.Right;
		body = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			body.add(new Coordinate(start.getX() + i, start.getY()));
		}
		previousBody = new ArrayList<>(body);
	}

	public void setAliveSnake(boolean aliveSnake) {
		this.aliveSnake = aliveSnake;
	}

	public boolean isAliveSnake() {
		return aliveSnake;
	}

	public boolean isInvincible() {
		return System.currentTimeMillis() < invincibleUntil;
	}

	public ArrayList<Coordinate> getSnakeBody() {
		return body;
	}

	public ArrayList<Coordinate> getPreviousBody() {
		return previousBody;
	}

	public boolean isMovingHorizontally() {
		return directionSnake == Direction.Left || directionSnake == Direction.Right;
	}

	/* ---------- Actions déclenchées par les fruits ---------- */

	public void grow() { // Fraise : on ajoute un morceau au bout de la queue
		body.add(0, body.get(0));
	}

	public void makeInvincible(long durationMs) { // Myrtille : invincible pendant durationMs millisecondes
		invincibleUntil = System.currentTimeMillis() + durationMs;
	}

	public Coordinate turnDown(Coordinate current) { // Bord du plateau ou bois : on descend d'une ligne et on repart dans l'autre sens
		Direction after = (directionSnake == Direction.Right) ? Direction.Left : Direction.Right;
		directionSnake = Direction.Down;
		Coordinate below = meetFruit(current, current.translate(Direction.Down)); // Il peut y avoir un fruit juste en dessous
		directionSnake = after;
		return below;
	}

	/* ---------- Déplacement et collisions ---------- */

	private Coordinate meetFruit(Coordinate current, Coordinate next) { // Si un fruit est sur la case, on applique son effet
		Fruit fruit = game.fruitAt(next);
		if(fruit == null) {
			return next;
		}
		if(fruit.isEatenBySnake()) {
			game.getFruits().remove(fruit);
		}
		return fruit.applyEffect(this, current, next); // Polymorphisme : chaque fruit sait ce qu'il fait
	}

	/*
	 * Une balle vient d'arriver sur la case shotNow (elle était sur shotBefore au début du tour,
	 * ou shotBefore = null si on ne regarde que sa case actuelle)
	 * Retourne true si elle touche le serpent. Elle le touche si :
	 * 		- elle est sur une case du corps,
	 * 		- ou elle a croisé un morceau pendant le tour : elle est entrée dans la case que ce morceau
	 * 		  vient de quitter, ou le morceau est entré dans la case que la balle vient de quitter
	 * Sans ce deuxième cas, la tête et la queue esquivaient une partie des tirs,
	 * alors qu'à l'écran on voyait la balle passer dessus
	 */
	public boolean hitByShot(Coordinate shotBefore, Coordinate shotNow) {
		if(isInvincible()) {
			return false;
		}
		int index = body.indexOf(shotNow);
		if(index < 0 && shotBefore != null) {
			// On associe chaque morceau à sa position du tour précédent, en partant de la tête
			int n = body.size();
			int m = previousBody.size();
			for(int k = 1; k <= Math.min(n, m); k++) {
				if(shotNow.equals(previousBody.get(m - k)) || shotBefore.equals(body.get(n - k))) {
					index = n - k;
					break;
				}
			}
		}
		if(index < 0) {
			return false;
		}
		if(body.size() == 1) { // Dernier morceau touché : le serpent a perdu
			aliveSnake = false;
			game.setGameEnd(true);
		} else {
			body.remove(index); // Sinon on enlève une partie du serpent
		}
		return true;
	}
	
	public void move() { // Fonction qui fait l'avancée du serpent et qui verifie s'il y a des fruits
		previousBody = new ArrayList<>(body); // On garde l'ancienne position pour l'animation
		Coordinate current = body.get(body.size() - 1);
		Coordinate next = current.translate(directionSnake);
		if(next.getX() < 0 || next.getX() >= game.getNbCell()) { // Le serpent arrive au bord : il descend
			next = turnDown(current);
		} else {
			next = meetFruit(current, next);
		}
		body.add(next);
		body.remove(0);
		if(next.getY() >= game.getNbCell() - 1) { // Si le serpent arrive au niveau du joueur alors le serpent gagne
			game.getPlayer().setPlayerAlive(false);
			game.setGameEnd(true);
		}
	}
}
