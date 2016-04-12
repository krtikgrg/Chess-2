import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.*;
import javax.swing.*;
import java.net.*;

public class King extends Piece {

	public boolean canCastleKingSide = true;
	public boolean canCastleQueenSide = true;
	public boolean isCastled = false;

	public King(Color col, boolean white, int x, int y) {
		this.color = col;
		this.x = x;
		this.y = y;
		this.icon = new ImageIcon(Piece.loadImage("king", color, 64, 64));
		setIcon(this.icon);
		setBorderPainted(false);
		setOpaque(true);

		int[][] pieceSquareConfigW = {
										{ -30,-40,-40,-50,-50,-40,-40,-30 },
										{ -30,-40,-40,-50,-50,-40,-40,-30 },
										{ -30,-40,-40,-50,-50,-40,-40,-30 },
										{ -30,-40,-40,-50,-50,-40,-40,-30 },
										{ -20,-30,-30,-40,-40,-30,-30,-20 },
										{ -10,-20,-20,-20,-20,-20,-20,-10 },
										{ 20, 20,  0,  0,  0,  0, 20, 20 },
										{ 20, 30, 10,  0,  0, 10, 30, 20 }
									};

		super.pieceSquareConfigW = pieceSquareConfigW;

		int[][] pieceSquareConfigB = new int[8][8];

		for (int r = 0, r2 = 7 ; r < 8 ; r++, r2--) {
			for (int c = 0 ; c < 8 ; c++) {
				pieceSquareConfigB[r][c] = pieceSquareConfigW[r2][c];
			}
		}
		
		super.pieceSquareConfigB = pieceSquareConfigB;

		int[][] moveConfig = { {0, 0, 0, 0, 0, 0, 0, 0},
							 {0, 0, 0, 0, 0, 0, 0, 0},
							 {0, 0, 0, 0, 0, 0, 0, 0},
							 {0, 0, 0, 0, 0, 0, 0, 0},
							 {0, 0, 0, 0, 0, 0, 0, 0},
							 {0, 0, 0, 0, 0, 0, 0, 0},
							 {0, 0, 0, 0, 0, 0, 0, 0},
							 {0, 0, 0, 0, 0, 0, 0, 0}
						   };
		int[][] captureConfig = { {0, 0, 0, 0, 0, 0, 0, 0},
							    {0, 0, 0, 0, 0, 0, 0, 0},
							    {0, 0, 0, 0, 0, 0, 0, 0},
							    {0, 0, 0, 0, 0, 0, 0, 0},
							    {0, 0, 0, 0, 0, 0, 0, 0},
							    {0, 0, 0, 0, 0, 0, 0, 0},
							    {0, 0, 0, 0, 0, 0, 0, 0},
							    {0, 0, 0, 0, 0, 0, 0, 0}
							  };
		super.moveConfig = moveConfig;
		super.captureConfig = captureConfig;

		if (white) {
			setBackground(new Color(192, 192, 192));
		} else {
			setBackground(new Color(128, 128, 128));
		}
		setPreferredSize(new Dimension(80, 76));
	}

	public int[][] getCaptureConfig(int x, int y, int[][] boardLayout, Board board) {
		int[][] newConfig = new int[8][8];

		if (x - 1 >= 0) {
			if (isDifferentColor(boardLayout[y][x - 1])) {
				newConfig[y][x - 1] = 1;
			}
			if (y - 1 >= 0) {
				if (isDifferentColor(boardLayout[y - 1][x - 1])) {
					newConfig[y - 1][x - 1] = 1;
				}
				if (isDifferentColor(boardLayout[y - 1][x])) {
					newConfig[y - 1][x] = 1;
				}
			}
			if (y + 1 < 8) {
				if (isDifferentColor(boardLayout[y + 1][x - 1])) {
					newConfig[y + 1][x - 1] = 1;
				}
				if (isDifferentColor(boardLayout[y + 1][x])) {
					newConfig[y + 1][x] = 1;
				}
			}
		}
		if (x + 1 < 8) {
			if (isDifferentColor(boardLayout[y][x + 1])) {
				newConfig[y][x + 1] = 1;
			}
			if (y - 1 >= 0) {
				if (isDifferentColor(boardLayout[y - 1][x + 1])) {
					newConfig[y - 1][x + 1] = 1;
				}
			}
			if (y + 1 < 8) {
				if (isDifferentColor(boardLayout[y + 1][x + 1])) {
					newConfig[y + 1][x + 1] = 1;
				}
			}
		}
		return newConfig;
	}

	public int[][] getMoveConfig(int x, int y, int[][] boardLayout, Board board) {
		int[][] newConfig = new int[8][8];

		boolean white = this.color == Color.WHITE;
		if (canCastleKingSide && (x + 2 < 8) && board.canCastle(white, false)) {
			newConfig[y][x + 2] = 1;
		}
		if (canCastleQueenSide && (x - 2 >= 0) && board.canCastle(white, true)) {
			newConfig[y][x - 2] = 1;
		}

		if (x - 1 >= 0) {
			if (isDifferentColor(boardLayout[y][x - 1])) {
				newConfig[y][x - 1] = 1;
			}
			if (y - 1 >= 0) {
				if (isDifferentColor(boardLayout[y - 1][x - 1])) {
					newConfig[y - 1][x - 1] = 1;
				}
				if (isDifferentColor(boardLayout[y - 1][x])) {
					newConfig[y - 1][x] = 1;
				}
			}
			if (y + 1 < 8) {
				if (isDifferentColor(boardLayout[y + 1][x - 1])) {
					newConfig[y + 1][x - 1] = 1;
				}
				if (isDifferentColor(boardLayout[y + 1][x])) {
					newConfig[y + 1][x] = 1;
				}
			}
		}
		if (x + 1 < 8) {
			if (isDifferentColor(boardLayout[y][x + 1])) {
				newConfig[y][x + 1] = 1;
			}
			if (y - 1 >= 0) {
				if (isDifferentColor(boardLayout[y - 1][x + 1])) {
					newConfig[y - 1][x + 1] = 1;
				}
			}
			if (y + 1 < 8) {
				if (isDifferentColor(boardLayout[y + 1][x + 1])) {
					newConfig[y + 1][x + 1] = 1;
				}
			}
		}

		return newConfig;
	}

	public static int[][] getMoveConfig(int x, int y, int[][] boardLayout) {
		int[][] newConfig = new int[8][8];

		if (x - 1 >= 0) {
			if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y][x - 1])) {
				newConfig[y][x - 1] = 1;
			}
			if (y - 1 >= 0) {
				if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y - 1][x - 1])) {
					newConfig[y - 1][x - 1] = 1;
				}
				if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y - 1][x])) {
					newConfig[y - 1][x] = 1;
				}
			}
			if (y + 1 < 8) {
				if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y + 1][x - 1])) {
					newConfig[y + 1][x - 1] = 1;
				}
				if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y + 1][x])) {
					newConfig[y + 1][x] = 1;
				}
			}
		}
		if (x + 1 < 8) {
			if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y][x + 1])) {
				newConfig[y][x + 1] = 1;
			}
			if (y - 1 >= 0) {
				if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y - 1][x + 1])) {
					newConfig[y - 1][x + 1] = 1;
				}
			}
			if (y + 1 < 8) {
				if (Piece.pieceIsDifferentColor(boardLayout[y][x], boardLayout[y + 1][x + 1])) {
					newConfig[y + 1][x + 1] = 1;
				}
			}
		}

		return newConfig;
	}

	public int[][] getMoveConfig(int x, int y, int[][] boardLayout, Board board, boolean checkForCastle) {
		//System.out.println("Other move config method for king");
		int[][] newConfig = new int[8][8];

		boolean white = this.color == Color.WHITE;
		if (checkForCastle) {
			if (canCastleKingSide && (x + 2 < 8) && board.canCastle(white, false)) {
				newConfig[y][x + 2] = 1;
			}
			if (canCastleQueenSide && (x - 2 >= 0) && board.canCastle(white, true)) {
				newConfig[y][x - 2] = 1;
			}
		}

		if (x - 1 >= 0) {
			if (isDifferentColor(boardLayout[y][x - 1])) {
				newConfig[y][x - 1] = 1;
			}
			if (y - 1 >= 0) {
				if (isDifferentColor(boardLayout[y - 1][x - 1])) {
					newConfig[y - 1][x - 1] = 1;
				}
				if (isDifferentColor(boardLayout[y - 1][x])) {
					newConfig[y - 1][x] = 1;
				}
			}
			if (y + 1 < 8) {
				if (isDifferentColor(boardLayout[y + 1][x - 1])) {
					newConfig[y + 1][x - 1] = 1;
				}
				if (isDifferentColor(boardLayout[y + 1][x])) {
					newConfig[y + 1][x] = 1;
				}
			}
		}
		if (x + 1 < 8) {
			if (isDifferentColor(boardLayout[y][x + 1])) {
				newConfig[y][x + 1] = 1;
			}
			if (y - 1 >= 0) {
				if (isDifferentColor(boardLayout[y - 1][x + 1])) {
					newConfig[y - 1][x + 1] = 1;
				}
			}
			if (y + 1 < 8) {
				if (isDifferentColor(boardLayout[y + 1][x + 1])) {
					newConfig[y + 1][x + 1] = 1;
				}
			}
		}

		return newConfig;
	}

	public String toString() {
		return "King";
	}
}