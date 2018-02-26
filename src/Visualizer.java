

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JFrame;

import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;

@SuppressWarnings("serial")
public class Visualizer extends JFrame {
	int width = 1000;
	int height = 800;
	DriveAction path;

	public Visualizer(String fileName) throws IOException {
		Reader a = new InputStreamReader(Visualizer.class.getResourceAsStream(fileName));
		BufferedReader reader = new BufferedReader(a);
		String file = "";
		String line;
		while ((line = reader.readLine()) != null) {
			file += line + "\n";
		}
		reader.close();

		// System.out.println(file);

		path = TextFileDeserializer.deserialize(file);
//		path.printLeft();
//		path.printRight();

		setTitle(fileName);
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) throws IOException {
//		new Visualizer("/paths/CenterLanePathClose.txt");
		new Visualizer("/paths/CenterLanePathFar.txt");
	}
	
    public void paint(Graphics g){
        // Circular Surface
    	
    	for(int i = 0; i < path.getLeftPath().size(); i++) {
    		double scale = 50;
    		
    		Point l = new Point((int)(path.getLeftPath().get(i).x*scale), (int)(path.getLeftPath().get(i).y*scale) + height/2);
    		Point r = new Point((int)(path.getRightPath().get(i).x*scale), (int)(path.getRightPath().get(i).y*scale) + height/2);

    		Point c = new Point((l.x + r.x)/2, (l.y + r.y)/2);

    		g.setColor(Color.GREEN);
    		g.drawLine(l.x, l.y, r.x, r.y);
    		
        	g.setColor(Color.BLUE);
        	drawCircleByCenter(g, c.x, c.y, 1);
        	g.setColor(Color.RED);
            drawCircleByCenter(g, l.x, l.y , 1);
            drawCircleByCenter(g, r.x, r.y , 1);
    	}
        	
    }

    void drawCircleByCenter(Graphics g, int x, int y, int radius){
        g.drawOval(x-radius, y-radius, 2*radius, 2*radius);
    }

}
