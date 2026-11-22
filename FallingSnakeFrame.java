
import javax.swing.JFrame;

public class FallingSnakeFrame extends JFrame{

	/*
	 *  La classe Frame du jeu
	 */


	private static final long serialVersionUID = 1L;

	public FallingSnakeFrame(Game game, FallingSnakeView gameView){
		this.add(gameView);
		this.setTitle("Falling Snake");
		this.setResizable(false);
		this.addKeyListener(game.getPlayer());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack(); // On calcule la taille avant d'afficher la fenêtre
		this.setLocationRelativeTo(null); // Fenêtre centrée sur l'écran
		this.setVisible(true);
	}
}
