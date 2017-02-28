import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;

public class UI {
	public static void main(String[] args) {
		//Create and setup JFrame
		JFrame frame = new JFrame("Playing Cards - Shockey Advanced Compsci");
		frame.setSize(600, 200);
		frame.setLocationRelativeTo(null);
		CardCanvas canvas;
		frame.add((canvas = new CardCanvas()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		//Setup resize listener
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						canvas.recalculate();
					}
				});
			}
		});
		canvas.recalculate();
	}
}
