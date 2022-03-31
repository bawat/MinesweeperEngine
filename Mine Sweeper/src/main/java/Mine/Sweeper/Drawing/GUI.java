package Mine.Sweeper.Drawing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JPanel;

import Mine.Sweeper.Cell;
import Mine.Sweeper.Grid;
import Mine.Sweeper.Point;
import lombok.SneakyThrows;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI implements UI{

	private Thread launchingThread;
	private JFrame frame = new JFrame("Minesweeper 2 - Return of the sweeper");
	{
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public GUI() {
		this.launchingThread = Thread.currentThread();
	}

	@Override
	public void update(Grid engine) {
		Container content = new Container();
		content.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		content.add(panel, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		content.add(panel_1, BorderLayout.CENTER);
		
		int cols = engine.getWidth();
		int rows = engine.getHeight();
		panel_1.setLayout(new GridLayout(rows, cols, 0, 0));
		
		for(int y = 0; y < engine.getHeight(); y++) {
			for(int x = 0; x < engine.getWidth(); x++) {
				final int xp = x, yp = y;
				
				Cell cell = engine.map.get(new Point().set(x,y));
				JButton minesweeperButton = new JButton(cell.toString());
				if(cell.isRevealed()) minesweeperButton.setEnabled(false);
				if(cell.isRevealed()) minesweeperButton.setBackground(new Color(
						(int) (255f * Math.sin(((float)cell.getNumberOfBombsSurroundingCell())/9f *  Math.PI/2F)),
						(int) (255f * Math.sin(((float)(9-cell.getNumberOfBombsSurroundingCell()))/18f *  Math.PI/2F)),
						0
					));
				minesweeperButton.setMargin(new Insets(0,0,0,0));
				minesweeperButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						guiSelectedPoint = new Point().set(xp,yp);
						synchronized (launchingThread){launchingThread.notify();}
					}
				});
				
				panel_1.add(minesweeperButton);
			}
		}
		
		frame.setContentPane(content);
        frame.setVisible(true);
	}

	
	private Point guiSelectedPoint;
	@SneakyThrows
	@Override
	public Point blockForInput() {
		Thread thread = Thread.currentThread();
		synchronized (thread){thread.wait();}
		return guiSelectedPoint; 
	}

	@Override
	public void gameComplete(boolean win) {
		frame.setVisible(false);
		frame.dispose();
	}

}
