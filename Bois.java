
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

public class Bois extends Fruit {

	/*
	 * Le bois est un obstacle : le serpent ne le mange pas,
	 * il descend d'une ligne et repart dans l'autre sens (comme sur un bord du plateau)
	 */

	private static final Color WOOD = new Color(0xC9, 0x93, 0x63);

	private static final Color GRAIN = new Color(0x93, 0x63, 0x3E);

	public Bois(Coordinate coord) {
		super(coord);
	}

	@Override
	public boolean isEatenBySnake() { // Le bois reste sur le plateau
		return false;
	}

	@Override
	public Coordinate applyEffect(Snake snake, Coordinate current, Coordinate next) {
		if(snake.isMovingHorizontally()) {
			return snake.turnDown(current);
		}
		return next; // Si le serpent descendait déjà, il passe sur le bois
	}

	@Override
	public void draw(Graphics2D g, double x, double y, double cell) { // Une planche avec des veines
		double pad = cell * 0.08;
		g.setColor(WOOD);
		g.fill(new RoundRectangle2D.Double(x + pad, y + pad, cell - 2 * pad, cell - 2 * pad, cell * 0.25, cell * 0.25));
		g.setColor(GRAIN);
		g.setStroke(new BasicStroke((float) (cell * 0.08), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.draw(new Line2D.Double(x + cell * 0.25, y + cell * 0.35, x + cell * 0.75, y + cell * 0.35));
		g.draw(new Line2D.Double(x + cell * 0.3, y + cell * 0.65, x + cell * 0.7, y + cell * 0.65));
	}
}
