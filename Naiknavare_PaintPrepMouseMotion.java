/* Karan Naiknavare
 * Paint Prep Part 2, Mouse Motion
 * Creates a paint GUI that can take in a file and redraw it, save a file, impose a file or user can create their own drawing to their preference.
 */
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.*;
import java.io.IOException;
import java.util.*;

public class Naiknavare_PaintPrepMouseMotion extends JFrame implements ActionListener{
	
	private Color currentColor;
	private JColorChooser colorChooser;
	private int currentSize;
	private DrawPanel canvas;
	
	public Naiknavare_PaintPrepMouseMotion() {
		
		//sets the title, size, exit on close and location
		setTitle("Pain Prep Menus");
		setSize(400,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		//sets the look and feel to Windows version
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch(Exception e) {
			System.out.println("That look and feel is not found");
			System.exit(-1);
		}
		
		//setting up IV's
		JButton clear= new JButton("Clear");
		clear.setBounds(0,580,400,60);
		
		colorChooser= new JColorChooser();
		currentColor = Color.black;
		currentSize=10;
		
		canvas = new DrawPanel();
		canvas.setBounds(0,0,400,610);
		
		//menu bar
		JMenuBar menuBar = new JMenuBar();
		
		//Menu for 'file' and its Menu items
		JMenu file = new JMenu("File");
		JMenuItem load= new JMenuItem("Load");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem impose = new JMenuItem("Impose");
		
		file.add(load);
		file.add(save);
		file.add(impose);
		
		//menu for 'options' and its menuItems
		JMenu options= new JMenu("Options");
		JMenuItem color =new JMenuItem("Color");
		
		JMenu size= new JMenu("Size");
		JMenuItem size10= new JMenuItem("10");
		JMenuItem size25= new JMenuItem("25");
		JMenuItem size50= new JMenuItem("50");
			
		size.add(size10);
		size.add(size25);
		size.add(size50);
		
		//adds size menu to options menu
		options.add(color);
		options.add(size);
		
		//adds all Menus to the menu bar
		menuBar.add(file);
		menuBar.add(options);
		
		setJMenuBar(menuBar); 
	
		//JMenu Items that trigger events
		load.addActionListener(this);
		save.addActionListener(this);
		impose.addActionListener(this);
		color.addActionListener(this);
		size10.addActionListener(this);
		size25.addActionListener(this);
		size50.addActionListener(this);
		clear.addActionListener(this);	
		
		add(clear);
		add(canvas);
		//add(mainPanel);
		setVisible(true);
	}
	
	//initiates loading in a file, saving a file, choosing a color and many other options.
	public void actionPerformed(ActionEvent ae){
		
		JFileChooser newone= new JFileChooser();
		int result=0;
		
		//loads in a piece of work
		if(ae.getActionCommand().equals("Load")) {
			canvas.clear();
			result= newone.showOpenDialog(null);
			openFile(newone,result);
		}
		
		//saves the work that you want to save in correct place
		else if(ae.getActionCommand().equals("Save")) {
			result= newone.showSaveDialog(null);
			saveFile(newone,result);
		}
		
		//opens the file without clearing what was there before it
		else if(ae.getActionCommand().equals("Impose")) {
			result= newone.showOpenDialog(null);
			openFile(newone,result);
		}
	
		//add if statement to check for null thing
		else if(ae.getActionCommand().equals("Color")) {
			
			JColorChooser chooser = new JColorChooser();
			Color newcolor = chooser.showDialog(null, "Select a color", null);
			
			if(newcolor!=null) {
				currentColor = newcolor;
			}
		}
		
		//clears and resets the page
		else if(ae.getActionCommand().equals("Clear")) {
			canvas.clear();
		}
		
		//changes the size of the circles drawn
		else if(ae.getActionCommand().equals("10")) {
			currentSize=10;
		}
		else if(ae.getActionCommand().equals("25")) {
			currentSize=25;
		}
		else if(ae.getActionCommand().equals("50")) {
			currentSize=50;
		}
	}
	
	//helper function that draws a file that is given
	private void openFile(JFileChooser newone,int result) {
		
		File f=null;
		Scanner sc = null;
		
		//if the option is approved, it can pull the file
		if(result == JFileChooser.APPROVE_OPTION){
			f = newone.getSelectedFile();
			
			//read and display the data from the file
			try{
				sc = new Scanner(f);
			}
			catch(FileNotFoundException e){
				System.out.println("File Not Found!");
				System.exit(-1);
				
			}
			
			while(sc.hasNextInt()){
				
				//takes coordinates with the first two numbers
				int x=sc.nextInt();
				int y=sc.nextInt();
				
				//creates a color variable with the next 3 numbers
				Color c=new Color(sc.nextInt(),sc.nextInt(),sc.nextInt());
		
				//creates a point with the first two numbers, color variable and the nextInt which is the size, then repaints.
				canvas.points.add(new Point(x,y,c,sc.nextInt()));
				this.repaint();
			}
		}
	}
	
	//should save the file
	private void saveFile(JFileChooser newone,int result) {
		
		File f=null;
		
		if(result == JFileChooser.APPROVE_OPTION){
			f = newone.getSelectedFile();
			
			//read and display the data from the file
			try{
				FileWriter outFile = new FileWriter(f);
				
				//adds all the points,color and size to the file going out.
				for(int i = 0; i < canvas.points.size(); i++){
					
					outFile.write( canvas.points.get(i)+"\r\n");
				}
				outFile.close();
			}
			catch(IOException e){
				System.out.println("IO issue");
				System.exit(-1);
			}
		}
	}
	
	//point class, creates a point with x,y,color and size
	public class Point {
		
		private int x;
		private int y;
		private Color ptColor;
		int ptSize;
		
		public Point(int x1, int y1, Color c, int size) {
			x=x1;
			y=y1;
			ptColor=c;
			ptSize=size;
		}
	}
	
	public class DrawPanel extends JPanel implements MouseMotionListener{
		
		private ArrayList<Point> points;
		
		//stores points to draw a polygon
		public DrawPanel(){
			this.addMouseMotionListener(this);
			points = new ArrayList<Point>();	
		}
		
		//function that clears everything and reverts back to normal
		public void clear() {
			points.clear();
			currentSize=10;
			currentColor=Color.black;
			this.repaint();
		}
		
		public void paintComponent(Graphics g){
			
			super.paintComponent(g);
			setBackground(Color.white);
			
			//draw a point, updating each color as it goes.
			for(Point nextP: points) {
				g.setColor(nextP.ptColor);
				g.fillOval(nextP.x, nextP.y, nextP.ptSize, nextP.ptSize);
			}
		}

		//adds the points
		public void mouseDragged(MouseEvent me) {
			points.add(new Point(me.getX(),me.getY(),currentColor,currentSize));
			this.repaint();
		}
		
		//function not used
		public void mouseMoved(MouseEvent arg0) {
		}
	}
		
	public static void main(String[] args){
		new Naiknavare_PaintPrepMouseMotion();
	}
}


