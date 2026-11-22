
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public abstract class Fruit {

	/*
	 * Classe abstraite d'un fruit (ou objet) posé sur le plateau
	 * Chaque type de fruit est une sous-classe qui définit :
	 * 			son effet quand le serpent le rencontre (applyEffect)
	 * 			s'il est mangé (il disparaît) ou s'il reste en place (isEatenBySnake)
	 * 			son dessin (draw)
	 * Pour ajouter un nouveau fruit, il suffit de créer une nouvelle sous-classe
	 * et de l'ajouter dans Game.randomFruit()
	 */

	private static final Color SHINE = new Color(255, 255, 255, 90);

	private final Coordinate coordFruit;

	protected Fruit(Coordinate coordFruit) {
		this.coordFruit = coordFruit;
	}

	public Coordinate getCoordFruit() {
		return coordFruit;
	}

	public boolean isEatenBySnake() { // Par défaut, le serpent mange le fruit et il disparaît du plateau
		return true;
	}

	/*
	 * Effet du fruit quand la tête du serpent arrive dessus
	 * current : la position actuelle de la tête
	 * next    : la case où la tête allait aller (celle du fruit)
	 * Retourne la case où la tête va finalement aller
	 */
	public abstract Coordinate applyEffect(Snake snake, Coordinate current, Coordinate next);

	/*
	 * Dessine le fruit dans la case dont le coin haut-gauche est (x, y), de côté cell pixels
	 */
	public abstract void draw(Graphics2D g, double x, double y, double cell);

	protected static void drawShine(Graphics2D g, double x, double y, double cell) { // Petit reflet blanc pour donner du volume
		g.setColor(SHINE);
		g.fill(new Ellipse2D.Double(x, y, cell * 0.18, cell * 0.18));
	}
}
