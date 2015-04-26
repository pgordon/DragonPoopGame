package DragonPoopGame;

public class MoveNode
{
    public int x = 0;
    public int y = 0;
    public PlayerRocket.Direction direction = PlayerRocket.Direction.NONE;

    MoveNode(int x, int y, PlayerRocket.Direction direction)
    {
    	this.x = x;
    	this.y = y;
    	this.direction = direction;
    }
}