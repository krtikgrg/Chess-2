import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.*;
import javax.swing.*;
import java.net.*;

public class Pawn extends Piece {

	public boolean hasMoved = false;
	public boolean justenpassanted = false;
	
	public Pawn(Color col, boolean white, int x, int y) {
		this.color = col;
		this.x = x;
		this.y = y;
		this.icon = new ImageIcon(Piece.loadImage("pawn", color, 64, 64));
		setIcon(this.icon);
		setBorderPainted(false);
		setOpaque(true);

		int[][] pieceSquareConfigW = {
										{ 0,  0,  0,  0,  0,  0,  0,  0 },
										{ 50, 50, 50, 50, 50, 50, 50, 50 },
										{ 10, 10, 20, 30, 30, 20, 10, 10 },
										{ 5,  5, 10, 25, 25, 10,  5,  5 },
										{ 0,  0,  0, 20, 20,  0,  0,  0 },
										{ 5, -5,-10,  0,  0,-10, -5,  5 },
										{ 5, 10, 10,-20,-20, 10, 10,  5 },
										{ 0,  0,  0,  0,  0,  0,  0,  0 }
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

		if (color == Color.BLACK) {
			if (y + 1 > 7) {
				return newConfig;
			}
			//Moving down
			if (hasMoved) {
				if (boardLayout[y + 1][x] == 0) {
					newConfig[y + 1][x] = 1;
				}
			} else {
				if (y + 2 > 7) {
					return newConfig;
				}
				if (boardLayout[y + 1][x] == 0) {
					newConfig[y + 1][x] = 1;
				}
				if (boardLayout[y + 1][x] == 0 && boardLayout[y + 2][x] == 0) {
					newConfig[y + 2][x] = 1;
				}
			}
		} else if (color == Color.WHITE) {
			if (y - 1 < 0) {
				return newConfig;
			}
			//Moving up
			if (hasMoved) {
				if (boardLayout[y - 1][x] == 0) {
					newConfig[y - 1][x] = 1;
				}
			} else {
				if (y - 2 < 0) {
					return newConfig;
				}
				if (boardLayout[y - 1][x] == 0) {
					newConfig[y - 1][x] = 1;
				}
				if (boardLayout[y - 1][x] == 0 && boardLayout[y - 2][x] == 0) {
					newConfig[y - 2][x] = 1;
				}
			}
		}
		return newConfig;
	}

	public static int[][] getMoveConfig(int x, int y, int[][] boardLayout, Color color) {
		int[][] newConfig = new int[8][8];

		if (color == Color.BLACK) {
			if (y + 1 > 7) {
				return newConfig;
			}
			if (boardLayout[y + 1][x] == 0) {
				newConfig[y + 1][x] = 1;
			}
		} else if (color == Color.WHITE) {
			if (y - 1 < 0) {
				return newConfig;
			}
			if (boardLayout[y - 1][x] == 0) {
				newConfig[y - 1][x] = 1;
			}
		}
		return newConfig;
	}

	public int[][] getCaptureConfig(int x, int y, int[][] boardLayout, Board board) {
		int[][] newConfig = new int[8][8];

		if (color == Color.BLACK) {
			if (y + 1 < 8) {
				if (x - 1 >= 0 && boardLayout[y + 1][x - 1] != 0 && isDifferentColor(boardLayout[y + 1][x - 1])) {
					newConfig[y + 1][x - 1] = 1;
				}
				if (x + 1 < 8 && boardLayout[y + 1][x + 1] != 0 && isDifferentColor(boardLayout[y + 1][x + 1])) {
					newConfig[y + 1][x + 1] = 1;
				}
			}
		} else if (color == Color.WHITE) {
			if (y - 1 >= 0) {
				if (x - 1 >= 0 && boardLayout[y - 1][x - 1] != 0 && isDifferentColor(boardLayout[y - 1][x - 1])) {
					newConfig[y - 1][x - 1] = 1;
				}
				if (x + 1 < 8 && boardLayout[y - 1][x + 1] != 0 && isDifferentColor(boardLayout[y - 1][x + 1])) {
					newConfig[y - 1][x + 1] = 1;
				}
			}
		}
		return newConfig;
	}

	public static int[][] getCaptureConfig(int x, int y, int[][] boardLayout, Color color) {
		int[][] newConfig = new int[8][8];

		if (color == Color.BLACK) {
			if (y + 1 < 8) {
				if (x - 1 >= 0 && boardLayout[y + 1][x - 1] != 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y + 1][x - 1])) {
					newConfig[y + 1][x - 1] = 1;
				}
				if (x + 1 < 8 && boardLayout[y + 1][x + 1] != 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y + 1][x + 1])) {
					newConfig[y + 1][x + 1] = 1;
				}
			}
		} else if (color == Color.WHITE) {
			if (y - 1 >= 0) {
				if (x - 1 >= 0 && boardLayout[y - 1][x - 1] != 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y - 1][x - 1])) {
					newConfig[y - 1][x - 1] = 1;
				}
				if (x + 1 < 8 && boardLayout[y - 1][x + 1] != 0 && Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y - 1][x + 1])) {
					newConfig[y - 1][x + 1] = 1;
				}
			}
		}
		return newConfig;
	}

	public void setHasMoved() {
		if (!hasMoved) {
			hasMoved = true;
		}
	}

	public String toString() {
		return "Pawn";
	}

	public boolean iswhite() {
		return color==Color.WHITE;
	}

	public void addEnPassant(Piece otherPawn) {
		int modifier = 1;
		if (otherPawn.color == Color.BLACK) {
			modifier = -1;
		}
		this.captureConfig[otherPawn.y + modifier][otherPawn.x] = 1;
		this.justenpassanted = true;
	}
}