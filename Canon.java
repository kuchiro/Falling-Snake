
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Canon implements KeyListener{
	
	/*
	 * Classe d'un joueur
	 * Contient le jeu
	 * 			les coordonnées du joueur
	 * 			le fait de savoir si le joueur est encore en vie
	 * 			le déplacement du joueur avec les touches flèches vers la droite ou vers la gauche 
	 * 			le fait de tirer avec les touches espaces ou flèches vers le haut
	 * 			la liste de toutes les balles tire qui sont encore sur l'interface 
	 * 			un constructeur 
	 * 			des getter/ setter
	 * 			
	 */
	
	private Game game; 
	
	private Coordinate coordPlayers;
	
	private Direction directionPlayer; 
	
	private boolean isPlayerAlive;
	
	private ArrayList<Bullet> shots;
	
	public Canon(Game game,Coordinate coordPlayers) {
		this.game = game;
		this.isPlayerAlive = true;
		this.coordPlayers = coordPlayers;
		this.shots = new ArrayList<Bullet>();
	}
	
	public Coordinate getCoordPlayers() {
		return coordPlayers;
	}

	public void setCoordPlayers(Coordinate coordPlayers) {
		this.coordPlayers = coordPlayers;
	}

	public boolean isPlayerAlive() {
		return isPlayerAlive;
	}

	public void setPlayerAlive(boolean isPlayerAlive) {
		this.isPlayerAlive = isPlayerAlive;
	}
	
	public ArrayList<Bullet> getShots() {
		return shots;
	}
	
	public void move() { // Fonction qui verifie si la balle touche une des entités ou sort de l'interface
		for(int i = 0; i < shots.size();i++) {
			if(snakeCollision(i)) { // Si la balle entre en collision avec le serpent
				return;
			}
			if(obstacleCollision(i)) { // Si la balle entre en collision avec un obstacle
				return;
			}
			if(shots.get(i).getCoordBullet().getY() == -1) { // Si la balle est en dehors de l'interface alors on l'enlève
				shots.remove(i);
				return;
			}
			shots.get(i).setCoordBullet(new Coordinate (coordNewBulletX(i),coordNewBulletY(i))); // s'il n'y a eu aucun des cas on fait avancé la balle
		} 
	}
	
	public int coordNewBulletX(int i) { // Fonction de la prochaine position de la balle en X
		return shots.get(i).getCoordBullet().getX() + shots.get(i).getDirectionBullet().getX();
	}
	
	public int coordNewBulletY(int i) { // Fonction de la prochaine position de la balle en Y
		return shots.get(i).getCoordBullet().getY() + shots.get(i).getDirectionBullet().getY();
	}

	public boolean equalSnakeCollision(int i, int j) { // Fonction qui vérifie si la balle est en collision avec le serpent et que le serpent n'est pas invincible
		if(game.getSnake().getSnakeBody().get(j).getX() == shots.get(i).getCoordBullet().getX() && 
				game.getSnake().getSnakeBody().get(j).getY() == shots.get(i).getCoordBullet().getY() && 
				!game.getSnake().isInvincible()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean snakeCollision(int i) { // Fonction qui applique l'effet de la balle sur le serpent
		for(int j = 0; j<game.getSnake().getSnakeBody().size();j++) {
			if(equalSnakeCollision(i,j)) {
				shots.remove(i);
				if(game.getSnake().getSnakeBody().size() == 1) { // Si la balle est en collision avec le serpent et que la taille du serpent n'est que de un alors le serpent a perdu
					game.getSnake().setAliveSnake(false);
					game.setGameEnd(true);
					return true;
				}
				game.getSnake().getSnakeBody().remove(j); // Sinon on enlève une partie du serpent
				return true;
			}
		}
		return false;
	}
	
	public boolean obstacleCollision(int i) { // Fonction qui applique l'effet de la balle sur les obstacles
		for(int j = 0; j<game.getObstacles().size();j++) {
			if(game.getObstacles().get(j).getCoordObstacle().getX() == shots.get(i).getCoordBullet().getX() && 
					game.getObstacles().get(j).getCoordObstacle().getY() == shots.get(i).getCoordBullet().getY() ){
				shots.remove(i);
				game.getObstacles().remove(j);
				return true;
			}
		}
		return false;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@SuppressWarnings("static-access")
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) { // Si la touche appuyé est à droite alors 
			if(coordPlayers.getX() + 1 <= game.NBCELL - 1) { // On vérifie qu'il n'est pas à l'extrémité droite s'il ne l'est pas alors on le déplace à droite
				directionPlayer = directionPlayer.Right;
				this.coordPlayers = new Coordinate (coordPlayers.getX() + directionPlayer.getX(), coordPlayers.getY() + directionPlayer.getY()); 
			}
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT) { // Si la touche appuyé est à gauche alors 
			if(coordPlayers.getX() - 1 >= 0) { // On vérifie qu'il n'est pas à l'extrémité gauche s'il ne l'est pas alors on le déplace à gauche
				directionPlayer = directionPlayer.Left;
				this.coordPlayers = new Coordinate (coordPlayers.getX() + directionPlayer.getX(), coordPlayers.getY() + directionPlayer.getY()); 
			}
		} else if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) { // Si la touche appuyé est la flèche du haut ou la barre espace alors 
			if(shots.size() < game.NBCELL / 2) { // Si le nombre de balle sur l'interface est bien inférieur au nombre de case alors 
				directionPlayer = directionPlayer.Up; // On crée la balle avec les coordonnées juste au dessus du joueur
				Bullet shot = new Bullet(new Coordinate(coordPlayers.getX() + directionPlayer.getX(), coordPlayers.getY() + directionPlayer.getY()));
				shots.add(shot); // On l'ajoute dans la liste des balles
			}
		} 
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
