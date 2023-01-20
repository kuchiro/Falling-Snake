
public class App {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
	
		Game game = new Game();
		
		FallingSnakeView gameView = new FallingSnakeView(game);
		
		FallingSnakeFrame gameFrame = new FallingSnakeFrame(game, gameView);
		
		while(!game.isGameEnd()){
			
			game.move();
			
		} 
	}
}

