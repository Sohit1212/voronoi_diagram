package Voronoi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class VoronoiApp extends javax.swing.JApplet
						implements Runnable, ActionListener, MouseListener{

	private boolean debug = false;
	private Component currentSwitch = null;
	
	private static String windowTitle = "Voronoi Diagram / Delaunay Triangulation";
	private JRadioButton voronoiButton = new JRadioButton("Voronoi Diagram");
	private JRadioButton delaunayButton = new JRadioButton("Delaunay Triangulation");
	private JButton clearButton = new JButton("Clear");
	private VoronoiPanel voronoiPanel = new VoronoiPanel(this);
	private JLabel circleSwitch = new JLabel("Show Empty Circles");
	private JLabel delaunaySwitch = new JLabel("Show Delaunay Edges");
	private JLabel voronoiSwitch = new JLabel("Show Voronoi Edges");
	

	public static void main(String[] args) {
		VoronoiApp vp = new VoronoiApp();
		vp.init();
		
		JFrame dWindow = new JFrame();
		dWindow.setSize(700,500);
		dWindow.setVisible(true);
		dWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dWindow.setTitle(windowTitle);
		dWindow.setLayout(new BorderLayout());
		
		dWindow.add(vp,"Center");
	}
	
	public void init() {
		try {
			SwingUtilities.invokeAndWait(this);
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Initialization Failure");
		}
	}

	@Override
	public void run() {
		setLayout(new BorderLayout());
		
		ButtonGroup group = new ButtonGroup();
		group.add(voronoiButton);
		group.add(delaunayButton);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(voronoiButton);
		buttonPanel.add(delaunayButton);
		buttonPanel.add(clearButton);
		this.add(buttonPanel, "North");
		
		JPanel switchPanel = new JPanel();
		switchPanel.add(circleSwitch);
		switchPanel.add(new Label("         "));
		switchPanel.add(delaunaySwitch);
		switchPanel.add(new Label("         "));
		switchPanel.add(voronoiSwitch);
		this.add(switchPanel, "South");
		
		voronoiPanel.setBackground(Color.gray);
		this.add(voronoiPanel, "Center");
		
		voronoiButton.addActionListener(this);
		delaunayButton.addActionListener(this);
		clearButton.addActionListener(this);
		voronoiPanel.addMouseListener(this);
		circleSwitch.addMouseListener(this);
		delaunaySwitch.addMouseListener(this);
		voronoiSwitch.addMouseListener(this);
		
		voronoiButton.doClick();
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {	}

	@Override
	public void mouseEntered(MouseEvent e) {
		currentSwitch = e.getComponent();
		if(currentSwitch instanceof JLabel) voronoiPanel.repaint();
		else currentSwitch = null;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		currentSwitch = null;
		if(e.getComponent() instanceof JLabel) voronoiPanel.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() != voronoiPanel) return;
		Point point = new Point(e.getX(),e.getY());
		
		if(debug) System.out.println("Click "+point);
		voronoiPanel.addPoint(point);
		voronoiPanel.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(debug) 
			System.out.println(((AbstractButton)e.getSource()).getText());
		if(e.getSource() == clearButton) voronoiPanel.clear();
		voronoiPanel.repaint();
	}
	
	public boolean isVoronoi() {
		//System.out.println(voronoiButton.isSelected());
		return voronoiButton.isSelected();
	}
	
	public boolean isDelauany() {
		return delaunayButton.isSelected();
	}
	
	public boolean showingCircles() {
		return currentSwitch == circleSwitch;
	}
	
	public boolean showingVoronoi() {
		return currentSwitch == voronoiSwitch;
	}
	
	public boolean showingDelaunay() {
		return currentSwitch == delaunaySwitch;
	}
	
}
