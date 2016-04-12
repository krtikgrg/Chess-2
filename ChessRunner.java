import java.awt.*;
import java.applet.*;
import javax.swing.*;

public class ChessRunner extends Applet {

	private Game game;
	private JScrollPane consolePane;
	public static Console console;
	
	public void init() {
		game = new Game(Boolean.parseBoolean(this.getParameter("singlePlayer")));
		console = new Console();
		consolePane = new JScrollPane(console);
		consolePane.setPreferredSize(new Dimension(200, 608));
		setLayout(new FlowLayout());
		add(consolePane);
		add(game.getBoard());
		System.out.println("Finished board...");
		validate();
		repaint();
		System.out.println("Creating ai...");
		game.getBoard().createAI();
		System.out.println("Done");
	}

	public Board getBoard() {
		return game.getBoard();
	}
}