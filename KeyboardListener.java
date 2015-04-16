package DragonPoopGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Hashtable;


//@SuppressWarnings("serial")
public class KeyboardListener extends JPanel {
	
	public KeyboardListener() {
		KeyListener listener = new GameKeyListener();
		addKeyListener(listener);
		setFocusable(true);
	}
	/*  TODO: add an instance of this to the frame
	public static void main(String[] args) {
		JFrame frame = new JFrame("Mini Tennis");
		KeyboardExample keyboardExample = new KeyboardListener();
		frame.add(keyboardExample);
		frame.setSize(200, 200);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	*/

	public class GameKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println("keyPressed="+KeyEvent.getKeyText(e.getKeyCode()));
			//TODO: notify all subscribers of the particular key
		}

		@Override
		public void keyReleased(KeyEvent e) {
			System.out.println("keyReleased="+KeyEvent.getKeyText(e.getKeyCode()));
			//TODO: notify all subscribers of the particular key
		}

		//private static Hashtable<KeyEvent><List>() TODO: add objects in so that they can be told about key events
		//List is of ActionListener objects, which have the method: actionPerformed, that can be called.
		
	}
}