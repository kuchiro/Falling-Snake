
import java.awt.Color;

public enum Difficulty {

	/*
	 * Les niveaux de difficulté, choisis dans le menu avant la partie
	 * Chaque niveau fixe :
	 * 			le nombre de cases du plateau (la fenêtre garde la même taille, ce sont les cases qui rétrécissent)
	 * 			la durée d'un tour en millisecondes (plus c'est petit, plus le serpent va vite)
	 * 			la longueur du serpent au départ (= son nombre de vies)
	 */

	FACILE   ("Facile",    15, 150, 3, "Petit plateau, serpent lent et court",   new Color(0x4A, 0xDE, 0x80)),
	DIFFICILE("Difficile", 20, 100, 5, "Plateau moyen, vitesse normale",          new Color(0xFF, 0xB0, 0x4D)),
	EXTREME  ("Extrême",   30,  75, 8, "Grand plateau, serpent rapide et long",  new Color(0xFF, 0x5C, 0x7A));

	private final String label;

	private final int nbCell;

	private final int tickMs;

	private final int snakeLength;

	private final String description;

	private final Color color;

	Difficulty(String label, int nbCell, int tickMs, int snakeLength, String description, Color color) {
		this.label = label;
		this.nbCell = nbCell;
		this.tickMs = tickMs;
		this.snakeLength = snakeLength;
		this.description = description;
		this.color = color;
	}

	public String getLabel() {
		return label;
	}

	public int getNbCell() {
		return nbCell;
	}

	public int getTickMs() {
		return tickMs;
	}

	public int getSnakeLength() {
		return snakeLength;
	}

	public String getDescription() {
		return description;
	}

	public Color getColor() {
		return color;
	}
}
