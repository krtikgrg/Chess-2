import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.*;
import javax.swing.*;
import java.net.*;

public class Rook extends Piece {
	
	public Rook(Color col, boolean white, int x, int y) {
		this.color = col;
		this.x = x;
		this.y = y;
		this.icon = new ImageIcon(Piece.loadImage("rook", color, 64, 64));
		setIcon(this.icon);
		setBorderPainted(false);
		setOpaque(true);

		int[][] pieceSquareConfigW = {
										{ 0,  0,  0,  0,  0,  0,  0,  0 },
										{ 5, 10, 10, 10, 10, 10, 10,  5 },
										{ -5,  0,  0,  0,  0,  0,  0, -5 },
										{ -5,  0,  0,  0,  0,  0,  0, -5 },
										{ -5,  0,  0,  0,  0,  0,  0, -5 },
										{ -5,  0,  0,  0,  0,  0,  0, -5 },
										{ -5,  0,  0,  0,  0,  0,  0, -5 },
										{  0,  -50,  0,  5,  5,  0,  -50,  0 }
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

	public int[][] getMoveConfig(int x, int y, int[][] boardLayout, Board board) {
		int[][] newConfig = new int[8][8];

		int modX = x - 1;

		//left
		while (modX >= 0 && boardLayout[y][modX] == 0) {
			if (isDifferentColor(boardLayout[y][modX])) {
				newConfig[y][modX] = 1;
			}

			modX -= 1;
		}
		if (modX >= 0 && isDifferentColor(boardLayout[y][modX])) { 
			newConfig[y][modX] = 1;
		}

		modX = x + 1;

		//right
		while (modX < 8 && boardLayout[y][modX] == 0) {
			if (isDifferentColor(boardLayout[y][modX])) {
				newConfig[y][modX] = 1;
			}

			modX += 1;
		}
		if (modX < 8 && isDifferentColor(boardLayout[y][modX])) { 
			newConfig[y][modX] = 1;
		}

		int modY = y - 1;

		//top
		while (modY >= 0 && boardLayout[modY][x] == 0) {
			if (isDifferentColor(boardLayout[modY][x])) {
				newConfig[modY][x] = 1;
			}

			modY -= 1;
		}
		if (modY >= 0 && isDifferentColor(boardLayout[modY][x])) {
			newConfig[modY][x] = 1;
		}

		modY = y + 1;

		//bottom
		while (modY < 8 && boardLayout[modY][x] == 0) {
			if (isDifferentColor(boardLayout[modY][x])) {
				newConfig[modY][x] = 1;
			}

			modY += 1;
		}
		if (modY < 8 && isDifferentColor(boardLayout[modY][x])) {
			newConfig[modY][x] = 1;
		}

		// System.out.println("X: " + x + " Y: " + y);
		// for (int c = 0 ; c < 8 ; c++) {
		// 	for (int r = 0 ; r < 8 ; r++) {
		// 		System.out.print(newConfig[c][r] + " ");
		// 	}
		// 	System.out.println("");
		// }

		return newConfig;
	}

	public static int[][] getMoveConfig(int x, int y, int[][] boardLayout) {
		int[][] newConfig = new int[8][8];

		int modX = x - 1;

		//left
		while (modX >= 0 && boardLayout[y][modX] == 0) {
			if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y][modX])) {
				newConfig[y][modX] = 1;
			}

			modX -= 1;
		}
		if (modX >= 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y][modX])) { 
			newConfig[y][modX] = 1;
		}

		modX = x + 1;

		//right
		while (modX < 8 && boardLayout[y][modX] == 0) {
			if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y][modX])) {
				newConfig[y][modX] = 1;
			}

			modX += 1;
		}
		if (modX < 8 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y][modX])) { 
			newConfig[y][modX] = 1;
		}

		int modY = y - 1;

		//top
		while (modY >= 0 && boardLayout[modY][x] == 0) {
			if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[modY][x])) {
				newConfig[modY][x] = 1;
			}

			modY -= 1;
		}
		if (modY >= 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[modY][x])) {
			newConfig[modY][x] = 1;
		}

		modY = y + 1;

		//bottom
		while (modY < 8 && boardLayout[modY][x] == 0) {
			if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[modY][x])) {
				newConfig[modY][x] = 1;
			}

			modY += 1;
		}
		if (modY < 8 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[modY][x])) {
			newConfig[modY][x] = 1;
		}

		return newConfig;
	}

	public String toString() {
		return "Rook";
	}
}