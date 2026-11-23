
import java.awt.Color;

public final class Palette {

	/*
	 * Les couleurs communes à tous les écrans du jeu
	 * Fond bleu nuit doux (pas de noir pur), couleurs vives mais pas fluo
	 * (les couleurs propres à chaque fruit sont dans la classe du fruit)
	 */

	private Palette() {}

	static final Color BACKGROUND       = new Color(0x1B, 0x1F, 0x2E);
	static final Color BACKGROUND_ALT   = new Color(0x21, 0x26, 0x38); // case sur deux, damier discret
	static final Color CARD             = new Color(0x25, 0x2A, 0x3E);
	static final Color CARD_SELECTED    = new Color(0x2E, 0x34, 0x4D);
	static final Color TEXT             = new Color(0xF1, 0xF5, 0xF9);
	static final Color TEXT_MUTED       = new Color(0x94, 0xA3, 0xB8);
	static final Color SNAKE_BODY       = new Color(0x4A, 0xDE, 0x80);
	static final Color SNAKE_HEAD       = new Color(0x86, 0xEF, 0xAC);
	static final Color SNAKE_INVINCIBLE = new Color(0x67, 0xE8, 0xF9);
	static final Color SNAKE_EYE        = new Color(0x1B, 0x1F, 0x2E);
	static final Color CANON            = new Color(0xFF, 0x8A, 0x65);
	static final Color CANON_DARK       = new Color(0xE0, 0x62, 0x44);
	static final Color BULLET           = new Color(0xFF, 0xE0, 0x82);
	static final Color OVERLAY          = new Color(0x1B, 0x1F, 0x2E, 215);
	static final Color PAUSE_BUTTON     = new Color(0x25, 0x2A, 0x3E, 200); // semi-transparent pour laisser voir le plateau
	static final Color WIN              = new Color(0x4A, 0xDE, 0x80);
	static final Color LOSE             = new Color(0xFF, 0x5C, 0x7A);

	static Color mix(Color a, Color b, double t) { // Mélange de deux couleurs (t = 0 → a, t = 1 → b)
		return new Color(
				(int) Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t),
				(int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
				(int) Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
	}
}
