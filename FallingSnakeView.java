
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.JPanel;

public class FallingSnakeView extends JPanel {

	/*
	 * La classe View
	 * Contient le jeu
	 * 			la palette de couleurs du jeu
	 * 			les méthodes qui dessinent le jeu
	 * 			l'animation entre deux ticks (interpolation des positions)
	 * 			un constructeur
	 */


	private static final long serialVersionUID = 1L;

	private static final int CELL = Game.CELL_SIZE;

	// Palette : fond bleu nuit doux (pas de noir pur), couleurs vives mais pas fluo
	private static final Color BACKGROUND       = new Color(0x1B, 0x1F, 0x2E);
	private static final Color BACKGROUND_ALT   = new Color(0x21, 0x26, 0x38); // case sur deux, damier discret
	private static final Color SNAKE_BODY       = new Color(0x4A, 0xDE, 0x80);
	private static final Color SNAKE_HEAD       = new Color(0x86, 0xEF, 0xAC);
	private static final Color SNAKE_INVINCIBLE = new Color(0x67, 0xE8, 0xF9);
	private static final Color SNAKE_EYE        = new Color(0x1B, 0x1F, 0x2E);
	private static final Color CANON            = new Color(0xFF, 0x8A, 0x65);
	private static final Color CANON_DARK       = new Color(0xE0, 0x62, 0x44);
	private static final Color BULLET           = new Color(0xFF, 0xE0, 0x82);
	private static final Color WOOD             = new Color(0xC9, 0x93, 0x63);
	private static final Color WOOD_GRAIN       = new Color(0x93, 0x63, 0x3E);
	private static final Color STRAWBERRY       = new Color(0xFF, 0x5C, 0x7A);
	private static final Color LEAF             = new Color(0x3D, 0xC9, 0x6B);
	private static final Color BLUEBERRY        = new Color(0x6B, 0x8C, 0xFF);
	private static final Color GOLD             = new Color(0xFF, 0xCB, 0x47);
	private static final Color GOLD_DARK        = new Color(0xD9, 0x9B, 0x1F);
	private static final Color HIGHLIGHT        = new Color(255, 255, 255, 90);
	private static final Color OVERLAY          = new Color(0x1B, 0x1F, 0x2E, 215);
	private static final Color TEXT             = new Color(0xF1, 0xF5, 0xF9);
	private static final Color WIN              = new Color(0x4A, 0xDE, 0x80);
	private static final Color LOSE             = new Color(0xFF, 0x5C, 0x7A);

	private final Game game;

	private double alpha; // Avancement entre le tick précédent (0) et le tick actuel (1)

	private double canonX; // Position affichée du canon, qui glisse vers sa vraie position

	public FallingSnakeView(Game game) {
		this.game = game;
		this.canonX = game.getPlayer().getCoordPlayers().getX();
		this.setBackground(BACKGROUND);
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
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		drawPanel(g);
		drawObstacle(g);
		drawSnake(g);
		drawBullets(g);
		drawPlayer(g);
		if(game.isGameEnd()) {
			drawGameOver(g);
		}
		Toolkit.getDefaultToolkit().sync(); // Évite les saccades sur macOS / Linux
	}

	public void drawGameOver(Graphics2D g) { // Fonction qui affiche, par-dessus le plateau, qui a gagné
		g.setColor(OVERLAY);
		g.fillRect(0, 0, Game.FRAMESIZE, Game.FRAMESIZE);
		String title;
		Color color;
		if(!game.getSnake().isAliveSnake()) {
			title = "Bien joué !";
			color = WIN;
		} else {
			title = "Game Over";
			color = LOSE;
		}
		String subtitle = !game.getSnake().isAliveSnake() ? "Le serpent a été détruit" : "Le serpent a atteint le bas";
		drawCentered(g, title, new Font(Font.SANS_SERIF, Font.BOLD, 48), color, Game.FRAMESIZE / 2 - 10);
		drawCentered(g, subtitle, new Font(Font.SANS_SERIF, Font.PLAIN, 18), TEXT, Game.FRAMESIZE / 2 + 30);
	}

	private void drawCentered(Graphics2D g, String text, Font font, Color color, int baseline) {
		g.setFont(font);
		g.setColor(color);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, (Game.FRAMESIZE - fm.stringWidth(text)) / 2, baseline);
	}

	public void drawPanel(Graphics2D g) { // Fonction qui dessine le plateau en damier (plus doux que des lignes blanches)
		g.setColor(BACKGROUND_ALT);
		for(int x = 0; x < Game.NBCELL; x++) {
			for(int y = 0; y < Game.NBCELL; y++) {
				if((x + y) % 2 == 0) {
					g.fillRect(x * CELL, y * CELL, CELL, CELL);
				}
			}
		}
	}

	public void drawSnake(Graphics2D g) { // Fonction qui dessine le serpent en le faisant glisser d'une case à l'autre
		boolean invincible = game.getSnake().isInvincible();
		Color bodyColor = SNAKE_BODY;
		Color headColor = SNAKE_HEAD;
		if(invincible) { // Invincible : le serpent devient cyan et clignote doucement
			double pulse = 0.5 + 0.5 * Math.sin(System.nanoTime() / 1e9 * 10);
			bodyColor = mix(SNAKE_INVINCIBLE, Color.WHITE, 0.35 * pulse);
			headColor = mix(SNAKE_INVINCIBLE, Color.WHITE, 0.5 * pulse);
		}
		List<Coordinate> body = game.getSnake().getSnakeBody();
		List<Coordinate> previous = game.getSnake().getPreviousBody();
		double pad = CELL * 0.08;
		for(int i = 0; i < body.size(); i++) {
			Coordinate to = body.get(i);
			Coordinate from = previousPosition(body, previous, i);
			double x = lerp(from.getX(), to.getX(), alpha) * CELL;
			double y = lerp(from.getY(), to.getY(), alpha) * CELL;
			boolean head = (i == body.size() - 1);
			g.setColor(head ? headColor : bodyColor);
			g.fill(new RoundRectangle2D.Double(x + pad, y + pad, CELL - 2 * pad, CELL - 2 * pad, CELL * 0.5, CELL * 0.5));
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
		double cx = x + CELL / 2.0 + dx * CELL * 0.18;
		double cy = y + CELL / 2.0 + dy * CELL * 0.18;
		double spread = CELL * 0.2;
		double r = CELL * 0.11;
		g.setColor(SNAKE_EYE);
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
		double x = canonX * CELL;
		double y = game.getPlayer().getCoordPlayers().getY() * CELL;
		g.setColor(CANON_DARK);
		g.fill(new RoundRectangle2D.Double(x + CELL * 0.36, y - CELL * 0.15, CELL * 0.28, CELL * 0.6, CELL * 0.2, CELL * 0.2));
		g.setColor(CANON);
		g.fill(new RoundRectangle2D.Double(x + CELL * 0.08, y + CELL * 0.35, CELL * 0.84, CELL * 0.6, CELL * 0.45, CELL * 0.45));
	}

	public void drawObstacle(Graphics2D g) { // Fonction qui dessine les différents obstacles avec une forme par type
		for(Obstacle obstacle : game.getObstacles()) {
			double x = obstacle.getCoordObstacle().getX() * CELL;
			double y = obstacle.getCoordObstacle().getY() * CELL;
			switch(obstacle.getNumberType()) {
				case 1: drawWood(g, x, y); break;
				case 2: drawStrawberry(g, x, y); break;
				case 3: drawBlueberry(g, x, y); break;
				case 4: drawGoldenCoin(g, x, y); break;
				default: break;
			}
		}
	}

	private void drawWood(Graphics2D g, double x, double y) { // Bois : une planche avec des veines
		double pad = CELL * 0.08;
		g.setColor(WOOD);
		g.fill(new RoundRectangle2D.Double(x + pad, y + pad, CELL - 2 * pad, CELL - 2 * pad, CELL * 0.25, CELL * 0.25));
		g.setColor(WOOD_GRAIN);
		g.setStroke(new BasicStroke((float) (CELL * 0.08), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.draw(new Line2D.Double(x + CELL * 0.25, y + CELL * 0.35, x + CELL * 0.75, y + CELL * 0.35));
		g.draw(new Line2D.Double(x + CELL * 0.3, y + CELL * 0.65, x + CELL * 0.7, y + CELL * 0.65));
	}

	private void drawStrawberry(Graphics2D g, double x, double y) { // Fraise : un rond rouge avec une feuille verte
		g.setColor(STRAWBERRY);
		g.fill(new Ellipse2D.Double(x + CELL * 0.14, y + CELL * 0.22, CELL * 0.72, CELL * 0.7));
		g.setColor(LEAF);
		g.fill(new Ellipse2D.Double(x + CELL * 0.3, y + CELL * 0.08, CELL * 0.4, CELL * 0.24));
		drawShine(g, x + CELL * 0.3, y + CELL * 0.38);
	}

	private void drawBlueberry(Graphics2D g, double x, double y) { // Myrtille : un rond bleu
		g.setColor(BLUEBERRY);
		g.fill(new Ellipse2D.Double(x + CELL * 0.16, y + CELL * 0.16, CELL * 0.68, CELL * 0.68));
		drawShine(g, x + CELL * 0.3, y + CELL * 0.3);
	}

	private void drawGoldenCoin(Graphics2D g, double x, double y) { // Pièce d'or : un disque avec un anneau
		g.setColor(GOLD_DARK);
		g.fill(new Ellipse2D.Double(x + CELL * 0.12, y + CELL * 0.12, CELL * 0.76, CELL * 0.76));
		g.setColor(GOLD);
		g.fill(new Ellipse2D.Double(x + CELL * 0.22, y + CELL * 0.22, CELL * 0.56, CELL * 0.56));
		drawShine(g, x + CELL * 0.3, y + CELL * 0.3);
	}

	private void drawShine(Graphics2D g, double x, double y) { // Petit reflet blanc pour donner du volume
		g.setColor(HIGHLIGHT);
		g.fill(new Ellipse2D.Double(x, y, CELL * 0.18, CELL * 0.18));
	}

	private void drawBullets(Graphics2D g) { // Fonction qui dessine les balles en les faisant monter en continu
		g.setColor(BULLET);
		double size = CELL * 0.4;
		for(Bullet bullet : game.getPlayer().getShots()){
			double x = lerp(bullet.getPreviousCoordBullet().getX(), bullet.getCoordBullet().getX(), alpha) * CELL;
			double y = lerp(bullet.getPreviousCoordBullet().getY(), bullet.getCoordBullet().getY(), alpha) * CELL;
			g.fill(new Ellipse2D.Double(x + (CELL - size) / 2, y + (CELL - size) / 2, size, size));
		}
	}

	private static Color mix(Color a, Color b, double t) { // Mélange de deux couleurs (t = 0 → a, t = 1 → b)
		return new Color(
				(int) Math.round(lerp(a.getRed(), b.getRed(), t)),
				(int) Math.round(lerp(a.getGreen(), b.getGreen(), t)),
				(int) Math.round(lerp(a.getBlue(), b.getBlue(), t)));
	}
}
