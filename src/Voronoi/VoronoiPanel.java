package Voronoi;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class VoronoiPanel extends JPanel {
	
	public static Color voronoiColor = Color.LIGHT_GRAY;
	public static Color delaunayColor = Color.ORANGE;
	public static int pointRadius = 3;
	
	private VoronoiApp controller;
	private Triangulation dt;
	private Map<Object,Color> colorTable;
	private Triangle initialTriangle;
	private int initialSize = 10000;
	private Graphics g;
	private Random random = new Random();
	
	public VoronoiPanel(VoronoiApp controller) {
		this.controller = controller;
		initialTriangle = new Triangle(
			new Point(-initialSize,-initialSize),
			new Point(initialSize,-initialSize),
			new Point(0,initialSize)
		);
		
		dt = new Triangulation(initialTriangle);
		colorTable = new HashMap<Object,Color>();
	}
	
	public void addPoint(Point point) {
		dt.delaunayPlace(point);
	}
	
	public void clear() {
		dt = new Triangulation(initialTriangle);
	}
	
	public Color getColor(Object item) {
		if(colorTable.containsKey(item)) return colorTable.get(item);
		Color color = new Color(Color.HSBtoRGB(random.nextFloat(), random.nextFloat(), 1.0f));
		colorTable.put(item, color);
		return color;
	}
	
	//draw a point
	public void draw(Point p ) {
		int r = pointRadius;
		int x = (int)p.coord(0);
		int y = (int)p.coord(1);
		
		g.fillOval(x-r, y-r, r+r, r+r);
	}
	
	//draw a circle
	public void draw(Point center, double radius, Color color) {
		int x = (int)center.coord(0);
		int y = (int)center.coord(1);
		int r = (int) radius;
		
		if(color != null) {
			Color tmp = g.getColor();
			g.setColor(color);
			g.fillOval(x-r, y-r, r+r, r+r);
			g.setColor(tmp);
		}
		
		g.drawOval(x-r, y-r, r+r, r+r);
	}
	
	//draw polygon
	public void draw(Point[] polygon, Color color) {
		
		int n = polygon.length;
		int[] x = new int[n];
		int[] y = new int[n];
		
		for(int i=0;i<n;i++) x[i] = (int) polygon[i].coord(0);
		for(int i=0;i<n;i++) y[i] = (int) polygon[i].coord(1);
		
		if(color != null) {
			Color tmp = g.getColor();
			g.setColor(color);
			g.fillPolygon(x, y, n);
			//System.out.println("HEre");
			g.setColor(tmp);
		}
		
		g.drawPolygon(x, y, n);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g = g;
		
		Color temp = g.getColor();
		if(!controller.isVoronoi()) g.setColor(delaunayColor);
		else if(dt.contains(initialTriangle)) g.setColor(this.getBackground());
		else g.setColor(voronoiColor);
		
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(temp);
		
		if(controller.isVoronoi()) {
			//System.out.println("There");
			drawAllVoronoi(true,true);
		}
		else drawAllDelaunay(true);
		
		temp = g.getColor();
		g.setColor(Color.white);
		if(controller.showingCircles()) {
			drawAllCircles();
			System.out.println("HEre");
		}
		if(controller.showingVoronoi()) drawAllVoronoi(false,false);
		if(controller.showingDelaunay()) drawAllDelaunay(false);
		
		g.setColor(temp);
		
	}

	private void drawAllDelaunay(boolean fill) {
		for(Triangle tri : dt) {
			Point[] vertices = tri.toArray(new Point[0]);
			draw(vertices ,fill ? getColor(tri) : null);
		}
	}

	private void drawAllCircles() {
		
		for(Triangle tri : dt) {
			if(tri.containsAny(initialTriangle)) continue;
			Point p = tri.getCircumcenter();
			double radius = p.subtract(tri.get(0)).magnitude();
			draw(p,radius,null);
		}
		
	}

	private void drawAllVoronoi(boolean site , boolean fill) {
		HashSet<Point> done = new HashSet<>(initialTriangle);
		for(Triangle tri : dt) 
			for(Point p : tri) {
				if(done.contains(p)) continue;
				done.add(p);
				List<Triangle> T = dt.surroundingTriangles(p, tri);
				//System.out.println(T.size());
				Point[] P = new Point[T.size()];
				int i=0;
				for(Triangle t : T) {
					//System.out.println(t.getCircumcenter());
					P[i++] = t.getCircumcenter();
				}
				
				draw(P,fill ? getColor(p) : null);
				if(site) draw(p);
				
			}
	}

}
