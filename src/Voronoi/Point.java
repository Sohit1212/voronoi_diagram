package Voronoi;

/**
 * Points in Euclidean space implemented as double[]
 * 
 * Implemented various simple geometeric operations
 * 
 * Used matrix and simplex as an array of Points.
 * 
 * @author sohit
 *
 */

public class Point {
	
	private double[] coordinates;
	
	public Point(double... coords) {
		
		coordinates = new double[coords.length];
		
		System.arraycopy(coords, 0, coordinates, 0, coords.length);
	}


	@Override
	public String toString() {
		
		if(coordinates.length == 0) return "Point()";
		
		String res = "Point("+coordinates[0];
		
		for(int i=1;i<coordinates.length;i++) {
			res = res + "," + coordinates[i];
		}
		
		res = res+")";
		return res;
	}


	@Override
	public boolean equals(Object other) {
		
		if(!(other instanceof Point)) return false;
		
		Point p = (Point)other;
		
		if(this.coordinates.length != p.coordinates.length) return false;
		
		for(int i=0;i<p.coordinates.length;i++) {
			if(this.coordinates[i] != p.coordinates[i]) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		
		return super.hashCode();
	}
	
	public double coord(int i) {
		int dim = this.dimension();
		if(i>=dim) throw new ArrayIndexOutOfBoundsException("Invalid coordinate");
		return this.coordinates[i];
	}
	
	public int dimension() {
		return this.coordinates.length;
	}
	
	public int dimCheck (Point p) {
        int len = this.coordinates.length;
        if (len != p.coordinates.length)
            throw new IllegalArgumentException("Dimension mismatch");
        return len;
    }
	
	public double dot(Point p) {
		int len = dimCheck(p);
		double sum = 0;
		
		for(int i=0;i<len;i++) {
			sum += this.coordinates[i]*p.coordinates[i];
		}
		
		return sum;
	}
	
	public double magnitude () {
        return Math.sqrt(this.dot(this));
    }
	
	public Point subtract (Point p) {
        int len = dimCheck(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++)
            coords[i] = this.coordinates[i] - p.coordinates[i];
        return new Point(coords);
    }
	
	public Point add (Point p) {
        int len = dimCheck(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++)
            coords[i] = this.coordinates[i] + p.coordinates[i];
        return new Point(coords);
    }
	
	public double angle(Point p) {
		return Math.acos(this.dot(p)/(this.magnitude()*p.magnitude()));
	}
	
	public Point extend(double... coords) {
		double[] res = new double[coordinates.length+coords.length];
		System.arraycopy(coordinates, 0, res, 0, coordinates.length);
		System.arraycopy(coords, 0, res, coordinates.length, coords.length);
		return new Point(res);
	}
	
	public Point bisector(Point p) {
		dimCheck(p);
		Point diff = this.subtract(p);
		Point sum = this.add(p);
		double dot = diff.dot(sum);
		
		return diff.extend(-dot/2);
	}
	
	public static String toString(Point[] matrix) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("{");
		for(Point row : matrix) buf.append(" "+row);
		buf.append("}");
		return buf.toString();
	}
	
	public static double determinant(Point[] matrix) {
		if(matrix.length != matrix[0].dimension()) {
			throw new IllegalArgumentException("Matrix is not square");
		}
		
		boolean[] columns = new boolean[matrix.length];
		
		for(int i=0;i<matrix.length;i++) columns[i] = true;
		
		try { return determinant(matrix,0,columns); }
		catch(ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Invaid Matrix");
		}
	}
	
	public static double determinant(Point[] matrix, int row, boolean[] columns) {
		
		if(row == matrix.length) return 1;
		
		Point point = matrix[row];
		double[] coords = point.coordinates;
		double sum = 0;
		int sign = 1;
		
		for(int i=0;i<columns.length;i++) {
			if(!columns[i]) continue;
			columns[i] = false;
			sum += (sign*coords[i]*determinant(matrix,row+1,columns));
			sign *= -1;
			columns[i] = true;
		}
		return sum;
	}
	
	public static Point cross(Point[] matrix) {
		
		int len = matrix.length+1;
		if(len != matrix[0].dimension()) 
			throw new IllegalArgumentException("Invalid set of points");
		boolean[] columns = new boolean[len];
		for(int i=0;i<len;i++) columns[i] = true;
		
		double[] result = new double[len];
		int sign = 1;
		try {
			for(int i=0;i<len;i++) {
				columns[i] = false;
				result[i] = sign*determinant(matrix,0,columns);
				sign*=-1;
				columns[i] =true;
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Invalid set of points");
		}
		return new Point(result);
	}
	
	public double content(Point[] simplex) {
		int len = simplex.length;
		Point[] matrix = new Point[len];
		
		for(int i=0;i<len;i++) 
			matrix[i] = simplex[i].extend(1);
		
		int fact = 1;
		
		for(int i=1;i<=len;i++) fact*=i;
		
		return determinant(matrix)/fact;
	}
	
	public int[] relation(Point[] simplex) {
		
		int dim = simplex.length-1;
	
		if(this.dimension() != dim) 
			throw new IllegalArgumentException("Invalid simplex");
		
		Point[] matrix = new Point[dim+1];
		
		double[] coords = new double[dim+2];
		
		for(int i=0;i<dim+2;i++) coords[i] = 1;
		
		matrix[0] = new Point(coords);
		
		for(int i=1;i<=dim;i++) {
			coords[0] = this.coordinates[i-1];
			for(int j=1;j<=(dim+1);j++) coords[j] = simplex[j-1].coordinates[i-1];
			matrix[i] = new Point(coords);
		}
		
		Point vector = cross(matrix);
		double content = vector.coordinates[0];
		
		int[] result = new int[dim+1];
		
		for(int i=0;i<(dim+1);i++) {
			
			double value = vector.coordinates[i+1];
			
			if(Math.abs(value) <= 1.0e-6*Math.abs(content)) result[i] = 0;
			else if(value < 0) result[i] = -1;
			else result[i] = 1;
		}
		
		if(content < 0) 
			for(int i=0;i<=dim;i++) result[i] = -result[i];
		if(content == 0) 
			for(int i=0;i<=dim;i++) result[i] = Math.abs(result[i]);
		return result;
	}
	
	public Point isOutside(Point[] simplex) {
		int[] result = this.relation(simplex);
		
		for(int i=0;i<result.length;i++) 
			if(result[i] > 0) return simplex[i];
		
		return null;
	}
	
	public boolean isInside(Point[] simplex) {
		int[] result = this.relation(simplex);
		
		for(int i=0;i<result.length;i++) 
			if(result[i] >= 0) return false;
		
		return true;
	}
	
	public Point isOn(Point[] simplex) {
		int[] result = this.relation(simplex);
		
		Point p = null;
		
		for(int i=0;i<result.length;i++) {
			if(result[i] == 0) p = simplex[i];
			if(result[i] > 0) return null;
		}
		
		return p;
	}
	
	public int circumcircle(Point[] simplex) {
		
		int len = simplex.length;
		
		Point[] matrix = new Point[len+1];
		
		for(int i=0;i<len;i++) {
			matrix[i] = simplex[i].extend(1,simplex[i].dot(simplex[i]));
		}
		
		matrix[len] = this.extend(1,this.dot(this));
		
		double res = determinant(matrix);
		
		int result = res < 0 ? -1 : (res > 0 ? 1 : 0); 
		
		if(content(simplex) < 0) result*=-1;
		
		return result;
	}
	
	public static Point circumcenter(Point[] simplex) {
		
		int dim = simplex[0].dimension();
		if(simplex.length-1 != dim) 
			throw new IllegalArgumentException("Invalid Points");
		
		Point[] matrix = new Point[dim];
		
		for(int i=0;i<dim;i++) 
			matrix[i] = simplex[i].bisector(simplex[i+1]);
		
		Point h = cross(matrix);
		
		double last = h.coordinates[dim];
		
		double[] result = new double[dim];
		
		for(int i=0;i<dim;i++) 
			result[i] = h.coordinates[i]/last;
		
		return new Point(result);
	}
	
}
