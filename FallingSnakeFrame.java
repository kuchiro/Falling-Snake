
import javax.swing.JFrame;

public class FallingSnakeFrame extends JFrame{
	
	/*
	 *  La classe Frame du jeu
	 */
	
	
	private static final long serialVersionUID = 1L;

	public FallingSnakeFrame(Game game, FallingSnakeView gameView){	
		this.add(gameView); 
		this.setTitle("Falling Snake");
		this.setLocation(50, 50);
		this.setResizable(false);
		this.setVisible(true);
		this.pack();
		this.addKeyListener(game.getPlayer());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
