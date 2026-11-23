
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	 * 			l'écran de fin avec les boutons "Rejouer" et "Menu" (clic, Entrée ou Échap)
	 * 			le bouton pause et l'écran de pause avec "Reprendre" et "Menu" (clic, P ou Échap)
	 * 			un constructeur
	 */


	private static final long serialVersionUID = 1L;

	private final Game game;

	private final int cell; // Taille d'une case en pixels

	private double alpha; // Avancement entre le tick précédent (0) et le tick actuel (1)

	private double canonX; // Position affichée du canon, qui glisse vers sa vraie position
	
	// Boutons des écrans de fin et de pause
	// (le bouton de gauche est "Rejouer" en fin de partie et "Reprendre" en pause)
	private static final int BUTTON_W = 170;
	private static final int BUTTON_H = 48;
	private static final int BUTTON_GAP = 20;
	private static final int BUTTON_Y = Game.FRAMESIZE / 2 + 70;
	private static final RoundRectangle2D REPLAY_BUTTON = new RoundRectangle2D.Double(
			Game.FRAMESIZE / 2 - BUTTON_W - BUTTON_GAP / 2, BUTTON_Y, BUTTON_W, BUTTON_H, 16, 16);
	private static final RoundRectangle2D MENU_BUTTON = new RoundRectangle2D.Double(
			Game.FRAMESIZE / 2 + BUTTON_GAP / 2, BUTTON_Y, BUTTON_W, BUTTON_H, 16, 16);

	// Bouton pause, en haut à droite pendant la partie
	private static final int PAUSE_SIZE = 36;
	private static final RoundRectangle2D PAUSE_BUTTON = new RoundRectangle2D.Double(
			Game.FRAMESIZE - PAUSE_SIZE - 10, 10, PAUSE_SIZE, PAUSE_SIZE, 12, 12);

	private boolean hoverPause;
	
	private boolean hoverReplay;
	
	private boolean hoverMenu;
	
	/*
	 * onReplay : ce qu'on fait quand le joueur veut rejouer (même difficulté)
	 * onMenu   : ce qu'on fait quand le joueur veut revenir au menu
	 */
	public FallingSnakeView(Game game, Runnable onReplay, Runnable onMenu) {
		this.game = game;
		this.cell = game.getCellSize();
		this.canonX = game.getPlayer().getCoordPlayers().getX();
		this.setBackground(Palette.BACKGROUND);
		this.setPreferredSize(new Dimension(Game.FRAMESIZE, Game.FRAMESIZE));
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		
		this.addKeyListener(game.getPlayer()); // Le canon se déplace et tire avec le clavier
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if(game.isGameEnd()) { // En fin de partie : Entrée pour rejouer, Échap pour le menu
					if(key == KeyEvent.VK_ENTER) {
						onReplay.run();
					} else if(key == KeyEvent.VK_ESCAPE) {
						onMenu.run();
					}
				} else if(game.isPaused()) { // En pause : P, Échap ou Entrée pour reprendre
					if(key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_ENTER) {
						game.setPaused(false);
					}
				} else if(key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) { // En jeu : P ou Échap pour mettre en pause
					game.setPaused(true);
				}
			}
		});
		
		MouseAdapter mouse = new MouseAdapter() { // Clic sur le bouton pause, ou sur les boutons des écrans de pause / fin
			@Override
			public void mouseMoved(MouseEvent e) {
				boolean overlay = game.isGameEnd() || game.isPaused(); // Un écran (fin ou pause) est affiché
				hoverReplay = overlay && REPLAY_BUTTON.contains(e.getPoint());
				hoverMenu = overlay && MENU_BUTTON.contains(e.getPoint());
				hoverPause = !overlay && PAUSE_BUTTON.contains(e.getPoint());
				setCursor(Cursor.getPredefinedCursor(hoverReplay || hoverMenu || hoverPause ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				if(game.isGameEnd() || game.isPaused()) {
					if(REPLAY_BUTTON.contains(e.getPoint())) {
						if(game.isGameEnd()) {
							onReplay.run();
						} else {
							game.setPaused(false); // "Reprendre"
						}
					} else if(MENU_BUTTON.contains(e.getPoint())) {
						onMenu.run();
					}
				} else if(PAUSE_BUTTON.contains(e.getPoint())) {
					game.setPaused(true);
				}
				mouseMoved(e); // Met à jour le survol et le curseur pour le nouvel écran
			}
		};
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
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
		} else if(game.isPaused()) {
			drawPause(g);
		} else {
			drawPauseButton(g);
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
		drawButton(g, REPLAY_BUTTON, "Rejouer", hoverReplay, true);
		drawButton(g, MENU_BUTTON, "Menu", hoverMenu, false);
		drawCentered(g, "Entrée : rejouer  ·  Échap : menu", new Font(Font.SANS_SERIF, Font.PLAIN, 14), Palette.TEXT_MUTED, BUTTON_Y + BUTTON_H + 40);
	}
	
	public void drawPause(Graphics2D g) { // Écran de pause, par-dessus le plateau figé
		g.setColor(Palette.OVERLAY);
		g.fillRect(0, 0, Game.FRAMESIZE, Game.FRAMESIZE);
		drawCentered(g, "Pause", new Font(Font.SANS_SERIF, Font.BOLD, 48), Palette.TEXT, Game.FRAMESIZE / 2 - 10);
		drawCentered(g, "Niveau " + game.getDifficulty().getLabel(), new Font(Font.SANS_SERIF, Font.PLAIN, 18), game.getDifficulty().getColor(), Game.FRAMESIZE / 2 + 30);
		drawButton(g, REPLAY_BUTTON, "Reprendre", hoverReplay, true);
		drawButton(g, MENU_BUTTON, "Menu", hoverMenu, false);
		drawCentered(g, "P ou Échap : reprendre", new Font(Font.SANS_SERIF, Font.PLAIN, 14), Palette.TEXT_MUTED, BUTTON_Y + BUTTON_H + 40);
	}

	private void drawPauseButton(Graphics2D g) { // Petit bouton avec deux barres, en haut à droite
		g.setColor(hoverPause ? Palette.CARD_SELECTED : Palette.PAUSE_BUTTON);
		g.fill(PAUSE_BUTTON);
		g.setColor(hoverPause ? Palette.TEXT : Palette.TEXT_MUTED);
		double barW = PAUSE_SIZE * 0.14;
		double barH = PAUSE_SIZE * 0.42;
		double top = PAUSE_BUTTON.getY() + (PAUSE_SIZE - barH) / 2;
		double centerX = PAUSE_BUTTON.getX() + PAUSE_SIZE / 2.0;
		g.fill(new RoundRectangle2D.Double(centerX - barW * 1.6, top, barW, barH, barW, barW));
		g.fill(new RoundRectangle2D.Double(centerX + barW * 0.6, top, barW, barH, barW, barW));
	}

	private void drawButton(Graphics2D g, RoundRectangle2D button, String label, boolean hover, boolean primary) {
		Color accent = game.getDifficulty().getColor();
		if(primary) { // Bouton principal : rempli avec la couleur du niveau
			g.setColor(hover ? Palette.mix(accent, Color.WHITE, 0.2) : accent);
			g.fill(button);
			g.setColor(Palette.BACKGROUND);
		} else { // Bouton secondaire : juste un contour
			g.setColor(hover ? Palette.CARD_SELECTED : Palette.CARD);
			g.fill(button);
			g.setColor(hover ? accent : Palette.TEXT_MUTED);
			g.setStroke(new BasicStroke(2f));
			g.draw(button);
			g.setColor(Palette.TEXT);
		}
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		FontMetrics fm = g.getFontMetrics();
		int textX = (int) (button.getX() + (button.getWidth() - fm.stringWidth(label)) / 2);
		int textY = (int) (button.getY() + (button.getHeight() + fm.getAscent() - fm.getDescent()) / 2);
		g.drawString(label, textX, textY);
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
