package smartHome;

public class move {
	private int mother;
	private int father;
	private int baby;
	private int thief;
	private move nextMove;
	
	public move(int mother, int father, int baby, int thief)
	{
		this.mother = mother;
		this.father = father;
		this.baby 	= baby;
		this.thief 	= thief;
		this.nextMove = null;
	}
	
	public move(int mother, int father, int baby, int thief, move nextMove)
	{
		this.mother = mother;
		this.father = father;
		this.baby 	= baby;
		this.thief 	= thief;
		this.nextMove = nextMove;
	}
	
	public int getMother(){
		return this.mother;
	}
	
	public int getFather(){
		return this.father;
	}
	
	public int getBaby(){
		return this.baby;
	}
	
	public int getThief(){
		return this.thief;
	}
	
	public move getNextMove() {
		return this.nextMove;
	}
	
	public void setNextMove(move m) {
		this.nextMove = m;
	}
}
