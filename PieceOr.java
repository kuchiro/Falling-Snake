
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class PieceOr extends Fruit {

	/*
	 * La pièce d'or remplace tous les fruits du plateau par de nouveaux, tirés au hasard
	 */

	private static final Color GOLD = new Color(0xFF, 0xCB, 0x47);

	private static final Color GOLD_DARK = new Color(0xD9, 0x9B, 0x1F);

	private final Game game;

	public PieceOr(Game game, Coordinate coord) {
		super(coord);
		this.game = game;
	}

	@Override
	public Coordinate applyEffect(Snake snake, Coordinate current, Coordinate next) {
		game.spawnFruits();
		return next;
	}

	@Override
	public void draw(Graphics2D g, double x, double y, double cell) { // Un disque doré avec un anneau
		g.setColor(GOLD_DARK);
		g.fill(new Ellipse2D.Double(x + cell * 0.12, y + cell * 0.12, cell * 0.76, cell * 0.76));
		g.setColor(GOLD);
		g.fill(new Ellipse2D.Double(x + cell * 0.22, y + cell * 0.22, cell * 0.56, cell * 0.56));
		drawShine(g, x + cell * 0.3, y + cell * 0.3, cell);
	}
}
