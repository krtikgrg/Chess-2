import java.awt.*;
import javax.swing.*;

public class Game {

	private Board board;
	private Player white;
	private Player black;

	public Game(boolean isSinglePlayer) {
		board = new Board(isSinglePlayer);
		white = new Player("White");
		black = new Player("Black");
	}

	public Board getBoard() {
		return this.board;
	}
}