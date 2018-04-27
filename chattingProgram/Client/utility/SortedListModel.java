package utility;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

public class SortedListModel extends AbstractListModel {
	  SortedSet<String> model;
	  int n;
	  public SortedListModel() {
	    model = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	  }
	  public SortedSet<String> getSortedSet()
	  {
		  return model;
	  }
	  public int getNum() {
		  return n;
	  }
	  public int getSize() {
	    return model.size();
	  }
	 
	  public Object getElementAt(int index) {
	    return model.toArray()[index];
	  }

	  public void add(String element) {
	    if (model.add(element)) {
	      fireContentsChanged(this, 0, getSize());
	  }
	}
	  public void clear() {
	    model.clear();
	    fireContentsChanged(this, 0, getSize());
	  }

	  public boolean contains(Object element) {
	    return model.contains(element);
	  }

	  public Object firstElement() {
	    return model.first();
	  }

	  public Iterator iterator() {
	    return model.iterator();
	  }

	  public Object lastElement() {
	    return model.last();
	  }

	  public boolean removeElement(Object element) {
	    boolean removed = model.remove(element);
	    if (removed) {
	      fireContentsChanged(this, 0, getSize());
	    }
	    return removed;
	  }
	}