import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.*;
import javax.swing.*;
import java.net.*;

public class Knight extends Piece {
	
	public Knight(Color col, boolean white, int x, int y) {
		this.color = col;
		this.x = x;
		this.y = y;
		this.icon = new ImageIcon(Piece.loadImage("knight", color, 64, 64));
		setIcon(this.icon);
		setBorderPainted(false);
		setOpaque(true);

		int[][] pieceSquareConfigW = {
										{ -50,-40,-30,-30,-30,-30,-40,-50 },
										{ -40,-20,  0,  0,  0,  0,-20,-40 },
										{ -30,  0, 10, 15, 15, 10,  0,-30 },
										{ -30,  5, 15, 20, 20, 15,  5,-30 },
										{ -30,  0, 15, 20, 20, 15,  0,-30 },
										{ -30,  5, 10, 15, 15, 10,  5,-30 },
										{ -40,-20,  0,  0,  0,  0,-20,-40 },
										{ -50,-40,-30,-30,-30,-30,-40,-50 }
									};

		super.pieceSquareConfigW = pieceSquareConfigW;

		int[][] pieceSquareConfigB = new int[8][8];

		for (int r = 0, r2 = 7 ; r < 8 ; r++, r2--) {
			for (int c = 0 ; c < 8 ; c++) {
				pieceSquareConfigB[r][c] = pieceSquareConfigW[r2][c];
			}
		}
		
		super.pieceSquareConfigB = pieceSquareConfigB;

		if (white) {
			setBackground(new Color(192, 192, 192));
		} else {
			setBackground(new Color(128, 128, 128));
		}
		setPreferredSize(new Dimension(80, 76));
	}

	public static int[][] getMoveConfig(int x, int y, int[][] boardLayout) {
		int[][] newConfig = new int[8][8];

		if (x - 2 >= 0) {
			if (y - 1 >= 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y - 1][x - 2])) {
				newConfig[y - 1][x - 2] = 1;
			}
			if (y + 1 < 8 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y + 1][x - 2])) {
				newConfig[y + 1][x - 2] = 1;
			}
		}
		if (x + 2 < 8) {
			if (y - 1 >= 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y - 1][x + 2])) {
				newConfig[y - 1][x + 2] = 1;
			}
			if (y + 1 < 8 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y + 1][x + 2])) {
				newConfig[y + 1][x + 2] = 1;
			}
		}
		if (y - 2 >= 0) {
			if (x - 1 >= 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y - 2][x - 1])) {
				newConfig[y - 2][x - 1] = 1;
			}
			if (x + 1 < 8 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y - 2][x + 1])) {
				newConfig[y - 2][x + 1] = 1;
			}
		}
		if (y + 2 < 8) {
			if (x - 1 >= 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y + 2][x - 1])) {
				newConfig[y + 2][x - 1] = 1;
			}
			if (x + 1 < 8 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y + 2][x + 1])) {
				newConfig[y + 2][x + 1] = 1;
			}
		}

		return newConfig;
	}

	public int[][] getMoveConfig(int x, int y, int[][] boardLayout, Board board) {
		int[][] newConfig = new int[8][8];

		if (x - 2 >= 0) {
			if (y - 1 >= 0 && isDifferentColor(boardLayout[y - 1][x - 2])) {
				newConfig[y - 1][x - 2] = 1;
			}
			if (y + 1 < 8 && isDifferentColor(boardLayout[y + 1][x - 2])) {
				newConfig[y + 1][x - 2] = 1;
			}
		}
		if (x + 2 < 8) {
			if (y - 1 >= 0 && isDifferentColor(boardLayout[y - 1][x + 2])) {
				newConfig[y - 1][x + 2] = 1;
			}
			if (y + 1 < 8 && isDifferentColor(boardLayout[y + 1][x + 2])) {
				newConfig[y + 1][x + 2] = 1;
			}
		}
		if (y - 2 >= 0) {
			if (x - 1 >= 0 && isDifferentColor(boardLayout[y - 2][x - 1])) {
				newConfig[y - 2][x - 1] = 1;
			}
			if (x + 1 < 8 && isDifferentColor(boardLayout[y - 2][x + 1])) {
				newConfig[y - 2][x + 1] = 1;
			}
		}
		if (y + 2 < 8) {
			if (x - 1 >= 0 && isDifferentColor(boardLayout[y + 2][x - 1])) {
				newConfig[y + 2][x - 1] = 1;
			}
			if (x + 1 < 8 && isDifferentColor(boardLayout[y + 2][x + 1])) {
				newConfig[y + 2][x + 1] = 1;
			}
		}

		return newConfig;
	}

	public String toString() {
		return "Knight";
	}
}