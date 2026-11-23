
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
	 * 			le déplacement des balles et leurs collisions
	 * 			un constructeur
	 * 			des getter/ setter
	 *
	 */

	private Game game;

	private Coordinate coordPlayers;

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

	public void moveBullets() { // Chaque balle monte d'une case, et disparaît quand elle sort de l'interface
		for(int i = shots.size() - 1; i >= 0; i--) {
			Bullet bullet = shots.get(i);
			bullet.setCoordBullet(bullet.getCoordBullet().translate(bullet.getDirectionBullet()));
			if(bullet.getCoordBullet().getY() < 0) {
				shots.remove(i);
			}
		}
	}

	/*
	 * Vérifie si chaque balle touche le serpent ou un fruit
	 * afterMove = false : on regarde seulement la case actuelle de la balle
	 * afterMove = true  : les balles et le serpent viennent de bouger, on compte aussi
	 *                     les cas où ils se sont croisés pendant le tour (voir Snake.hitByShot)
	 */
	public void checkHits(boolean afterMove) {
		// On parcourt la liste à l'envers : retirer la balle i ne décale pas celles qu'il reste à traiter
		for(int i = shots.size() - 1; i >= 0 && !game.isGameEnd(); i--) {
			Bullet bullet = shots.get(i);
			Coordinate before = afterMove ? bullet.getPreviousCoordBullet() : null;
			if(game.getSnake().hitByShot(before, bullet.getCoordBullet())) { // Si la balle touche le serpent (et qu'il n'est pas invincible)
				shots.remove(i);
				continue;
			}
			Fruit fruit = game.fruitAt(bullet.getCoordBullet());
			if(fruit != null) { // Si la balle touche un fruit, les deux disparaissent
				shots.remove(i);
				game.getFruits().remove(fruit);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if(game.isGameEnd() || game.isPaused()) { // Le canon ne bouge pas et ne tire pas en pause
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) { // On vérifie qu'il n'est pas à l'extrémité droite s'il ne l'est pas alors on le déplace à droite
			if(coordPlayers.getX() + 1 <= game.getNbCell() - 1) {
				coordPlayers = coordPlayers.translate(Direction.Right);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT) { // On vérifie qu'il n'est pas à l'extrémité gauche s'il ne l'est pas alors on le déplace à gauche
			if(coordPlayers.getX() - 1 >= 0) {
				coordPlayers = coordPlayers.translate(Direction.Left);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) { // Tir avec la flèche du haut ou la barre espace
			if(shots.size() < game.getNbCell() / 2) { // On limite le nombre de balles sur l'interface
				shots.add(new Bullet(coordPlayers.translate(Direction.Up))); // La balle part juste au dessus du joueur
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
