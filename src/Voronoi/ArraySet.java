package Voronoi;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An ArrayList implementation of set.
 * Efficient for creating a set with small number of points
 * 
 * @author sohit
 *
 */
public class ArraySet<E> extends AbstractSet<E> {

	private ArrayList<E> items;
	
	public ArraySet() {
		items = new ArrayList<E>(3);
	}
	
	public ArraySet(int capacity) {
		items = new ArrayList<E>(capacity);
	}
	
	public ArraySet(Collection<? extends E> collection) {
		
		items = new ArrayList<E>(collection.size());
		
		for(E o : collection) {
			if(!items.contains(o)) items.add(o);
		}
	}
	
	public E get(int id) throws IndexOutOfBoundsException {
		return items.get(id);
	}
	
	public boolean containsAny(Collection<?> collection) {
		
		for(Object o : collection) 
			if(items.contains(o)) return true;
		
		return false;
	}
	public boolean add(E item) {
		if(items.contains(item)) return false;
		return items.add(item);
	}
	
	@Override
	public Iterator<E> iterator() {
		return items.iterator();
	}

	@Override
	public int size() {
		return items.size();
	}
	
	
}
