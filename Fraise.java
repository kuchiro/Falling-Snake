
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Fraise extends Fruit {

	/*
	 * La fraise fait grandir le serpent d'un morceau (donc une vie de plus)
	 */

	private static final Color RED = new Color(0xFF, 0x5C, 0x7A);

	private static final Color LEAF = new Color(0x3D, 0xC9, 0x6B);

	public Fraise(Coordinate coord) {
		super(coord);
	}

	@Override
	public Coordinate applyEffect(Snake snake, Coordinate current, Coordinate next) {
		snake.grow();
		return next;
	}

	@Override
	public void draw(Graphics2D g, double x, double y, double cell) { // Un rond rouge avec une feuille verte
		g.setColor(RED);
		g.fill(new Ellipse2D.Double(x + cell * 0.14, y + cell * 0.22, cell * 0.72, cell * 0.7));
		g.setColor(LEAF);
		g.fill(new Ellipse2D.Double(x + cell * 0.3, y + cell * 0.08, cell * 0.4, cell * 0.24));
		drawShine(g, x + cell * 0.3, y + cell * 0.38, cell);
	}
}
