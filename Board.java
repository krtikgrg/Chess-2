import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;

public class Board extends JPanel implements ActionListener {

	GridBagConstraints gbc;
	int clickedX = 0;
	int clickedY = 2;
	boolean whitesTurn;
	King whiteKing;
	King blackKing;
	boolean globallock = false;
	ChessAI ai;
	public ArrayList<Piece> aiPieces = new ArrayList<Piece>(16);
	public ArrayList<Piece> humanPieces = new ArrayList<Piece>(16);
	boolean firstMove = true;
	public int moves = 0;
	boolean isSinglePlayer;

	/**
	 * Numerical representation of each piece/empty space
	 * 0 = Empty
	 * 1 = King (11 for black)
	 * 2 = Queen (12 for black)
	 * 3 = Bishop (13 for black)
	 * 4 = Knight (14 for black)
	 * 5 = Rook (15 for black)
	 * 6 = Pawn (16 for black)
	 */
	int[][] boardLayout = { {15, 14, 13, 12, 11, 13, 14, 15},
							{16, 16, 16, 16, 16, 16, 16, 16},
							{0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0},
							{6, 6, 6, 6, 6, 6, 6, 6},
							{5, 4, 3, 2, 1, 3, 4, 5}
						};

	public Board(boolean isSinglePlayer) {
		this.isSinglePlayer = isSinglePlayer;
		GridBagLayout layout = new GridBagLayout();
		this.gbc = new GridBagConstraints();
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		whitesTurn = true;
		setLayout(layout);
		placePieces();
	}

	public void placePieces() {
		removeAll();
		Color color = Color.BLACK;
		for (int r = 0 ; r < 8 ; r++) {
			for (int c = 0 ; c < 8 ; c++) {
				if (boardLayout[r][c] > 10) {
					color = Color.BLACK;
				} else {
					color = Color.WHITE;
				}
				Piece piece = getNewPiece(boardLayout[r][c] % 10, color, isWhiteSpace(c, r), c, r);
				if (piece instanceof King) {
					if (color == Color.WHITE) {
						whiteKing = (King) piece;
					} else {
						blackKing = (King) piece;
					}
				}
				gbc.gridx = c;
				gbc.gridy = r;
				if (piece == null) {
					//Empty space
					JButton b = new JButton();
					b.addActionListener(this);
					b.setPreferredSize(new Dimension(80, 76));
					b.setBorderPainted(false);
					b.setOpaque(true);
					if (isWhiteSpace(c, r)) {
						b.setBackground(new Color(192, 192, 192));
					} else {
						b.setBackground(new Color(128, 128, 128));
					}
					add(b, gbc);
					continue;
				}
				piece.addActionListener(this);
				if (!(piece instanceof King)) {
					piece.setMoveConfig(c, r, piece.getMoveConfig(c, r, boardLayout, this), boardLayout, this);
				}
				if (color == Color.BLACK) {
					aiPieces.add(piece);
				} else {
					humanPieces.add(piece);
				}
				add(piece, gbc);
			}
		}
		validate();
	}

	public void createAI() {
		ai = new ChessAI(Color.BLACK, aiPieces, humanPieces, this);
	}

	public boolean canCapture(int num1, int num2) {
		return ((num1 < 10 && num1 > 0) && num2 > 10) || (num1 > 10 && (num2 < 10 && num2 > 0));
	}

	public void addPieceToAI(Piece p) {
		ai.oppPieces.add(p);
	}

	public void actionPerformed(ActionEvent e) {
		if (globallock) {
			return;
		}
		JButton source = (JButton) e.getSource();
		int col = source.getX() / 80;
		int row = source.getY() / 76;

		int numRepLast = boardLayout[clickedY][clickedX];
		int numRepCurr = boardLayout[row][col];

		Piece lastClicked = null;
		if (getComponentAt(clickedX * 80, clickedY * 76) instanceof Piece) {
			lastClicked = (Piece) getComponentAt(clickedX * 80, clickedY * 76);
		}

		int[][] futureLayout = copyBoard();
		int a = futureLayout[clickedY][clickedX];
		futureLayout[clickedY][clickedX] = 0;
		futureLayout[row][col] = a;

		int kingX = whiteKing.x;
		int kingY = whiteKing.y;

		if (!whitesTurn) {
			kingX = blackKing.x;
			kingY = blackKing.y;
		}

		if (lastClicked instanceof King) {
			kingX = col;
			kingY = row;
		}

		if (lastClicked != null && isRightTurn(lastClicked) && (lastClicked.captureConfig[row][col] == 1 || lastClicked.moveConfig[row][col] == 1) && !isSameTeam(numRepLast, numRepCurr)) {
			//If Last clicked spot wasn't empty AND it's the right turn AND current clicked spot IS contained in the piece's move locations AND they are different teams, then we can move
			if (isKingSafe(kingX, kingY, whitesTurn, futureLayout)) {
				if (Math.abs(clickedX - col) == 2) {
					boolean queenSideCastle = (clickedX - col) == 2;
					if (lastClicked instanceof King && canCastle(whitesTurn, queenSideCastle)) {
						if (queenSideCastle) {
							issueMove(lastClicked, clickedX, clickedY, col, row, false, false);
							Piece rook = (Piece) getComponentAt((col - 2) * 80, row * 76);
							issueMove(rook, rook.x, rook.y, col + 1, row, false);
							log(getColor(whitesTurn), lastClicked, "0-0-0");
						} else {
							issueMove(lastClicked, clickedX, clickedY, col, row, false, false);
							Piece rook = (Piece) getComponentAt((col + 1) * 80, row * 76);
							issueMove(rook, rook.x, rook.y, col - 1, row, false);
							log(getColor(whitesTurn), lastClicked, "0-0");
						}
						King k = (King) lastClicked;
						k.isCastled = true;
						k.canCastleKingSide = false;
						k.canCastleQueenSide = false;
						clickedX = col;
						clickedY = row;
						lastClicked.setMoveConfig(col, row, lastClicked.getMoveConfig(col, row, boardLayout, this), boardLayout, this);
						return;
					} else if (lastClicked instanceof King && !canCastle(whitesTurn, queenSideCastle)) {
						return;
					}
				}
				if (lastClicked instanceof Pawn) {
					Pawn pawn = (Pawn) lastClicked;
					if (clickedX != col && boardLayout[row][col] == 0) {
						//Diagonal move but empty space (trying to en passant)
						remove(getComponentAt(col * 80, clickedY * 76));
						gbc.gridx = col;
						gbc.gridy = clickedY;

						boardLayout[clickedY][col] = 0;

						JButton b = new JButton();
						b.addActionListener(this);
						b.setPreferredSize(new Dimension(80, 76));
						b.setBorderPainted(false);
						b.setOpaque(true);
						if (isWhiteSpace(col, clickedY)) {
							b.setBackground(new Color(192, 192, 192));
						} else {
							b.setBackground(new Color(128, 128, 128));
						}
						add(b, gbc);
					}
				}
				if (lastClicked instanceof King) {
					King king = (King) lastClicked;
					king.canCastleQueenSide = false;
					king.canCastleKingSide = false;
				}
				if (lastClicked instanceof Rook) {
					boolean queenSide = false;
					if (lastClicked.x == 0) {
						//Queen side rook
						queenSide = true;	
					}
					if (whitesTurn) {
						if (queenSide) {
							whiteKing.canCastleQueenSide = false;
						} else {
							whiteKing.canCastleKingSide = false;
						}
					} else {
						if (queenSide) {
							blackKing.canCastleQueenSide = false;
						} else {
							blackKing.canCastleKingSide = false;
						}
					}
				}
				issueMove(lastClicked, clickedX, clickedY, col, row, true);

				if (lastClicked instanceof Pawn) {
					//We have to check for en passant here because issueMove revalidates move configs and overrides any normally illegal capture configs, so we'll overwrite here if we can en passant
					if (Math.abs(clickedY-row) == 2) {
						if (col-1>=0) {
							if (getComponentAt((col-1)*80, row*76) instanceof Piece) {
								Piece p = (Piece) getComponentAt((col-1)*80, row*76);
								if (p instanceof Pawn && ((Pawn)p).iswhite()==whitesTurn) {
									((Pawn)p).addEnPassant(lastClicked);
								}
							}
						} 
						if (col+1<=7) {
							if (getComponentAt((col+1)*80, row*76) instanceof Piece) {
								Piece p = (Piece) getComponentAt((col+1)*80, row*76);
								if (p instanceof Pawn && ((Pawn)p).iswhite()==whitesTurn) {
									((Pawn)p).addEnPassant(lastClicked);
								}
							}
						}
					}
				}
				
				lastClicked.setMoveConfig(col, row, lastClicked.getMoveConfig(col, row, boardLayout, this), boardLayout, this);

				if (isInCheck(whitesTurn)) {
					if (isInCheckmate(whitesTurn, lastClicked, boardLayout)) {
						log(getColor(whitesTurn), null, "Checkmate");
						return;
					}
					log(getColor(whitesTurn), null, "Check");
				}

				boolean castleLock = !isInCheck(whitesTurn);
				if (whitesTurn) {
					whiteKing.canCastleKingSide = castleLock;
					whiteKing.canCastleQueenSide = castleLock;
				} else {
					blackKing.canCastleKingSide = castleLock;
					blackKing.canCastleQueenSide = castleLock;
				}

				if (!whitesTurn && isSinglePlayer) {
					//ai.setBoard(this);
					playAI(lastClicked);
				}

				return;
			} else {
				System.out.println("King not safe, exiting");
			}
		}

		clickedX = col;
		clickedY = row;
	}

	public void log(String player, Piece p, String move) {
		ChessRunner.console.log(player, p, move);
	}

	public boolean isRightTurn(Piece p) {
		return ((p.color == Color.WHITE && whitesTurn) || (p.color == Color.BLACK && !whitesTurn));
	}

	public boolean canCastle(boolean white, boolean queenSide) {
		if (white) {
			if (queenSide && whiteKing.canCastleQueenSide && !whiteKing.isCastled) {
				return (!isSquareTargeted(7, 1, !white, boardLayout) && !isSquareTargeted(7, 2, !white, boardLayout) && !isSquareTargeted(7, 3, !white, boardLayout) && boardLayout[7][0] == 5 && boardLayout[7][1] == 0 && boardLayout[7][2] == 0 && boardLayout[7][3] == 0);
			} else if (!queenSide && whiteKing.canCastleKingSide && !whiteKing.isCastled) {
				return (!isSquareTargeted(7, 5, !white, boardLayout) && !isSquareTargeted(7, 6, !white, boardLayout) && boardLayout[7][5] == 0 && boardLayout[7][6] == 0 && boardLayout[7][7] == 5);
			}
		} else {
			if (queenSide && blackKing.canCastleQueenSide && !blackKing.isCastled) {
				return (!isSquareTargeted(0, 1, !white, boardLayout) && !isSquareTargeted(0, 2, !white, boardLayout) && !isSquareTargeted(0, 3, !white, boardLayout) && boardLayout[0][0] == 15 && boardLayout[0][1] == 0 && boardLayout[0][2] == 0 && boardLayout[0][3] == 0);
			} else if (!queenSide && blackKing.canCastleKingSide && !blackKing.isCastled) {
				return (!isSquareTargeted(0, 5, !white, boardLayout) && !isSquareTargeted(0, 6, !white, boardLayout) && boardLayout[0][5] == 0 && boardLayout[0][6] == 0 && boardLayout[0][7] == 15);
			}
		}
		return false;
	}

	public boolean isInCheck(boolean white) {
		if (white) {
			return !isKingSafe(whiteKing.x, whiteKing.y, white, boardLayout);
		}
		return !isKingSafe(blackKing.x, blackKing.y, white, boardLayout);
	}

	public boolean isInCheck(boolean white, int[][] futureLayout) {
		if (white) {
			return !isKingSafe(whiteKing.x, whiteKing.y, white, futureLayout);
		}
		return !isKingSafe(blackKing.x, blackKing.y, white, futureLayout);
	}

	public boolean isInCheckmate(boolean white, Piece lastmoved, int[][] layout) {
		System.out.println("Checking for checkmate...");
		ArrayList<Piece> targeting = targeting(lastmoved.y, lastmoved.x, white, boardLayout);
		System.out.println("Got targeting pieces");
		boolean checkmate = false;
		King king = whiteKing;
		if (!white) {
			king = blackKing;
		}
		if (canBeBlocked(lastmoved, king)) {
			System.out.println("Can be blocked");
			return false;
		}
		System.out.println("Can't be blocked");
		if (targeting.size() == 1 && targeting.get(0) instanceof King) {
			//Only king is targeting the enemy piece
			if (isSquareTargeted(lastmoved.y, lastmoved.x, !white, boardLayout)) {
				System.out.println("Piece is protected");
				//Enemy piece is protected
				checkmate = true;
			} else {
				System.out.println("Piece is not protected");
				return false;
			}
		} else if (targeting.size() > 0) {
			System.out.println("Can take piece");
			return false;
		}
		if (white) {
			for (int r = 0 ; r < 8 ; r++) {
				for (int c = 0 ; c < 8 ; c++) {
					if (whiteKing.moveConfig[r][c] == 1 && isKingSafe(c, r, white, boardLayout)) {
						System.out.println("King can move");
						return false;
					}
				}
			}
			checkmate = true;
		} else {
			for (int r = 0 ; r < 8 ; r++) {
				for (int c = 0 ; c < 8 ; c++) {
					if (blackKing.moveConfig[r][c] == 1 && isKingSafe(c, r, white, boardLayout)) {
						System.out.println("King can move");
						return false;
					}
				}
			}
			checkmate = true;
		}
		return checkmate;
	}

	public boolean canBeBlocked(Piece toBlock, King king) {
		int diffX = Math.abs(toBlock.x - king.x);
		int diffY = Math.abs(toBlock.y - king.y);
		System.out.println("diffX: " + diffX + ", diffY: " + diffY);
		if (diffX == 0) {
			diffX = -5;
		}
		if (diffY == 0) {
			diffY = -5;
		}
		int tdiffX = (toBlock.x - king.x) / (diffX * -1);
		int tdiffY = (toBlock.y - king.y) / (diffY * -1);
		System.out.println("tdiffX: " + tdiffX + ", tdiffY: " + tdiffY);
		if ((diffX == 1 || diffY == 1 || toBlock instanceof Knight)) {
			return false;
		}
		boolean white = king.color == Color.WHITE;
		int row = toBlock.y;
		int col = toBlock.x;
		while ((diffX > 1 || diffX == -5) && (diffY > 1 || diffY == -5)) {
			row += tdiffY;
			col += tdiffX;
			System.out.println("row: " + row + ", col: " + col);
			ArrayList<Piece> targeting = targeting(row, col, white, boardLayout);
			if (targeting.size() == 1 && targeting.get(0) instanceof King) {
				continue;
			}
			if (isSquareTargeted(row, col, white, boardLayout)) {
				//One of the in between squares is targeted by an allied piece, thus we can block the piece in question
				return true;
			}
			if (diffX > 0) {
				diffX = Math.abs(col - king.x);
			}
			if (diffY > 0) {
				diffY = Math.abs(row - king.y);
			}
		}
		return false;
	}

	public int[][] copyBoard() {
		int[][] copy = new int[8][8];

		for (int r = 0 ; r < 8 ; r++) {
			copy[r] = Arrays.copyOf(boardLayout[r], 8);
		}

		return copy;
	}

	public int[][] copyBoard(int[][] board) {
		int[][] copy = new int[8][8];

		for (int r = 0 ; r < 8 ; r++) {
			copy[r] = Arrays.copyOf(board[r], 8);
		}

		return copy;
	}

	public void printBoard() {
		for (int c = 0 ; c < 8 ; c++) {
			for (int r = 0 ; r < 8 ; r++) {
				System.out.print(boardLayout[c][r] + " ");
			}
			System.out.println("");
		}
	}
	public void printBoard(int[][] board) {
		for (int c = 0 ; c < 8 ; c++) {
			for (int r = 0 ; r < 8 ; r++) {
				System.out.print(board[c][r] + " ");
			}
			System.out.println("");
		}
	}

	public boolean isKingSafe(int x, int y, boolean white, int[][] layout) {
		return !isSquareTargeted(y, x, !white, layout);
	}

	public boolean isSameTeam(int num1, int num2) {
		if (num1 == 0 || num2 == 0) {
			return false;
		}
		return (num1 > 10 && num2 > 10 || num1 < 10 && num2 < 10);
	}

	public void issueMove(Piece piece, int x, int y, int x2, int y2, boolean log) {
		boolean pieceWasTaken = (getComponentAt(x2 * 80, y2 * 76) instanceof Piece);
		boardLayout[y2][x2] = boardLayout[y][x];
		boardLayout[y][x] = 0;
		remove(piece);
		if (getComponentAt(x2 * 80, y2 * 76) instanceof Piece) {
			Piece otherPiece = (Piece) getComponentAt(x2 * 80, y2 * 76);
			if (otherPiece.color == Color.BLACK) {
				ai.removePiece(otherPiece);
			} else {
				ai.removeOtherPiece(otherPiece);
			}
		}
		remove(getComponentAt(x2 * 80, y2 * 76));

		gbc.gridx = x;
		gbc.gridy = y;

		JButton b = new JButton();
		b.addActionListener(this);
		b.setPreferredSize(new Dimension(80, 76));
		b.setBorderPainted(false);
		b.setOpaque(true);
		if (isWhiteSpace(x, y)) {
			b.setBackground(new Color(192, 192, 192));
		} else {
			b.setBackground(new Color(128, 128, 128));
		}
		add(b, gbc);

		gbc.gridx = x2;
		gbc.gridy = y2;

		piece.setOpaque(true);
		if (isWhiteSpace(x2, y2)) {
			piece.setBackground(new Color(192, 192, 192));
		} else {
			piece.setBackground(new Color(128, 128, 128));
		}

		add(piece, gbc);
		piece.x = x2;
		piece.y = y2;
		validate();
		repaint();

		clickedX = x;
		clickedY = y;
		if (piece instanceof Pawn) {
			((Pawn)piece).setHasMoved();
			checkPromotion(piece);
		}
		revalidateMoveConfigs();
		whitesTurn = !whitesTurn;

		String middle = " - ";
		if (pieceWasTaken) {
			middle = " x ";
		}
		if (log) {
			log(getColor(!whitesTurn), piece, (coordToLetter(x,y) + middle + coordToLetter(x2,y2)));
		}
		// System.out.println("Issued move for: " + piece + " from: " + x + ", " + y + " to: " + x2 + ", " + y2);
		
	}

	public void issueMove(Piece piece, int x, int y, int x2, int y2, boolean log, boolean playAI) {
		boolean pieceWasTaken = (getComponentAt(x2 * 80, y2 * 76) instanceof Piece);
		int spotMovedTo = boardLayout[y2][x2];

		boardLayout[y2][x2] = boardLayout[y][x];
		boardLayout[y][x] = 0;
		remove(piece);
		if (getComponentAt(x2 * 80, y2 * 76) instanceof Piece) {
			Piece otherPiece = (Piece) getComponentAt(x2 * 80, y2 * 76);
			if (otherPiece.color == Color.BLACK) {
				ai.removePiece(otherPiece);
			}
		}
		remove(getComponentAt(x2 * 80, y2 * 76));

		gbc.gridx = x;
		gbc.gridy = y;

		JButton b = new JButton();
		b.addActionListener(this);
		b.setPreferredSize(new Dimension(80, 76));
		b.setBorderPainted(false);
		b.setOpaque(true);
		if (isWhiteSpace(x, y)) {
			b.setBackground(new Color(192, 192, 192));
		} else {
			b.setBackground(new Color(128, 128, 128));
		}
		add(b, gbc);

		gbc.gridx = x2;
		gbc.gridy = y2;

		piece.setOpaque(true);
		if (isWhiteSpace(x2, y2)) {
			piece.setBackground(new Color(192, 192, 192));
		} else {
			piece.setBackground(new Color(128, 128, 128));
		}

		add(piece, gbc);
		piece.x = x2;
		piece.y = y2;

		validate();
		repaint();

		clickedX = x;
		clickedY = y;
		if (piece instanceof Pawn) {
			((Pawn)piece).setHasMoved();
			checkPromotion(piece);
		}
		revalidateMoveConfigs();
		if (playAI) {
			whitesTurn = !whitesTurn;
		}

		String middle = " - ";
		if (pieceWasTaken) {
			middle = " x ";
		}
		if (log) {
			log(getColor(!whitesTurn), piece, (coordToLetter(x,y) + middle + coordToLetter(x2,y2)));
		}
		if (!whitesTurn && isSinglePlayer && playAI) {
			//ai.setBoard(this);
			playAI(piece);
		}
		// System.out.println("Issued move for: " + piece + " from: " + x + ", " + y + " to: " + x2 + ", " + y2);
	}

	public void playAI(Piece justMoved) {
		if (firstMove) {
			ai.legalMoves = ai.getLegalMoves(true, boardLayout, ai.pieces, ai.oppPieces);
			firstMove = false;
		}
		System.out.println("Thinking...");
		long start = System.currentTimeMillis();

		Move bestMove;

		ai.start = start;

		if (moves < 1) {
			bestMove = ai.negamax(2, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, boardLayout, 1, ai.openingMoves, ai.pieces, ai.oppPieces);
			ai.openingMoves.remove(bestMove);
		} else {
			bestMove = ai.negamax(2, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, boardLayout, 1, ai.pieces, ai.oppPieces);
		}
		moves++;
		System.out.println("Done - " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
		if (bestMove == null) {
			if (isInCheck(false)) {
				log("-", justMoved, "Checkmate!");
			} else {
				log("-", justMoved, "Stalemate!");
			}
			return;
		}
		System.out.println(bestMove);
		issueMove(bestMove.piece, bestMove.x1, bestMove.y1, bestMove.x2, bestMove.y2, true);
	}

	public String getColor(boolean white) {
		if (white) {
			return "WHITE";
		}
		return "BLACK";
	}

	public String coordToLetter(int x, int y) {
		String prefix = "a";
		switch (x) {
			case 1:
				prefix = "b";
				break;
			case 2:
				prefix = "c";
				break;
			case 3:
				prefix = "d";
				break;
			case 4:
				prefix = "e";
				break;
			case 5:
				prefix = "f";
				break;
			case 6:
				prefix = "g";
				break;
			case 7:
				prefix = "h";
				break;
			default:
				prefix = "a";
				break;
		}
		return (prefix + (8 - y));
	}

	public void checkPromotion(Piece piece) {
		if ((piece.color == Color.WHITE && piece.y==0) || (piece.color == Color.BLACK && piece.y==7)) {
			PromotionPanel ppanel = new PromotionPanel(piece.color, piece);
			globallock=true;
			getParent().add(ppanel);
			getParent().validate();
			validate();
		}
	}

	public boolean isSquareTargeted(int row, int col, boolean byWhite, int[][] layout) {
		if (byWhite) { //Checking that White is targeting this square
			for (int r = 0 ; r < 8 ; r++) {
				for (int c = 0 ; c < 8 ; c++) { //Check every piece on the board
					if (layout[r][c] == 0 || layout[r][c] > 10) { //So long as it isn't empty or Black
						continue;
					}
					//layout[r][c] is the current pieceNum
					int[][] captureConfig = Piece.getCaptureConfig(layout[r][c], layout, c, r);

					if (captureConfig[row][col] == 1 || (row < 7 && col < 7 && layout[row + 1][col + 1] == 6) || (row < 7 && col > 0 && layout[row + 1][col - 1] == 6)) {
						return true;
					}
				}
			}
			return false;
		} else {
			for (int r = 0 ; r < 8 ; r++) {
				for (int c = 0 ; c < 8 ; c++) { //Check every piece on the board
					if (layout[r][c] == 0 || layout[r][c] < 10) { //So long as it isn't empty or White
						continue;
					}
					//layout[r][c] is the current pieceNum
					int[][] captureConfig = Piece.getCaptureConfig(layout[r][c], layout, c, r);

					if (captureConfig[row][col] == 1 || (row > 0 && col < 7 && layout[row - 1][col + 1] == 16) || (row > 0 && col > 0 && layout[row - 1][col - 1] == 16)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public void revalidateMoveConfigs() {
		for (int r = 0 ; r < 8 ; r++) {
			for (int c = 0 ; c < 8 ; c++) {
				if (boardLayout[r][c] != 0 && getComponentAt(c * 80, r * 76) instanceof Piece) {
					Piece p = (Piece) getComponentAt(c * 80, r * 76);
					p.setMoveConfig(c, r, p.getMoveConfig(c, r, boardLayout, this), boardLayout, this);
				}
			}
		}
	}

	public Piece getNewPiece(int numRep, Color color, boolean white, int x, int y) {
		if (numRep == 0) {
			return null;
		} else if (numRep == 1) {
			return new King(color, white, x, y);
		} else if (numRep == 2) {
			return new Queen(color, white, x, y);
		} else if (numRep == 3) {
			return new Bishop(color, white, x, y);
		} else if (numRep == 4) {
			return new Knight(color, white, x, y);
		} else if (numRep == 5) {
			return new Rook(color, white, x, y);
		}
		return new Pawn(color, white, x, y);
	}

	public boolean isWhiteSpace(int x, int y) {
		return ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1));
	}

	public ArrayList<Piece> targeting(int row, int col, boolean byWhite, int[][] layout) {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		if (byWhite) { //Checking that White is targeting this square
			for (int r = 0 ; r < 8 ; r++) {
				for (int c = 0 ; c < 8 ; c++) { //Check every piece on the board
					if (layout[r][c] == 0 || layout[r][c] > 10) { //So long as it isn't empty or Black
						continue;
					}
					if (!(getComponentAt(c * 80, r * 76) instanceof Piece)) {
						continue;
					}
					Piece p = (Piece) getComponentAt(c * 80, r * 76);
					if (p.captureConfig[row][col] == 1) {
						pieces.add(p);
					} else {
						continue;
					}
				}
			}
			return pieces;
		} else {
			for (int r = 0 ; r < 8 ; r++) {
				for (int c = 0 ; c < 8 ; c++) { //Check every piece on the board
					if (layout[r][c] == 0 || layout[r][c] < 10) { //So long as it isn't empty or White
						continue;
					}
					if (!(getComponentAt(c * 80, r * 76) instanceof Piece)) {
						continue;
					}
					Piece p = (Piece) getComponentAt(c * 80, r * 76);
					if (p.captureConfig[row][col] == 1) {
						pieces.add(p);
					} else {
						continue;
					}
				}
			}
			return pieces;
		}
	}
}