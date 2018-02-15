/*
 * Created on 08.06.2005
 *
 */
package org.visnacom.model;

import java.util.*;
import java.util.Map.Entry;
import java.math.*;

/**
 * @author F. Pfeiffer
 * 
 * This class is an implementation of a doubly linked list using HashMaps for
 * getting positions.
 */
public class DLL implements List {

	// maps objects to their positions in list
	private HashMap pos;

	// number of elements in list
	private BigInteger bigSize;

	// the first and last position in list
	private DLLNode first, last;

	// used for indicating ConcurrentModification errors
	private Object changedBy;

	// used for indicating ConcurrentModification errors
	private Object changedByIter = null;

	/**
	 * Constructor.
	 *  
	 */
	public DLL() {
		pos = new HashMap();
		bigSize = new BigInteger("0");
		first = null;
		last = null;
		changedBy = new Object();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param dll
	 *            The list to be copied.
	 */
	public DLL(DLL dll) {
		pos = new HashMap();
		bigSize = new BigInteger("0");
		first = null;
		last = null;
		Iterator it = dll.iterator();
		while (it.hasNext()) {
			add(it.next());
		}
		changedBy = new Object();
	}

	/**
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return bigSize.intValue();
	}

	/**
	 * Returns the size as BigInteger.
	 * 
	 * @return Number of elements in list.
	 */
	public BigInteger bigSize() {

		return bigSize;
	}

	/**
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return first == null;
	}

	/**
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object arg0) {
		return pos.get(arg0) != null;
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator iterator() {

		return new DLLIterator(this);

	}

	/**
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		Object[] arr = new Object[this.size()];
		DLLNode iter = first;
		int index = 0;
		while (iter != null) {
			arr[index] = iter.element;
			index++;
			iter = iter.succ;
		}
		return arr;
	}

	/**
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	public Object[] toArray(Object[] arg0) {
		DLLNode iter = first;
		int index = 0;
		while (iter != null) {
			arg0[index] = iter.element;
			index++;
			iter = iter.succ;
		}
		return arg0;
	}

	/**
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object arg0) {
		if (contains(arg0)) {
			throw new IllegalArgumentException("Already in list!");
		}
		boolean res;
		if (bigSize().equals(new BigInteger("0"))) {
			res = addSucc(null, arg0);
		} else {
			res = addSucc(last.element, arg0);
		}
		assert checkConsistency();
		return res;
	}

	/**
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object arg0) {

		bigSize = bigSize.subtract(new BigInteger("1"));
		DLLNode position = (DLLNode) pos.get(arg0);
		if (position == null) {
			return false;
		}

		// standard dll remove

		DLLNode formerPred = position.pred;
		DLLNode formerSucc = position.succ;
		if (formerPred != null) {
			formerPred.succ = formerSucc;
		}
		if (formerSucc != null) {
			formerSucc.pred = formerPred;
		}

		if (first == position) {
			first = formerSucc;
		}

		if (last == position) {
			last = formerPred;
		}

		position.pred = null;
		position.succ = null;
		pos.remove(arg0);
		changedBy = new Object();
		changedByIter = null;
		assert checkConsistency();

		return true;
	}

	/**
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection arg0) {
		Iterator it = arg0.iterator();
		while (it.hasNext()) {
			if (pos.get(it.next()) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection arg0) {
		Iterator it = arg0.iterator();
		boolean res = true;
		while (it.hasNext()) {
			Object newObj = it.next();
			if (contains(newObj)) {
				throw new IllegalArgumentException("Already in list!");
			}
			boolean b;
			if (last != null) {
				b = addSucc(last.element, newObj);
			} else {
				b = addSucc(null, newObj);
			}
			res = res && b;
		}
		return res;
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int arg0, Collection arg1) {
		ListIterator it = this.listIterator(arg0);
		Iterator it2 = arg1.iterator();
		while (it2.hasNext()) {
			Object newObj = it2.next();
			it.add(newObj);
		}
		return true;
	}

	/**
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection arg0) {
		Iterator it = arg0.iterator();
		boolean res = true;
		while (it.hasNext()) {
			Object next = it.next();
			res = res && remove(next);
		}
		return res;
	}

	/**
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection arg0) {
		Iterator it = this.iterator();
		boolean res = true;
		while (it.hasNext()) {
			Object next = it.next();
			if (!arg0.contains(next)) {
				it.remove();
			}
		}
		return res;
	}

	/**
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		first = null;
		last = null;
		bigSize = new BigInteger("0");
		pos.clear();
		changedBy = new Object();
		changedByIter = null;
	}

	/**
	 * @see java.util.List#get(int)
	 */
	public Object get(int arg0) {
		Iterator it = this.iterator();
		int index = 0;
		while (it.hasNext()) {
			Object o = it.next();
			if (index == arg0) {
				return o;
			}
			index++;
		}
		return null;
	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int arg0, Object arg1) {
		ListIterator it = this.listIterator();
		int index = 0;
		while (it.hasNext()) {
			it.next();
			if (index == arg0) {
				it.set(arg1);
				return arg1;
			}
			index++;
		}
		return null;
	}

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int arg0, Object arg1) {
		if (contains(arg1)) {
			throw new IllegalArgumentException("Already in list!");
		}
		ListIterator it = this.listIterator(arg0);
		it.add(arg1);
		assert checkConsistency();

	}

	/**
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int arg0) {
		ListIterator it = this.listIterator();
		int index = 0;
		while (it.hasNext()) {
			Object o = it.next();
			if (index == arg0) {
				it.remove();
				assert checkConsistency();

				return o;
			}
			index++;
		}
		assert checkConsistency();

		return null;
	}

	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object arg0) {
		ListIterator it = this.listIterator();
		int index = 0;
		while (it.hasNext()) {
			Object o = it.next();
			if (o == arg0) {
				return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object arg0) {
		DLLNode iter = last;
		int index = size() - 1;
		while (last != null) {
			if (iter.element == arg0) {
				return index;
			}
			index--;
			iter = iter.pred;
		}
		return -1;
	}

	/**
	 * @see java.util.List#listIterator()
	 */
	public ListIterator listIterator() {
		return new DLLIterator(this);
	}

	/**
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator listIterator(int arg0) {
		ListIterator it = new DLLIterator(this);
		int index = 0;
		while (it.hasNext() && index < arg0) {
			it.next();
			index++;
		}
		return it;
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List subList(int arg0, int arg1) {
		ListIterator it = this.listIterator();
		DLL sublist = new DLL();
		int index = 0;
		while (it.hasNext()) {
			Object o = it.next();
			if (index <= arg1 && index >= arg0) {
				sublist.add(o);
			}
			index++;
		}
		return sublist;
	}

	/**
	 * Adds a new object as successor of another object.
	 * 
	 * @param p
	 *            The predecessor of new object.
	 * @param newObj
	 *            The new object.
	 * @return True if element was added.
	 */
	public boolean addSucc(Object p, Object newObj) {
		if (contains(newObj)) {
			throw new IllegalArgumentException("Already in list!");
		}

		DLLNode position = (DLLNode) pos.get(p);

		// no correct predecessor
		if (p != null && position == null) {
			return false;
		}

		DLLNode formerSucc = null;
		if (p != null) {
			formerSucc = position.succ;
		}

		DLLNode newDLL;

		if (p == null) {
			// added as first element
			newDLL = new DLLNode(null, first, newObj);
			first = newDLL;
		} else if (position == last) {
			// added as last element
			newDLL = new DLLNode(last, null, newObj);
			last = newDLL;

		} else {
			newDLL = new DLLNode(position, formerSucc, newObj);
		}
		pos.put(newObj, newDLL);

		if (bigSize().equals(new BigInteger("0"))) {
			first = newDLL;
			last = newDLL;
		}

		bigSize = bigSize.add(new BigInteger("1"));
		changedBy = new Object();
		changedByIter = null;
		assert checkConsistency();

		return true;
	}

	/**
	 * Adds a new object as predecessor of another object.
	 * 
	 * @param s
	 *            The successor of new object.
	 * @param newObj
	 *            The new object.
	 * @return True if element was added.
	 */
	public boolean addPred(Object s, Object newObj) {
		if (contains(newObj)) {
			throw new IllegalArgumentException("Already in list!");
		}

		DLLNode position = (DLLNode) pos.get(s);

		// no correct predecessor
		if (s != null && position == null) {
			return false;
		}

		DLLNode formerPred = null;
		if (s != null) {
			formerPred = position.pred;
		}

		DLLNode newDLL;

		if (s == null) {
			// added as last element
			newDLL = new DLLNode(last, null, newObj);
			last = newDLL;
		} else if (position == first) {
			//added as first element
			newDLL = new DLLNode(null, first, newObj);
			first = newDLL;
		} else {
			newDLL = new DLLNode(formerPred, position, newObj);
		}

		pos.put(newObj, newDLL);
		if (bigSize().equals(new BigInteger("0"))) {
			first = newDLL;
			last = newDLL;
		}
		bigSize = bigSize.add(new BigInteger("1"));
		changedBy = new Object();
		changedByIter = null;
		assert checkConsistency();

		return true;
	}

	/**
	 * Gets the predecessor of an element.
	 * 
	 * @param o
	 *            The predecessor of this object is looked for.
	 * @return The predecessor of given object.
	 */
	public Object getPred(Object o) {
		DLLNode position = (DLLNode) pos.get(o);
		if (position == null) {
			return null;
		}
		return position.pred;
	}

	/**
	 * Gets the predecessor element of a given object.
	 * 
	 * @param o
	 *            The predecessor of this object is looked for.
	 * @return Predecessor.
	 */
	public Object getPredEl(Object o) {
		DLLNode position = (DLLNode) pos.get(o);
		if (position == null) {
			return null;
		}
		if (position.pred != null) {
			return position.pred.element;
		}
		return null;
	}

	/**
	 * Gets the successor element of given object.
	 * 
	 * @param o
	 *            The successor element of this object is looked for.
	 * @return The successor element of given object.
	 */
	public Object getSuccEl(Object o) {

		DLLNode position = (DLLNode) pos.get(o);
		if (position == null) {
			return null;
		}
		if (position.succ != null) {
			return position.succ.element;
		}
		return null;
	}

	/**
	 * Gets the first element in this list.
	 * 
	 * @return The first element in this list.
	 */
	public Object getFirst() {
		if (first != null) {
			return first.element;
		}
		return null;
	}

	/**
	 * Gets the last element in this list.
	 * 
	 * @return The last element in this list.
	 */
	public Object getLast() {
		if (last != null) {
			return last.element;
		}
		return null;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "[";
		Iterator it = this.iterator();
		while (it.hasNext()) {
			Object next = it.next();
			if (next != null) {
				s += next.toString();
			} else {
				s += "null";
			}
			if (it.hasNext()) {
				s += ", ";
			}
		}
		s += "]";
		return s;
	}

	/**
	 * Testing.
	 * 
	 * @return True if test is passed.
	 */
	public boolean checkConsistency() {
		Iterator it = pos.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			assert entry.getKey() == ((DLLNode) entry.getValue()).element;
			assert entry.getKey() != null;
		}
		it = this.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			assert o != null;
			assert pos.containsKey(o);
		}
		assert pos.values().size() == size();
		assert size() >= 0;
		return true;
	}

	/**
	 * 
	 * @author F. Pfeiffer
	 * 
	 * Position of element in list.
	 */
	private class DLLNode {

		// predecessor and successor
		DLLNode pred, succ;

		// the element at this position in list
		Object element;

		/**
		 * Constructor.
		 *  
		 */
		public DLLNode() {
			pred = null;
			succ = null;
			element = null;
		}

		/**
		 * Constructor. Sets element.
		 * 
		 * @param el
		 *            The element to be placed in this node.
		 */
		public DLLNode(Object el) {
			pred = null;
			succ = null;
			element = el;
		}

		/**
		 * Constructor. Sets predecessor, successor and element.
		 * 
		 * @param p
		 *            Predecessor.
		 * @param s
		 *            Successor.
		 * @param el
		 *            Element.
		 */
		public DLLNode(DLLNode p, DLLNode s, Object el) {
			pred = p;
			succ = s;
			element = el;
			if (p != null) {
				p.succ = this;
			}
			if (s != null) {
				s.pred = this;
			}
		}

	}

	/**
	 * 
	 * @author F. Pfeiffer
	 * 
	 * Inmplements an iterator for DLL.
	 */
	private class DLLIterator implements ListIterator {

		// current position
		private DLLNode curr;

		// the list for which this is the iterator
		private DLL dll;

		// the last returned position
		private DLLNode lastReturned;

		// the position index
		private BigInteger index;

		// used for indication changes to list
		private Object change;

		/**
		 * Constructor.
		 * 
		 * @param d
		 *            Doubly linked list for which iterator is needed.
		 */
		public DLLIterator(DLL d) {
			dll = d;
			curr = dll.first;
			lastReturned = null;
			index = new BigInteger("0");
			change = changedBy;
		}

		/**
		 * @see java.util.ListIterator#hasNext()
		 */
		public boolean hasNext() {
			return curr != null;
		}

		/**
		 * @see java.util.ListIterator#next()
		 */
		public Object next() {
			if (changedBy != change && changedByIter != this) {
				throw new ConcurrentModDLLException(""
						+ "DLL Concurrent Modification");
			}
			DLLNode next = curr;
			curr = curr.succ;
			lastReturned = next;
			if (next != null) {
				index = index.add(new BigInteger("1"));
			}
			return next.element;
		}

		/**
		 * @see java.util.ListIterator#remove()
		 */
		public void remove() {
			dll.remove(lastReturned.element);
			//dll.bigSize = dll.bigSize.subtract(new BigInteger("1"));
			changedByIter = this;
			changedBy = new Object();
			assert checkConsistency();

		}

		/**
		 * @see java.util.ListIterator#previous()
		 */
		public Object previous() {
			Object prev = curr.pred;
			if (prev != null) {
				curr = curr.pred;
				lastReturned = curr;
				index = index.subtract(new BigInteger("1"));
				return curr.element;
			}
			return null;
		}

		/**
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex(Object o) {
			DLLNode iter = curr;
			int res = index.intValue();
			while (iter != null) {
				if (iter.element == o) {
					return res;
				}
				res++;
			}
			return -1;
		}

		/**
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		public void set(Object o) {
			DLLNode oldPosition = (DLLNode) pos.remove(o);
			if (oldPosition != null) {
				//throw new IllegalArgumentException("Already in list!");
				oldPosition.element = null;
			}
			pos.remove(lastReturned.element);
			lastReturned.element = o;
			pos.put(o, lastReturned);
			changedByIter = this;
			changedBy = new Object();
			//assert checkConsistency();
		}

		/**
		 * @see java.util.ListIterator#hasNext()
		 */
		public void add(Object o) {
			if (contains(o)) {
				throw new IllegalArgumentException("Already in list!");
			}
			if (lastReturned == null) {
				if (first == null) {
					dll.add(o);
				} else {
					addPred(first.element, o);
				}
			} else {
				dll.addSucc(lastReturned.element, o);
				//dll.bigSize = dll.bigSize.add(new BigInteger("1"));
			}
			lastReturned = (DLLNode) pos.get(o);
			changedByIter = this;
			changedBy = new Object();
			assert checkConsistency();

		}

		/**
		 * Not needed.
		 */
		public int nextIndex() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex() {
			return index.subtract(new BigInteger("1")).intValue();
		}

		/**
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious() {
			return curr.pred != null;
		}
	}

	/**
	 * @author F. Pfeiffer
	 * 
	 * Exception indicating ConcurrentModification error.
	 */
	private class ConcurrentModDLLException extends RuntimeException {

		/**
		 * Constructor.
		 * 
		 * @param err
		 *            Error message.
		 */
		public ConcurrentModDLLException(String err) {
			super(err);
		}

	}
}