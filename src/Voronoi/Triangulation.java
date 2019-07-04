package Voronoi;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 * A 2D Delaunay Triangulation using an incremental Algorithm
 * @author sohit
 *
 */

public class Triangulation extends AbstractSet<Triangle>{
	
	private Triangle mostRecent = null;
	private Graph<Triangle> triGraph;
	
	public Triangulation(Triangle t) {
		triGraph = new Graph<>();
		triGraph.add(t);
		mostRecent = t;
	}
	public void delaunayPlace(Point point) {
		
		Triangle tri = locate(point);
		//System.out.println(tri);
		if(tri == null) 
			throw new IllegalArgumentException("No containing triangle");
		else if(tri.contains(point)) return ;
		
		Set<Triangle> cavity = getCavity(point, tri);
		mostRecent = update(point,cavity);
	}
	
	public Triangle update(Point point, Set<Triangle> cavity) {
		Set<Set<Point>> boundary = new HashSet<Set<Point>>();
		Set<Triangle> theTriangles = new HashSet<>();
		
		for(Triangle tri : cavity) {
			theTriangles.addAll(neighbours(tri));
			for(Point p : tri) {
				Set<Point> set = tri.facetOpposite(p);
				if(!boundary.contains(set))
					boundary.add(set);
				else boundary.remove(set);
			}
		}
		
		theTriangles.removeAll(cavity);
		
		for(Triangle t : cavity) triGraph.remove(t);
		
		Set<Triangle> newTriangles = new HashSet<>();
		
		for(Set<Point> set : boundary) {
			set.add(point);
			Triangle t = new Triangle(set);
			triGraph.add(t);
			newTriangles.add(t);
		}
		
		theTriangles.addAll(newTriangles);
		
		for(Triangle t1 : newTriangles) 
			for(Triangle t2 : theTriangles) 
				if(t1.isNeighbour(t2)) 
					triGraph.add(t1, t2);
		
		return newTriangles.iterator().next();
	}
	
	public Set<Triangle> getCavity(Point point, Triangle triangle) {
		
		Set<Triangle> set = new HashSet<>();
		Queue<Triangle> queue = new LinkedList<>();
		Set<Triangle> visited = new HashSet<>();
		
		queue.add(triangle);
		visited.add(triangle);
		
		while(!queue.isEmpty()) {
			
			triangle = queue.remove();
			
			if(point.circumcircle(triangle.toArray(new Point[0])) == 1) 
				continue;
			else set.add(triangle);
			
			for(Triangle neighbour : triGraph.getNeighbours(triangle))
				if(!visited.contains(neighbour)) {
					queue.add(neighbour);
					visited.add(neighbour);
				}
		}
		return set;
	}
	
	public Triangle locate(Point point) {
		Triangle triangle = mostRecent;
		if(!this.contains(triangle)) triangle = null;
		
		Set<Triangle> visited = new HashSet<Triangle>();
		
		while(triangle != null) {
			
			if(visited.contains(triangle)) break;
			
			visited.add(triangle);
			
			Point location = point.isOutside(triangle.toArray(new Point[0]));
			
			if(location == null) return triangle;
			
			triangle = this.oppositeNeighbour(location,triangle);
		}
		//System.out.println("Warning: Checking all triangles for " + point);
		for(Triangle t : this) {
			if(point.isOutside(t.toArray(new Point[0])) == null ) return t;
		}
		
		//System.out.println("Warning: No triangle holds " + point);
		return null;
	}
	
	public List<Triangle> surroundingTriangles(Point point, Triangle triangle) {
		
		if(!triangle.contains(point))
			throw new IllegalArgumentException("Point is not a vertex of Triangle");
		
		List<Triangle> list = new ArrayList<Triangle>();
		Triangle start = triangle;
		
		Point other = triangle.getOtherVertex(point); // this point decides whether cc or ccw
		while(true) {
			list.add(triangle);
			Triangle prev = triangle;
			triangle = this.oppositeNeighbour(other,triangle); //next triangle
			other = prev.getOtherVertex(point,other); //next vertex
			if(triangle == start) break;
		}
		
		return list;
	}
	
	public Set<Triangle> neighbours(Triangle triangle) {
		if(!triGraph.getNodes().contains(triangle))
			throw new NoSuchElementException("No such triangle exists"); 
		return triGraph.getNeighbours(triangle);
	}
	
	public Triangle oppositeNeighbour(Point point, Triangle triangle) {
		
		if(!triangle.contains(point)) 
			throw new NoSuchElementException("the triangle does not contain the given point");
		
		for(Triangle neighbour : triGraph.getNeighbours(triangle)) 
			if(!neighbour.contains(point)) return neighbour;
		
		return null;
	}
	
	@Override
	public Iterator<Triangle> iterator() {
		return triGraph.getNodes().iterator();
	}

	@Override
	public String toString() {
		return "Triangulation with "+size()+" triangles";
	}

	@Override
	public int size() {
		return triGraph.getNodes().size();
	}
	
	public boolean contains(Object t) {
		return triGraph.getNodes().contains(t);
	}
	
}
