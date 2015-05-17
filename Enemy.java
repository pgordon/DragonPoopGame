package DragonPoopGame;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Enemy extends PowerUp
{

	private int speed = 4;
	public static List<Enemy> s_Enemies = new ArrayList<Enemy>();

    public Enemy()
    {
    	super();
    	s_Enemies.add(this);
    }

    //PRECONDITION: Should call isTouching before this to ensure enemies don't overshoot player
    public void Update(int playerX, int playerY)
    {
    	int horizDiff = playerX - x;
    	int vertDiff = playerY - y;

    	if( Math.abs(horizDiff) > Math.abs(vertDiff) )
    	{
			x += speed * Integer.signum(horizDiff);
    	}
    	else
    	{
    		y += speed * Integer.signum(vertDiff);
    	}

    	animation.x = x;
    	animation.y = y;
    }

    public static void Exterminate()
    {
    	s_Enemies.clear();
    }

    public boolean Destroy()
    {
    	return s_Enemies.remove(this);
    }
	
}