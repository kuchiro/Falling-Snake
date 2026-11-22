
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.JPanel;

public class FallingSnakeView extends JPanel {

	/*
	 * La classe View
	 * Contient le jeu
	 * 			les méthodes qui dessinent le jeu (les fruits se dessinent eux-mêmes)
	 * 			l'animation entre deux ticks (interpolation des positions)
	 * 			un constructeur
	 */


	private static final long serialVersionUID = 1L;

	private final Game game;

	private final int cell; // Taille d'une case en pixels

	private double alpha; // Avancement entre le tick précédent (0) et le tick actuel (1)

	private double canonX; // Position affichée du canon, qui glisse vers sa vraie position

	public FallingSnakeView(Game game) {
		this.game = game;
		this.cell = game.getCellSize();
		this.canonX = game.getPlayer().getCoordPlayers().getX();
		this.setBackground(Palette.BACKGROUND);
		this.setPreferredSize(new Dimension(Game.FRAMESIZE, Game.FRAMESIZE));
		this.setDoubleBuffered(true);
		this.setFocusable(true);
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
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		drawPanel(g);
		drawFruits(g);
		drawSnake(g);
		drawBullets(g);
		drawPlayer(g);
		if(game.isGameEnd()) {
			drawGameOver(g);
		}
		Toolkit.getDefaultToolkit().sync(); // Évite les saccades sur macOS / Linux
	}

	public void drawGameOver(Graphics2D g) { // Fonction qui affiche, par-dessus le plateau, qui a gagné
		g.setColor(Palette.OVERLAY);
		g.fillRect(0, 0, Game.FRAMESIZE, Game.FRAMESIZE);
		boolean playerWon = !game.getSnake().isAliveSnake();
		String title = playerWon ? "Bien joué !" : "Game Over";
		String subtitle = playerWon ? "Le serpent a été détruit" : "Le serpent a atteint le bas";
		drawCentered(g, title, new Font(Font.SANS_SERIF, Font.BOLD, 48), playerWon ? Palette.WIN : Palette.LOSE, Game.FRAMESIZE / 2 - 10);
		drawCentered(g, subtitle, new Font(Font.SANS_SERIF, Font.PLAIN, 18), Palette.TEXT, Game.FRAMESIZE / 2 + 30);
	}

	protected static void drawCentered(Graphics2D g, String text, Font font, Color color, int baseline) {
		g.setFont(font);
		g.setColor(color);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, (Game.FRAMESIZE - fm.stringWidth(text)) / 2, baseline);
	}

	public void drawPanel(Graphics2D g) { // Fonction qui dessine le plateau en damier (plus doux que des lignes blanches)
		g.setColor(Palette.BACKGROUND_ALT);
		for(int x = 0; x < game.getNbCell(); x++) {
			for(int y = 0; y < game.getNbCell(); y++) {
				if((x + y) % 2 == 0) {
					g.fillRect(x * cell, y * cell, cell, cell);
				}
			}
		}
	}

	public void drawFruits(Graphics2D g) { // Chaque fruit sait se dessiner lui-même (polymorphisme)
		for(Fruit fruit : game.getFruits()) {
			fruit.draw(g, fruit.getCoordFruit().getX() * cell, fruit.getCoordFruit().getY() * cell, cell);
		}
	}

	public void drawSnake(Graphics2D g) { // Fonction qui dessine le serpent en le faisant glisser d'une case à l'autre
		Color bodyColor = Palette.SNAKE_BODY;
		Color headColor = Palette.SNAKE_HEAD;
		if(game.getSnake().isInvincible()) { // Invincible : le serpent devient cyan et clignote doucement
			double pulse = 0.5 + 0.5 * Math.sin(System.nanoTime() / 1e9 * 10);
			bodyColor = Palette.mix(Palette.SNAKE_INVINCIBLE, Color.WHITE, 0.35 * pulse);
			headColor = Palette.mix(Palette.SNAKE_INVINCIBLE, Color.WHITE, 0.5 * pulse);
		}
		List<Coordinate> body = game.getSnake().getSnakeBody();
		List<Coordinate> previous = game.getSnake().getPreviousBody();
		double pad = cell * 0.08;
		for(int i = 0; i < body.size(); i++) {
			Coordinate to = body.get(i);
			Coordinate from = previousPosition(body, previous, i);
			double x = lerp(from.getX(), to.getX(), alpha) * cell;
			double y = lerp(from.getY(), to.getY(), alpha) * cell;
			boolean head = (i == body.size() - 1);
			g.setColor(head ? headColor : bodyColor);
			g.fill(new RoundRectangle2D.Double(x + pad, y + pad, cell - 2 * pad, cell - 2 * pad, cell * 0.5, cell * 0.5));
			if(head) {
				drawEyes(g, x, y, from, to);
			}
		}
	}

	private void drawEyes(Graphics2D g, double x, double y, Coordinate from, Coordinate to) {
		// Les yeux regardent dans le sens du déplacement
		int dx = to.getX() - from.getX();
		int dy = to.getY() - from.getY();
		if(dx == 0 && dy == 0) {
			dx = 1;
		}
		double cx = x + cell / 2.0 + dx * cell * 0.18;
		double cy = y + cell / 2.0 + dy * cell * 0.18;
		double spread = cell * 0.2;
		double r = cell * 0.11;
		g.setColor(Palette.SNAKE_EYE);
		g.fill(new Ellipse2D.Double(cx - dy * spread - r, cy - dx * spread - r, 2 * r, 2 * r));
		g.fill(new Ellipse2D.Double(cx + dy * spread - r, cy + dx * spread - r, 2 * r, 2 * r));
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

	public void drawPlayer(Graphics2D g) { // Fonction qui dessine le canon : un socle arrondi et un tube vers le haut
		double x = canonX * cell;
		double y = game.getPlayer().getCoordPlayers().getY() * cell;
		g.setColor(Palette.CANON_DARK);
		g.fill(new RoundRectangle2D.Double(x + cell * 0.36, y - cell * 0.15, cell * 0.28, cell * 0.6, cell * 0.2, cell * 0.2));
		g.setColor(Palette.CANON);
		g.fill(new RoundRectangle2D.Double(x + cell * 0.08, y + cell * 0.35, cell * 0.84, cell * 0.6, cell * 0.45, cell * 0.45));
	}

	private void drawBullets(Graphics2D g) { // Fonction qui dessine les balles en les faisant monter en continu
		g.setColor(Palette.BULLET);
		double size = cell * 0.4;
		for(Bullet bullet : game.getPlayer().getShots()){
			double x = lerp(bullet.getPreviousCoordBullet().getX(), bullet.getCoordBullet().getX(), alpha) * cell;
			double y = lerp(bullet.getPreviousCoordBullet().getY(), bullet.getCoordBullet().getY(), alpha) * cell;
			g.fill(new Ellipse2D.Double(x + (cell - size) / 2, y + (cell - size) / 2, size, size));
		}
	}
}
