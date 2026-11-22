
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;
import javax.swing.JPanel;

public class MenuView extends JPanel {

	/*
	 * L'écran d'avant-partie : le joueur choisit la difficulté
	 * Flèches haut/bas ou souris pour choisir, Entrée / Espace ou clic pour lancer
	 */

	private static final long serialVersionUID = 1L;

	private static final int CARD_X = 90;
	private static final int CARD_W = Game.FRAMESIZE - 2 * CARD_X;
	private static final int CARD_H = 100;
	private static final int CARD_TOP = 180;
	private static final int CARD_GAP = 18;

	private final Difficulty[] levels = Difficulty.values();

	private final transient Consumer<Difficulty> onStart; // Ce qu'on fait quand le joueur a choisi

	private int selected = 0;

	public MenuView(Consumer<Difficulty> onStart) {
		this.onStart = onStart;
		this.setBackground(Palette.BACKGROUND);
		this.setPreferredSize(new Dimension(Game.FRAMESIZE, Game.FRAMESIZE));
		this.setFocusable(true);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_UP) {
					select((selected + levels.length - 1) % levels.length);
				} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					select((selected + 1) % levels.length);
				} else if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
					onStart.accept(levels[selected]);
				}
			}
		});

		MouseAdapter mouse = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int i = cardAt(e.getX(), e.getY());
				if(i >= 0) {
					select(i);
				}
				setCursor(Cursor.getPredefinedCursor(i >= 0 ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR)); // Main au survol d'une carte
			}

			@Override
			public void mouseReleased(MouseEvent e) { // Released plutôt que Clicked : un clic reste valide même si la souris bouge un peu
				int i = cardAt(e.getX(), e.getY());
				if(i >= 0 && e.getButton() == MouseEvent.BUTTON1) {
					onStart.accept(levels[i]);
				}
			}
		};
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
	}

	private void select(int i) {
		if(i != selected) {
			selected = i;
			repaint();
		}
	}

	private static int cardY(int i) {
		return CARD_TOP + i * (CARD_H + CARD_GAP);
	}

	private int cardAt(int x, int y) { // L'indice de la carte sous la souris, ou -1
		for(int i = 0; i < levels.length; i++) {
			if(x >= CARD_X && x <= CARD_X + CARD_W && y >= cardY(i) && y <= cardY(i) + CARD_H) {
				return i;
			}
		}
		return -1;
	}

	@Override
	protected void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		FallingSnakeView.drawCentered(g, "Falling Snake", new Font(Font.SANS_SERIF, Font.BOLD, 46), Palette.SNAKE_BODY, 100);
		FallingSnakeView.drawCentered(g, "Choisis ta difficulté", new Font(Font.SANS_SERIF, Font.PLAIN, 18), Palette.TEXT_MUTED, 140);

		for(int i = 0; i < levels.length; i++) {
			drawCard(g, levels[i], cardY(i), i == selected);
		}

		FallingSnakeView.drawCentered(g, "Clic ou ↑ ↓ + Entrée pour lancer  ·  en jeu : ← → bouger, Espace tirer",
				new Font(Font.SANS_SERIF, Font.PLAIN, 13), Palette.TEXT_MUTED, Game.FRAMESIZE - 30);
	}

	private void drawCard(Graphics2D g, Difficulty level, int y, boolean isSelected) {
		RoundRectangle2D card = new RoundRectangle2D.Double(CARD_X, y, CARD_W, CARD_H, 18, 18);
		g.setColor(isSelected ? Palette.CARD_SELECTED : Palette.CARD);
		g.fill(card);
		if(isSelected) {
			g.setColor(level.getColor());
			g.setStroke(new BasicStroke(3f));
			g.draw(card);
		}
		int textX = CARD_X + 24;
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		g.setColor(level.getColor());
		g.drawString(level.getLabel(), textX, y + 36);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		g.setColor(Palette.TEXT);
		g.drawString(level.getDescription(), textX, y + 62);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
		g.setColor(Palette.TEXT_MUTED);
		g.drawString("Plateau " + level.getNbCell() + "×" + level.getNbCell()
				+ "  ·  " + speedLabel(level) + "  ·  " + level.getSnakeLength() + " vies", textX, y + 84);
	}

	private static String speedLabel(Difficulty level) {
		if(level.getTickMs() >= 150) {
			return "Lent";
		} else if(level.getTickMs() >= 100) {
			return "Vitesse normale";
		}
		return "Rapide";
	}
}
