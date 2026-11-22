
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JPanel;

public class FallingSnakeView extends JPanel {

	/*
	 * La classe View
	 * Contient le jeu
	 * 			les méthodes qui dessinent le jeu
	 * 			l'animation entre deux ticks (interpolation des positions)
	 * 			un constructeur
	 */


	private static final long serialVersionUID = 1L;

	private static final int CELL = Game.CELL_SIZE;

	private final Game game;

	private double alpha; // Avancement entre le tick précédent (0) et le tick actuel (1)

	private double canonX; // Position affichée du canon, qui glisse vers sa vraie position

	public FallingSnakeView(Game game) {
		this.game = game;
		this.canonX = game.getPlayer().getCoordPlayers().getX();
		this.setBackground(Color.black);
		this.setPreferredSize(new Dimension(Game.FRAMESIZE, Game.FRAMESIZE));
		this.setDoubleBuffered(true);
	}

	public void nextFrame(double alpha, double dtSeconds) { // Appelée par la boucle de jeu à chaque image
		this.alpha = alpha;
		double target = game.getPlayer().getCoordPlayers().getX();
		canonX += (target - canonX) * Math.min(1.0, dtSeconds * 25);
		repaint();
	}

	private static double lerp(double from, double to, double t) { // Interpolation linéaire entre deux valeurs
		return from + (to - from) * t;
	}

	@Override
	protected void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		if(!game.isGameEnd()) {
			drawPanel(g);
			drawSnake(g);
			drawObstacle(g);
			drawPlayer(g);
			drawBullets(g);
		} else {
			drawGameOver(g);
		}
		Toolkit.getDefaultToolkit().sync(); // Évite les saccades sur macOS / Linux
	}

	public void drawGameOver(Graphics2D g) { // Fonction qui dessine le message qui annonce qui a perdu
		g.setColor(Color.red);
		if(!game.getSnake().isAliveSnake()) {
			g.drawString("Well done player", CELL / 3, CELL / 3 );
		}
		if(!game.getPlayer().isPlayerAlive()) {
			g.drawString("Game Over Player", CELL / 3, CELL / 3);
		}
	}

	public void drawPanel(Graphics2D g) { // Fonction qui dessine les lignes
		g.setColor(Color.white);
		for(int i = 0; i < Game.NBCELL; i++) {
			g.drawLine(i * CELL, 0, i * CELL, Game.FRAMESIZE);
			g.drawLine(0, i * CELL, Game.FRAMESIZE, i * CELL);
		}
	}

	public void drawSnake(Graphics2D g) { // Fonction qui dessine le serpent en le faisant glisser d'une case à l'autre
		g.setColor(Color.green);
		if(game.getSnake().isInvincible()) {
			g.setColor(Color.gray);
		}
		List<Coordinate> body = game.getSnake().getSnakeBody();
		List<Coordinate> previous = game.getSnake().getPreviousBody();
		for(int i = 0; i < body.size(); i++) {
			Coordinate to = body.get(i);
			Coordinate from = previousPosition(body, previous, i);
			double x = lerp(from.getX(), to.getX(), alpha) * CELL;
			double y = lerp(from.getY(), to.getY(), alpha) * CELL;
			g.fill(new Rectangle2D.Double(x, y, CELL, CELL));
		}
	}

	private Coordinate previousPosition(List<Coordinate> body, List<Coordinate> previous, int i) {
		// On aligne les deux listes depuis la tête (dernier élément), car le serpent peut grandir ou perdre un morceau
		int k = previous.size() - (body.size() - i);
		Coordinate to = body.get(i);
		if(k < 0 || k >= previous.size()) {
			return to;
		}
		Coordinate from = previous.get(k);
		if(Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY()) > 1) { // Saut de plus d'une case : pas d'animation
			return to;
		}
		return from;
	}

	public void drawPlayer(Graphics2D g) { // Fonction qui dessine le joueur
		g.setColor(Color.RED);
		g.fill(new Rectangle2D.Double(canonX * CELL, game.getPlayer().getCoordPlayers().getY() * CELL, CELL, CELL));
	}

	public void drawObstacle(Graphics2D g) { // Fonction qui dessine les différents obstacles
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
			g.fillRect(game.getObstacles().get(i).getCoordObstacle().getX() * CELL, game.getObstacles().get(i).getCoordObstacle().getY() * CELL, CELL, CELL);
		}
	}

	private void drawBullets(Graphics2D g) { // Fonction qui dessine les balles en les faisant monter en continu
		g.setColor(Color.red);
		for(Bullet bullet : game.getPlayer().getShots()){
			double x = lerp(bullet.getPreviousCoordBullet().getX(), bullet.getCoordBullet().getX(), alpha) * CELL;
			double y = lerp(bullet.getPreviousCoordBullet().getY(), bullet.getCoordBullet().getY(), alpha) * CELL;
			g.fill(new Ellipse2D.Double(x, y, CELL / 2.0, CELL / 2.0));
		}
	}
}
