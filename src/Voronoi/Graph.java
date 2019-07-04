package Voronoi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Undirected Graph implementation
 * @author sohit
 *
 */

public class Graph<N> {
	
	private Map<N,Set<N>> edges = new HashMap<N,Set<N>>();
	private Set<N> nodes = Collections.unmodifiableSet(edges.keySet());
	
	public void add(N node) {
		if(edges.containsKey(node)) return;
		edges.put(node,new ArraySet<N>());
	}
	
	public void add(N nodeA, N nodeB) throws NullPointerException {
		edges.get(nodeA).add(nodeB);
		edges.get(nodeB).add(nodeA);
	}
	
	public void remove(N node) {
		if(!edges.containsKey(node)) return;
		
		for(N adj : edges.get(node)) 
			edges.get(adj).remove(node);
		
		edges.get(node).clear();
		edges.remove(node);
		
	}
	
	public void remove(N nodeA, N nodeB) {
		edges.get(nodeA).remove(nodeB);
		edges.get(nodeB).remove(nodeA);
	}
	
	public Set<N> getNeighbours(N node) throws NullPointerException {
		return  Collections.unmodifiableSet(edges.get(node));
	}
	
	public Set<N> getNodes() {
		return nodes;
	}
	
}
