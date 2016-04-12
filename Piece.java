import java.awt.Color;
import java.net.URL;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Piece extends JButton {
	public ImageIcon icon;
	public Color color;
	public int[][] moveConfig = new int[8][8];
	public int[][] captureConfig = new int[8][8];
	public int[][] pieceSquareConfigW = new int[8][8];
	public int[][] pieceSquareConfigB = new int[8][8];
	public int x;
	public int y;
	public boolean iswhite;

	public static Image loadImage(String piece, Color color, int width, int height) {
		String path = null;
		Image image = null;
		Image scaled = null;

		try {
			if (color == Color.WHITE) {
				path = piece.toLowerCase() + "_white.png";
			} else {
				path = piece.toLowerCase() + "_black.png";
			}
			image = ImageIO.read(new File(path));
			scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			System.out.println("Could not load piece image at path: " + path);
			e.printStackTrace();
		}

		return scaled;
	}

	public boolean captureConfigContains(int x, int y) {
		return (captureConfig[y][x] == 1);
	}

	public ArrayList<Piece> getPiecesTargeted(Piece justMoved, int x, int y, int[][] boardLayout, Board board) {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (int r = 0 ; r < 8 ; r++) {
			for (int c = 0 ; c < 8 ; c++) {
				int[][] moveConfig = this.moveConfig;
				int[][] captureConfig = this.captureConfig;
				if (this instanceof King) {
					King k = (King) this;
					this.setMoveConfig(x, y, k.getMoveConfig(x, y, boardLayout, board, false), boardLayout, board); //Change move/capture config to what the board WILL look like
				} else {
					this.setMoveConfig(x, y, this.getMoveConfig(x, y, boardLayout, board), boardLayout, board); //Change move/capture config to what the board WILL look like
				}
				if (this.captureConfig[r][c] == 1) {
					//Only checking spots in capture config
					if (boardLayout[r][c] != 0 && board.getComponentAt(c * 80, r * 76) instanceof Piece) {
						this.setMoveAndCaptureConfig(x, y, moveConfig, captureConfig); //Revert back to original move/capture config
						Piece p = (Piece) board.getComponentAt(c * 80, r * 76);
						if (p.color != this.color) {
							pieces.add(p);
						}
					} else if (boardLayout[r][c] != 0 && justMoved.x == c && justMoved.y == r) {
						this.setMoveAndCaptureConfig(x, y, moveConfig, captureConfig); //Revert back to original move/capture config
						if (justMoved.color != this.color) {
							pieces.add(justMoved);
						}
					}
				}
			}
		}
		return pieces;
	}

	public void setMoveConfig(int x, int y, int[][] newConfig, int[][] boardLayout, Board board) {
		this.moveConfig = newConfig;
		this.captureConfig = getCaptureConfig(x, y, boardLayout, board);
	}

	public void setMoveAndCaptureConfig(int x, int y, int[][] newConfig, int[][] newCaptureConfig) {
		this.moveConfig = newConfig;
		this.captureConfig = newCaptureConfig;
	}

	public int[][] getMoveConfig(int x, int y, int[][] boardLayout, Board board) {
		return new int[8][8];
	}

	public int[][] getCaptureConfig(int x, int y, int[][] boardLayout, Board board) {
		return getMoveConfig(x, y, boardLayout, board);
	}

	public void printCaptureConfig() {
		for (int c = 0 ; c < 8 ; c++) {
			for (int r = 0 ; r < 8 ; r++) {
				System.out.print(captureConfig[c][r] + " ");
			}
			System.out.println("");
		}
	}

	public static boolean pieceIsDifferentColor(int compare, int numRep) {
		if (numRep == 0) {
			return true;
		}
		if (compare > 10 && numRep < 10) {
			return false;
		}
		if (compare < 10 && numRep > 10) {
			return false;
		}
		return true;
	}

	public boolean isDifferentColor(int numRep) {
		if (numRep == 0) {
			return true;
		}
		if (color == Color.WHITE && numRep < 10) {
			return false;
		}
		if (color == Color.BLACK && numRep > 10) {
			return false;
		}
		return true;
	}

	public static int[][] getMoveConfig(int pieceNum, int[][] layout, int x, int y) {
		Color c = Color.WHITE;
		if (pieceNum > 10) {
			pieceNum = pieceNum % 10;
			c = Color.BLACK;
		}
		switch (pieceNum) {
			case 1:
				return King.getMoveConfig(x, y, layout);
			case 2:
				return Queen.getMoveConfig(x, y, layout);
			case 3:
				return Bishop.getMoveConfig(x, y, layout);
			case 4:
				return Knight.getMoveConfig(x, y, layout);
			case 5:
				return Rook.getMoveConfig(x, y, layout);
			case 6:
				return Pawn.getMoveConfig(x, y, layout, c);
			default:
				return null;
		}
	}

	public static int[][] getCaptureConfig(int pieceNum, int[][] layout, int x, int y) {
		Color color = Color.WHITE;
		if (pieceNum > 10) {
			pieceNum = pieceNum % 10;
			color = Color.BLACK;
		}
		switch (pieceNum) {
			case 1:
				return King.getMoveConfig(x, y, layout);
			case 2:
				return Queen.getMoveConfig(x, y, layout);
			case 3:
				return Bishop.getMoveConfig(x, y, layout);
			case 4:
				return Knight.getMoveConfig(x, y, layout);
			case 5:
				return Rook.getMoveConfig(x, y, layout);
			case 6:
				return Pawn.getCaptureConfig(x, y, layout, color);
			default:
				return null;
		}
	}

	public int x() {
		return this.x;
	}

	public int y() {
		return this.y;
	}

	public boolean isWhite() {
		return this.iswhite;
	}

}