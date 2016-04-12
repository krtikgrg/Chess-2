import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PromotionPanel extends JPanel implements ActionListener {

	GridBagConstraints gbc;
	int px;
	int py;

	public PromotionPanel(Color color, Piece p) {
		GridBagLayout layout = new GridBagLayout();
		this.gbc = new GridBagConstraints();
		this.px = p.x;
		this.py = p.y;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		setLayout(layout);

		Rook rook = new Rook(color, true, 0, 0);
		Bishop bishop = new Bishop(color, true, 0, 0);
		Knight knight = new Knight(color, true, 0, 0);
		Queen queen = new Queen(color, true, 0, 0);

		rook.addActionListener(this);
		bishop.addActionListener(this);
		knight.addActionListener(this);
		queen.addActionListener(this);

		gbc.gridx = 0;
		gbc.gridy = 0;

		add(queen, gbc);

		gbc.gridx = 1;

		add(knight, gbc);

		gbc.gridy = 1;

		add(bishop, gbc);

		gbc.gridx = 0;

		add(rook, gbc);

		validate();
	}

	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		ChessRunner runner = (ChessRunner) getParent();
		Piece clicked = null;
		Piece promoted = (Piece) runner.getBoard().getComponentAt(px * 80, py * 76);

		if (source instanceof Piece) {
			clicked = (Piece) source;
		}

		if (clicked != null) {
			runner.getBoard().gbc.gridx = promoted.x;
			runner.getBoard().gbc.gridy = promoted.y;
			runner.getBoard().remove(promoted);
			clicked.addActionListener(runner.getBoard());
			clicked.removeActionListener(this);
			clicked.x = promoted.x;
			clicked.y = promoted.y;
			clicked.setBackground(promoted.getBackground());
			clicked.setMoveConfig(promoted.x, promoted.y, clicked.getMoveConfig(promoted.x, promoted.y, runner.getBoard().boardLayout, runner.getBoard()), runner.getBoard().boardLayout, runner.getBoard());
			runner.getBoard().add(clicked, runner.getBoard().gbc);
			removeAll();
			revalidate();
			runner.repaint();
			runner.getBoard().globallock = false;
			runner.getBoard().addPieceToAI(clicked);
			runner.remove(this);
			runner.validate();
		}
	}
}