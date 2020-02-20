package smartHome;

public class listMoves {
	private move head;
	private move tail;
	private move current;
	private int length;
	
	public listMoves(){
		this.head = null;
		this.tail = null;
		this.current = null;
		this.length = 0;
	}
	
	public move getHead() 
	{
		return this.head;
	}
	
	public move getTail() 
	{
		return this.tail;
	}
	
	public void add(move m) 
	{
		if(this.current == null)
			this.current = m;
		
		if(this.head == null)
		{
			this.head = m;
			this.tail = m;
		}
		
		else
		{
			this.tail.setNextMove(m);
			this.tail = m;
		}
		this.length++;
	}
	
	public int[] getNextTurnValues()
	{
		if(this.current == null)
			return null;
		int[] result = new int[4];
		result[0] = this.current.getMother();
		result[1] = this.current.getFather();
		result[2] = this.current.getBaby();
		result[3] = this.current.getThief();
		this.current = this.current.getNextMove();
		return result;
	}
	
	public int[] getTailTurnValues()
	{
		if(this.tail == null)
			return null;
		int[] result = new int[4];
		result[0] = this.tail.getMother();
		result[1] = this.tail.getFather();
		result[2] = this.tail.getBaby();
		result[3] = this.tail.getThief();
		return result;
	}
	
	public void print()
	{
		move current = this.current;
		this.current = this.head;
		int[] result = getNextTurnValues();
		while(result != null)
		{
			System.out.println("mother = " + result[0] +", father = "+ result[1] +", baby = "+ result[2] +", thief = " + result[3]);
			result = getNextTurnValues();
		}
		this.current = current;
	}

	public int length()
	{
		return this.length;
	}
}
