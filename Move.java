public class Move implements Comparable<Move> {

	public int x1;
	public int y1;
	public int x2;
	public int y2;
	public int score;
	public int orderingScore;
	public Piece piece;

	public Move(Piece piece, int startX, int startY, int endX, int endY) {
		this.piece = piece;
		this.x1 = startX;
		this.y1 = startY;
		this.x2 = endX;
		this.y2 = endY;
		this.score = 0;
		this.orderingScore = 0;
	}

	public String toString() {
		return (piece + ": " + x1 + ", " + y1 + " ---> " + x2 + ", " + y2 + ", score: " + score + " (" + piece.color + ")");
	}

	@Override
	public int compareTo(Move m) {
		return (this.orderingScore - m.orderingScore);
	}
}