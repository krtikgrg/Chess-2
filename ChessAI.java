import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

public class ChessAI {

	private Color color;
	public List<Move> legalMoves;
	public List<Piece> pieces;
	public List<Piece> oppPieces;
	public List<Move> openingMoves;
	private King king;
	private King oppKing;
	private Board board;
	private int MAX_ITER = 1;

	long start = 0;

	public Piece kingPawn;
	public Piece queenPawn;
	public Piece kingKnight;
	public Piece kingBishop;
	public Piece kingKnightPawn;
	public Piece queenKnight;

	public ChessAI(Color color, ArrayList<Piece> pieces, ArrayList<Piece> oppPieces, Board board) {
		this.color = color;
		this.pieces = pieces;
		this.oppPieces = oppPieces;
		this.king = getKing();
		this.oppKing = getOppKing();
		this.board = board;

		openingMoves = new ArrayList<Move>();

		for (Piece p : pieces) {
			if (p.x == 4 && p.y == 1) {
				kingPawn = p;
			} else if (p.x == 3 && p.y == 1) {
				queenPawn = p;
			} else if (p.x == 6 && p.y == 0) {
				kingKnight = p;
			} else if (p.x == 1 && p.y == 0) {
				queenKnight = p;
			} else if (p.x == 5 && p.y == 0) {
				kingBishop = p;
			} else if (p.x == 6 && p.y == 1) {
				kingKnightPawn = p;
			}
		}

		Move kp = new Move(kingPawn, kingPawn.x, kingPawn.y, kingPawn.x, kingPawn.y + 2);
		Move qp = new Move(queenPawn, queenPawn.x, queenPawn.y, queenPawn.x, queenPawn.y + 2);
		Move k = new Move(kingKnight, kingKnight.x, kingKnight.y, kingKnight.x - 1, kingKnight.y + 2);
		Move qk = new Move(queenKnight, queenKnight.x, queenKnight.y, queenKnight.x + 1, queenKnight.y + 2);
		Move b = new Move(kingBishop, kingBishop.x, kingBishop.y, kingBishop.x + 1, kingBishop.y + 1);
		Move kbp = new Move(kingKnightPawn, kingKnightPawn.x, kingKnightPawn.y, kingKnightPawn.x, kingKnightPawn.y + 1);

		openingMoves.add(kp);
		openingMoves.add(qp);
		openingMoves.add(k);
		openingMoves.add(qk);
		openingMoves.add(b);
		openingMoves.add(kbp);

		Collections.sort(openingMoves);
	}

	public Piece getPieceAt(int x, int y) {
		for (Piece p : pieces) {
			if (p.x == x && p.y == y) {
				return p;
			}
		}
		return null;
	}

	public void removeOtherPiece(Piece p) {
		oppPieces.remove(p);
	}

	public void removePiece(Piece p) {
		pieces.remove(p);
	}

	public King getKing() {
		for (Piece p : pieces) {
			if (p instanceof King) {
				return (King) p;
			}
		}
		return null;
	}

	public King getOppKing() {
		for (Piece p : oppPieces) {
			if (p instanceof King) {
				return (King) p;
			}
		}
		return null;
	}

	public Queen getQueen() {
		for (Piece p : pieces) {
			if (p instanceof Queen) {
				return (Queen) p;
			}
		}
		return null;
	}

	public Queen getOppQueen() {
		for (Piece p : oppPieces) {
			if (p instanceof Queen) {
				return (Queen) p;
			}
		}
		return null;
	}

	public Move getBestSingleMove(boolean forAi, int[][] layout, List<Piece> pieces, List<Piece> oppPieces) {
		ArrayList<Move> bestMoves = new ArrayList<Move>();
		Move bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		if (!forAi) {
			bestScore = Integer.MAX_VALUE;
		}
		//AI wants higher scored moves, human wants lowest
		for (Move move : getLegalMoves(forAi, layout, pieces, oppPieces)) {
			if ((move.score > bestScore && forAi) || (move.score < bestScore && !forAi)) {
				bestMove = move;
				bestScore = move.score;
				bestMoves.clear();
				bestMoves.add(move);
			} else if (move.score == bestScore) {
				bestMoves.add(move);
				bestScore = move.score;
			}
		}

		if (bestMoves.size() > 1) {
			return bestMoves.get((new Random()).nextInt(bestMoves.size()));
		}
		return bestMove;
	}

	public int[][] copyDoubleArray(int[][] src, int[][] dest) {
		for (int i = 0 ; i < 8 ; i++) {
		    System.arraycopy(src[i], 0, dest[i], 0, 8);
		}

		return dest;
	}

	public Move negamax(int depth, int alpha, int beta, int[][] layout, int color, List<Piece> pieces, List<Piece> oppPieces) {
		boolean ai = color > 0;
		if (depth == 0) {
			return getBestSingleMove(ai, layout, pieces, oppPieces);
		}
		Move bestMove = null;
		int bestScore = Integer.MIN_VALUE;
		if (!ai) {
			bestScore = Integer.MAX_VALUE;
		}

		List<Move> moves = getLegalMoves(ai, layout, pieces, oppPieces);
		for (int i = moves.size() - 1 ; i >= 0 ; i--) {
			Move move = moves.get(i);
			int[][] futureLayout = new int[8][8];
			futureLayout = copyDoubleArray(layout, futureLayout);
			futureLayout[move.y2][move.x2] = futureLayout[move.y1][move.x1];
			futureLayout[move.y1][move.x1] = 0;
			Move m = negamax(depth - 1, -1 * beta, -1 * alpha, futureLayout, -1 * color, pieces, oppPieces);
			if (m == null) {
				continue;
			}
			int val = m.score;
			if ((val < bestScore && !ai) || (val > bestScore && ai)) {
				bestScore = val;
				bestMove = move;
			}
		}
		return bestMove;
	}

	//With a dictionary of moves (for opening)
	public Move negamax(int depth, int alpha, int beta, int[][] layout, int color, List<Move> moves, List<Piece> pieces, List<Piece> oppPieces) {
		boolean ai = color > 0;
		if (depth == 0) {
			return getBestSingleMove(ai, layout, pieces, oppPieces);
		}
		Move bestMove = null;
		int bestScore = Integer.MAX_VALUE;

		for (int i = moves.size() - 1 ; i >= 0 ; i--) {
			Move move = moves.get(i);
			int[][] futureLayout = new int[8][8];
			futureLayout = copyDoubleArray(layout, futureLayout);
			futureLayout[move.y2][move.x2] = futureLayout[move.y1][move.x1];
			futureLayout[move.y1][move.x1] = 0;
			Move m = negamax(depth - 1, -1 * beta, -1 * alpha, futureLayout, -1 * color, pieces, oppPieces);
			if (m == null) {
				continue;
			}
			int val = m.score;
			if (val < bestScore) {
				bestScore = val;
				bestMove = move;
			}
		}
		return bestMove;
	}

	public List<Move> getLegalMoves(boolean ai, int[][] layout, List<Piece> pieces, List<Piece> oppPieces) {
		List<Move> moves = new ArrayList<Move>();
		List<Piece> iterPieces = pieces;
		if (!ai) {
			iterPieces = oppPieces;
		}
		for (Piece piece : iterPieces) {
			List<Move> newMoves = getLegalMoves(piece, layout, pieces, oppPieces);
			moves.addAll(newMoves);
		}
		Collections.sort(moves);
		return moves;
	}

	public boolean isMajorMinorPiece(Piece p) {
		return (p instanceof Rook || p instanceof Queen || p instanceof Knight || p instanceof Knight);
	}

	public List<Move> getLegalMoves(Piece piece, int[][] layout, List<Piece> pieces, List<Piece> oppPieces) {
		List<Move> moves = new ArrayList<Move>();
		boolean ai = piece.color == Color.BLACK;
		King k = this.king;
		if (!ai) {
			k = this.oppKing;
		}
		int oldKX = k.x;
		int oldKY = k.y;
		int[][] pMoveConfig = piece.getMoveConfig(piece.x, piece.y, layout, board);
		for (int r = 0 ; r < 8 ; r++) {
			for (int c = 0 ; c < 8 ; c++) {
				int[][] futureLayout = new int[8][8];
				if (pMoveConfig[r][c] == 1) {
					futureLayout = copyDoubleArray(layout, futureLayout);
					futureLayout[r][c] = futureLayout[piece.y][piece.x];
					futureLayout[piece.y][piece.x] = 0;
					if (piece instanceof King) {
						k.x = c;
						k.y = r;
					}
					if (!board.isKingSafe(k.x, k.y, !ai, futureLayout)) {
						k.x = oldKX;
						k.y = oldKY;
						continue;
					}
					k.x = oldKX;
					k.y = oldKY;
					Move move = new Move(piece, piece.x, piece.y, c, r);
					int evalScore = evaluateScore(move, futureLayout, layout, pieces, oppPieces);
					move.score = evalScore;
					move.orderingScore = evalScore;
					if (layout[r][c] != 0) {
						//capture
						move.orderingScore += 200;
					}
					moves.add(move);
				}
				if (piece instanceof Pawn) {
					int[][] pCaptureConfig = piece.getCaptureConfig(piece.x, piece.y, layout, board);
					if (pCaptureConfig[r][c] == 1) {
						futureLayout = copyDoubleArray(layout, futureLayout);
						futureLayout[r][c] = futureLayout[piece.y][piece.x];
						futureLayout[piece.y][piece.x] = 0;
						Move move = new Move(piece, piece.x, piece.y, c, r);
						int evalScore = evaluateScore(move, futureLayout, layout, pieces, oppPieces);
						move.score = evalScore;
						move.orderingScore = evalScore;
						if (layout[r][c] != 0) {
							//capture
							move.orderingScore += 200;
						}
						moves.add(move);
					}
				}
			}
		}
		return moves;
	}

	public int pieceValue(int[][] layout, int y, int x) {
		if (layout[y][x] == 2 || layout[y][x] == 12) {
			return 900;
		} else if (layout[y][x] == 3 || layout[y][x] == 13 || layout[y][x] == 4 || layout[y][x] == 14) {
			return 300;
		} else if (layout[y][x] == 5 || layout[y][x] == 15) {
			return 500;
		}
		int pawnVal = 1;
		if (board.moves < 10) {
			pawnVal = 3;
		}
		return (pawnVal * 100);
	}

	public int evaluateScore(Move move, int[][] futureLayout, int[][] layout, List<Piece> pieces, List<Piece> oppPieces) {
		int score = 0;
		King k = this.king;
		List<Piece> iterPieces = pieces;

		Piece piece = move.piece;

		boolean forAI = piece.color == Color.BLACK;

		if (!forAI) {
			k = this.oppKing;
			iterPieces = oppPieces;
		}

		if (piece instanceof King) {
			//Moving king is generally bad...
			score += -10000;
		}

		int wPawn = 0;
		int bPawn = 0;
		int wBishop = 0;
		int bBishop = 0;
		int wRook = 0;
		int bRook = 0;
		int wKnight = 0;
		int bKnight = 0;

		int wMaterial = 0;
		int bMaterial = 0;

		int captureFactor = 1;

		for (int r = 0 ; r < 8 ; r++) {
			for (int c = 0 ; c < 8 ; c++) {
				if (forAI) {
					if (futureLayout[r][c] == 12 && board.isSquareTargeted(r, c, forAI, futureLayout)) {
						//Queen targeted = bad
						score -= 1000;
					} 
				} else {
					if (futureLayout[r][c] == 2 && board.isSquareTargeted(r, c, forAI, futureLayout)) {
						//Queen targeted = bad
						score -= 1000;
					}
				}
			}
		}

		if (board.isSquareTargeted(move.y2, move.x2, forAI, futureLayout)) {
			score += -200 * pieceValue(futureLayout, move.y2, move.x2);
			captureFactor = 1;
			int valCaptured = pieceValue(futureLayout, move.y2, move.x2);
			int valCapturing = pieceValue(futureLayout, move.y1, move.x1);
			if (valCaptured > valCapturing) {
				captureFactor = 5;
			} else {
				captureFactor = valCaptured - valCapturing;
			}
		} else {
			captureFactor = 20;
		}

		if (move.piece instanceof Pawn) {
			if (Math.abs(move.y2 - move.y1) == 2) {
				//Moving pawn by 2 is better, generally
				score += 50;
			}
			if (move.y2 + 1 < 8 && move.y2 - 1 >= 0) {
				if (move.x2 - 1 >= 0) {
					if ((futureLayout[move.y2 + 1][move.x2 - 1] == 6 && piece.color == Color.WHITE) || (futureLayout[move.y2 - 1][move.x2 - 1] == 16 && piece.color == Color.BLACK)) {
						score += 300;
					}
					if ((futureLayout[move.y2 - 1][move.x2 - 1] == 6 && piece.color == Color.WHITE) || (futureLayout[move.y2 + 1][move.x2 - 1] == 16 && piece.color == Color.BLACK)) {
						score += 300;
					}
				}
			}

			if (move.x2 + 1 < 8) {
				if ((futureLayout[move.y2 + 1][move.x2 + 1] == 6 && piece.color == Color.WHITE) || (futureLayout[move.y2 - 1][move.x2 + 1] == 16 && piece.color == Color.BLACK)) {
					score += 300;
				}
				if (move.y2 + 1 < 8 && move.y2 - 1 >= 0) {
					if ((futureLayout[move.y2 - 1][move.x2 + 1] == 6 && piece.color == Color.WHITE) || (futureLayout[move.y2 + 1][move.x2 + 1] == 16 && piece.color == Color.BLACK)) {
						score += 300;
					}
				}
			}
		}

		if (layout[move.y2][move.x2] == 2 || layout[move.y2][move.x2] == 12) {
			score += (captureFactor * 900);
		} else if (layout[move.y2][move.x2] == 3 || layout[move.y2][move.x2] == 13 || layout[move.y2][move.x2] == 4 || layout[move.y2][move.x2] == 14) {
			score += (captureFactor * 300);
		} else if (layout[move.y2][move.x2] == 5 || layout[move.y2][move.x2] == 15) {
			score += (captureFactor * 500);
		} else if (layout[move.y2][move.x2] == 6 || layout[move.y2][move.x2] == 16) {
			score += (captureFactor * 100);
		}

		for (int r = 0 ; r < 8 ; r++) {
			for (int c = 0 ; c < 8 ; c++) {
				if (futureLayout[r][c] == 6) {
					wPawn++;
					wMaterial += 100;
					if (c == 3 || c == 4) {
						//Pawn is worth more if it is a D or E file pawn
						wMaterial += 50;
					} else if (c == 0 || c == 7) {
						//But it is worth less if it is an A or H file pawn
						wMaterial -= 50;
					}
				} else if (futureLayout[r][c] == 16) {
					bPawn++;
					bMaterial += 100;
					if (c == 3 || c == 4) {
						//Pawn is worth more if it is a D or E file pawn
						bMaterial += 50;
					} else if (c == 0 || c == 7) {
						//But it is worth less if it is an A or H file pawn
						bMaterial -= 50;
					}
				} else if (futureLayout[r][c] == 5) {
					wRook++;
					wMaterial += 500;
				} else if (futureLayout[r][c] == 15) {
					bRook++;
					bMaterial += 500;
				} else if (futureLayout[r][c] == 4) {
					wKnight++;
					wMaterial += 325;
				} else if (futureLayout[r][c] == 14) {
					bKnight++;
					bMaterial += 325;
				} else if (futureLayout[r][c] == 3) {
					wBishop++;
					wMaterial += 325;
				} else if (futureLayout[r][c] == 13) {
					bBishop++;
					bMaterial += 325;
				} else if (futureLayout[r][c] == 2) {
					wMaterial += 975;
				} else if (futureLayout[r][c] == 12) {
					bMaterial += 975;
				}
			}
		}

		if (board.isInCheck(!forAI, futureLayout)) {
			score += 2000;
		}

		if (bBishop == 2) {
			bMaterial += 50;
		}
		if (wBishop == 2) {
			wMaterial += 50;
		}

		bMaterial += ((bPawn - 5) * (6 * bKnight));
		bMaterial += ((bPawn - 5) * (-12 * bRook));

		wMaterial += ((wPawn - 5) * (6 * wKnight));
		wMaterial += ((wPawn - 5) * (-12 * wRook));

		if (forAI) {
			score += (bMaterial - wMaterial) + (10 * piece.pieceSquareConfigB[move.y2][move.x2]);
		} else {
			score += (wMaterial - bMaterial) + (10 * piece.pieceSquareConfigW[move.y2][move.x2]);
		}

		if (!forAI) {
			//If human, we want the opposite scale, lower numbers = good for human moves
			score *= -1;
		}

		return score;
	}
}