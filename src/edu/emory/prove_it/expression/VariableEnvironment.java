package edu.emory.prove_it.expression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractListModel;


@SuppressWarnings("serial")
public class VariableEnvironment extends AbstractListModel<VariableExpression> implements List<VariableExpression> {
	private final ArrayList<VariableExpression> variables = new ArrayList<VariableExpression>();
	
	/**
	 * Produce a canonical reference to a provided variable
	 * 
	 * If var is already in the environment, return the copy from the
	 * environment. Otherwise, add var to the environment, then return it.
	 * @param var a variable expression
	 * @return the canonical reference to the same variable
	 */
	public VariableExpression get(VariableExpression var) {
		if (indexOf(var) > -1)
			return get(indexOf(var));
		else {
			add(var);
			return var;
		}	
	}
	
	@Override
	public VariableExpression getElementAt(int index) {
		return get(index);
	}

	@Override
	public int getSize() {
		return size();
	}

	@Override
	public boolean add(VariableExpression e) {
		boolean b = variables.add(e);
		if (b) fireIntervalAdded(this, size()-1, size()-1);
		return b;
	}

	@Override
	public void add(int index, VariableExpression element) {
		variables.add(index, element);
		fireIntervalAdded(this, index, index);
	}

	@Override
	public boolean addAll(Collection<? extends VariableExpression> c) {
		boolean b = variables.addAll(c);
		if (b) fireIntervalAdded(this, size()-c.size()-1, size()-1);
		return b;
	}

	@Override
	public boolean addAll(int index, Collection<? extends VariableExpression> c) {
		boolean b = variables.addAll(index, c);
		if (b) fireIntervalAdded(this, index, index+c.size()-1);
		return b;
	}

	@Override
	public void clear() {
		int s = size();
		variables.clear();
		fireIntervalRemoved(this, 0, s-1);
	}

	@Override
	public boolean contains(Object o) {
		return variables.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return variables.containsAll(c);
	}

	@Override
	public VariableExpression get(int index) {
		return variables.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return variables.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return variables.isEmpty();
	}

	@Override
	public Iterator<VariableExpression> iterator() {
		/* 
		 * TODO: Fire intervalRemoved event when the iterator removes an element from the list.
		 */
		return variables.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return variables.lastIndexOf(o);
	}

	@Override
	public ListIterator<VariableExpression> listIterator() {
		/*
		 * TODO: Fire relevant events when the iterator changes the list.
		 */
		return variables.listIterator();
	}

	@Override
	public ListIterator<VariableExpression> listIterator(int index) {
		/*
		 * TODO: Fire relevant events when the iterator changes the list.
		 */
		return variables.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		boolean b = variables.remove(o);
		if (index >= 0)
			fireIntervalRemoved(this, index, index);
		return b;
	}

	@Override
	public VariableExpression remove(int index) {
		VariableExpression v = variables.remove(index);
		fireIntervalRemoved(this, index, index);
		return v;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		/*
		 * TODO: This method doesn't fire the appropriate event.
		 */
		return variables.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		/*
		 * TODO: This method doesn't fire the appropriate event.
		 */
		return variables.retainAll(c);
	}

	@Override
	public VariableExpression set(int index, VariableExpression element) {
		VariableExpression v = variables.set(index, element);
		fireContentsChanged(this, index, index);
		return v;
	}

	@Override
	public int size() {
		return variables.size();
	}

	@Override
	public List<VariableExpression> subList(int fromIndex, int toIndex) {
		return variables.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return variables.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return variables.toArray(a);
	}
}
