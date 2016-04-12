import javax.swing.*;
import java.awt.Dimension;
import javax.swing.table.DefaultTableModel;

public class Console extends JTable {

	DefaultTableModel model;

	public Console() {
		String[] header = {"Player", "Piece", "Move"};
		model = new DefaultTableModel();
		setModel(model);
		setPreferredSize(new Dimension(200, 608));
		setFillsViewportHeight(true);
		model.addColumn("Player");
		model.addColumn("Piece");
		model.addColumn("Move");
		validate();
	}

	public void log(String player, Piece p, String move) {
		String piece = "-";
		if (p != null) {
			piece = p.toString();
		}
		String[] row = {player, piece, move};
		model.addRow(row);
	}
}