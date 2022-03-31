package Mine.Sweeper.Drawing;

import Mine.Sweeper.Grid;
import Mine.Sweeper.Point;

public interface UI {
	public void update(Grid engine);
	public Point blockForInput();
	public void gameComplete(boolean win);
}
