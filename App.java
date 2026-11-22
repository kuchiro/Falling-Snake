
import javax.swing.SwingUtilities;

public class App {

	/*
	 * Point d'entrée du jeu : on ouvre la fenêtre sur le menu de choix de la difficulté
	 * (tout se passe sur le thread de l'interface Swing)
	 */

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new FallingSnakeFrame().showMenu());
	}
}
