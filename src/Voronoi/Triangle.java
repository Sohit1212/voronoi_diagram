package Voronoi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Triangle extends ArraySet<Point> {
	
	private int id;
	private Point circumcenter = null;
	
	private static int i = 0;
	public static boolean moreInfo = false;
	
	public Triangle(Point...vertices) {
		this(Arrays.asList(vertices));
	}
	
	public Point getOtherVertex(Point...bad) {
		Collection<Point> v = Arrays.asList(bad);
		for(Point p : this) 
			if(!v.contains(p)) return p;
		
		throw new NoSuchElementException("No such vertex found");
	}
	
	public boolean isNeighbour(Triangle t) {
		int count = 0;
		for(Point p : this) 
			if(t.contains(p)) count++;
		
		return count == 2;
	}
	
	public Triangle(Collection<? extends Point> collection) {
		super(collection);
		id = i++;
		if(this.size() != 3)
			throw new IllegalArgumentException("Triangle must have 3 vertices");
	}
	
	public ArraySet<Point> facetOpposite(Point vertex) {
		
		ArraySet<Point> set = new ArraySet<>(this);
		
		if(!set.remove(vertex))
			throw new IllegalArgumentException("The point is not a vertex of the triangle");
		
		return set;
	}
	public Point getCircumcenter() {
		if(circumcenter == null)
			circumcenter = Point.circumcenter(this.toArray(new Point[0]));
		return circumcenter;
	}
	
	
	@Override
	public String toString() {
		if(!moreInfo) return "Triangle "+id;
		return "Triangle "+id+super.toString();
	}
	
	@Override
	public boolean add(Point item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Point> iterator() {
		return new Iterator<Point>() {
			private Iterator<Point> it = Triangle.super.iterator();
			public boolean hasNext() { return it.hasNext(); }
			public Point next() { return it.next(); }
			public void remove() { throw new UnsupportedOperationException(); }
		};
	}

	@Override
	public boolean equals(Object o) {
		return (this == o);
	}

	@Override
	public int hashCode() {
		return (int)(id^(id>>>32));
	}

}
