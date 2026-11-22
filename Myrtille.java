
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Myrtille extends Fruit {

	/*
	 * La myrtille rend le serpent invincible aux balles pendant quelques secondes
	 */

	private static final long DURATION_MS = 5000;

	private static final Color BLUE = new Color(0x6B, 0x8C, 0xFF);

	public Myrtille(Coordinate coord) {
		super(coord);
	}

	@Override
	public Coordinate applyEffect(Snake snake, Coordinate current, Coordinate next) {
		snake.makeInvincible(DURATION_MS);
		return next;
	}

	@Override
	public void draw(Graphics2D g, double x, double y, double cell) { // Un rond bleu
		g.setColor(BLUE);
		g.fill(new Ellipse2D.Double(x + cell * 0.16, y + cell * 0.16, cell * 0.68, cell * 0.68));
		drawShine(g, x + cell * 0.3, y + cell * 0.3, cell);
	}
}
