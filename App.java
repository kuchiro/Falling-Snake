
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class App {

	/*
	 * Point d'entrée du jeu
	 * La boucle de jeu tourne dans un Timer Swing (sur le thread de l'interface) :
	 * 		- la logique avance d'un "tick" toutes les Game.TICK_MS millisecondes
	 * 		- l'affichage est redessiné environ 120 fois par seconde, en interpolant
	 * 		  les positions entre deux ticks pour que les déplacements soient fluides
	 */

	private static final int FRAME_MS = 8;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(App::start);
	}

	private static void start() {
		Game game = new Game();
		FallingSnakeView gameView = new FallingSnakeView(game);
		new FallingSnakeFrame(game, gameView);

		Timer loop = new Timer(FRAME_MS, new ActionListener() {
			private long last = System.nanoTime();
			private double accumulatedMs = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				long now = System.nanoTime();
				double dtMs = Math.min((now - last) / 1_000_000.0, 250); // on évite un énorme saut après une pause
				last = now;

				if (!game.isGameEnd()) {
					accumulatedMs += dtMs;
					while (accumulatedMs >= Game.TICK_MS && !game.isGameEnd()) { // on fait avancer la logique du jeu
						game.move();
						accumulatedMs -= Game.TICK_MS;
					}
				}
				double alpha = game.isGameEnd() ? 1.0 : accumulatedMs / Game.TICK_MS; // avancement entre deux ticks (0 → 1)
				gameView.nextFrame(alpha, dtMs / 1000.0);
			}
		});
		loop.setCoalesce(true);
		loop.start();
	}
}
