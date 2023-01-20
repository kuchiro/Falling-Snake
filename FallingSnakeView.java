
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class FallingSnakeView extends JPanel {
	
	/*
	 * La classe View 
	 * Contient le jeu 
	 * 			les méthodes qui dessinent le jeu 
	 * 			un constructeur 
	 */
	
	
	private static final long serialVersionUID = 1L;
	private Game game;
	
	@SuppressWarnings("static-access")
	public FallingSnakeView(Game game) {
		this.game = game;
		this.setBackground(Color.black);
		this.setPreferredSize(new Dimension(game.FRAMESIZE, game.FRAMESIZE));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(!game.isGameEnd()) { 
			drawPanel(g);
			drawSnake(g);
			drawObstacle(g);
			drawPlayer(g);
			drawBullets(g);
		} else {
			drawGameOver(g);
		}
		repaint();	
	}
	
	@SuppressWarnings("static-access")
	public void drawGameOver(Graphics g) { // Fonction qui dessine le message qui annonce qui a perdu 
		g.setColor(Color.red);
		if(!game.getSnake().isAliveSnake()) { 
			g.drawString("Well done player", game.CELL_SIZE / 3, game.CELL_SIZE / 3 );
		} 
		if(!game.getPlayer().isPlayerAlive()) {
			g.drawString("Game Over Player", game.CELL_SIZE / 3, game.CELL_SIZE / 3);
		}
	}
	
	@SuppressWarnings("static-access")
	public void drawPanel(Graphics g) { // Fonction qui dessine les lignes 
		g.setColor(Color.white);
		for(int i = 0; i < game.FRAMESIZE / game.CELL_SIZE; i++) {
			g.drawLine(i * game.CELL_SIZE, 0, i * game.CELL_SIZE, game.FRAMESIZE);
			g.drawLine(0, i * game.CELL_SIZE, game.FRAMESIZE, i * game.CELL_SIZE);
		}
	}
	
	@SuppressWarnings("static-access")
	public void drawSnake(Graphics g) { // Fonction qui dessine le serpent
		g.setColor(Color.green);
		if(game.getSnake().isInvincible()) {
			g.setColor(Color.gray);
		}
		for(int i = 0; i < game.getSnake().getSnakeBody().size(); i++) {
			g.fillRect(game.getSnake().getSnakeBody().get(i).getX() * game.CELL_SIZE, game.getSnake().getSnakeBody().get(i).getY() * game.CELL_SIZE, game.CELL_SIZE, game.CELL_SIZE);
		}
	}
	
	@SuppressWarnings("static-access")
	public void drawPlayer(Graphics g) { // Fonction qui dessine le joueur 
		g.setColor(Color.RED);
		g.fillRect(game.getPlayer().getCoordPlayers().getX() * game.CELL_SIZE, game.getPlayer().getCoordPlayers().getY() * game.CELL_SIZE, game.CELL_SIZE, game.CELL_SIZE);
	}
	
	@SuppressWarnings("static-access")
	public void drawObstacle(Graphics g) { // Fonction qui dessine les différents obstacles
		for(int i = 0; i < game.getObstacles().size(); i++) {
			if(game.getObstacles().get(i).getNumberType() == 1) {
				g.setColor(new Color(130,85,50));
			} else if(game.getObstacles().get(i).getNumberType() == 2) {
				g.setColor(Color.magenta);
			} else if(game.getObstacles().get(i).getNumberType() == 3) {
				g.setColor(new Color(115,15,190));
			} else if(game.getObstacles().get(i).getNumberType() == 4) {
				g.setColor(Color.yellow);
			}
			g.fillRect(game.getObstacles().get(i).getCoordObstacle().getX() * game.CELL_SIZE, game.getObstacles().get(i).getCoordObstacle().getY() * game.CELL_SIZE, game.CELL_SIZE, game.CELL_SIZE);
		}
	}
	
	@SuppressWarnings("static-access")
	private void drawBullets(Graphics g) { // Fonction qui dessine les balles
		for(int i = 0; i < game.getPlayer().getShots().size(); i++){
			g.setColor(Color.red);
			g.fillOval(game.getPlayer().getShots().get(i).getCoordBullet().getX() * game.CELL_SIZE,game.getPlayer().getShots().get(i).getCoordBullet().getY() * game.CELL_SIZE, game.CELL_SIZE / 2, game.CELL_SIZE / 2);
		}
	}
}
