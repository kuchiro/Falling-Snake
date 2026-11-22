
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

public class FallingSnakeFrame extends JFrame{

	/*
	 *  La classe Frame du jeu
	 *  Affiche soit le menu (choix de la difficulté), soit une partie
	 *  et fait tourner la boucle de jeu pendant une partie :
	 *  		- la logique avance d'un "tick" toutes les game.getTickMs() millisecondes
	 *  		- l'affichage est redessiné environ 120 fois par seconde, en interpolant
	 *  		  les positions entre deux ticks pour que les déplacements soient fluides
	 */

	private static final long serialVersionUID = 1L;

	private static final int FRAME_MS = 8;

	private transient Timer loop;

	public FallingSnakeFrame(){
		this.setTitle("Falling Snake");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void showMenu() { // Écran d'avant-partie
		stopLoop();
		setScreen(new MenuView(this::startGame));
	}

	public void startGame(Difficulty difficulty) { // Lance une partie avec la difficulté choisie
		stopLoop();
		Game game = new Game(difficulty);
		FallingSnakeView gameView = new FallingSnakeView(game, () -> startGame(difficulty), this::showMenu);
		setScreen(gameView);
		startLoop(game, gameView);
	}

	private void setScreen(JComponent screen) { // Remplace le contenu de la fenêtre
		getContentPane().removeAll();
		add(screen);
		pack();
		if(!isVisible()) {
			setLocationRelativeTo(null); // Fenêtre centrée sur l'écran
			setVisible(true);
		}
		revalidate();
		repaint();
		screen.requestFocusInWindow(); // Pour que l'écran reçoive les touches du clavier
	}

	private void startLoop(Game game, FallingSnakeView gameView) {
		loop = new Timer(FRAME_MS, null);
		loop.addActionListener(new java.awt.event.ActionListener() {
			private long last = System.nanoTime();
			private double accumulatedMs = 0;

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				long now = System.nanoTime();
				double dtMs = Math.min((now - last) / 1_000_000.0, 250); // on évite un énorme saut après une pause
				last = now;

				if (!game.isGameEnd()) {
					accumulatedMs += dtMs;
					while (accumulatedMs >= game.getTickMs() && !game.isGameEnd()) { // on fait avancer la logique du jeu
						game.move();
						accumulatedMs -= game.getTickMs();
					}
				}
				double alpha = game.isGameEnd() ? 1.0 : accumulatedMs / game.getTickMs(); // avancement entre deux ticks (0 → 1)
				gameView.nextFrame(alpha, dtMs / 1000.0);
			}
		});
		loop.setCoalesce(true);
		loop.start();
	}

	private void stopLoop() {
		if(loop != null) {
			loop.stop();
			loop = null;
		}
	}
}
