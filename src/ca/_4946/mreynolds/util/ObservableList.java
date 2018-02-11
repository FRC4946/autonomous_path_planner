package ca._4946.mreynolds.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.event.EventListenerList;

import ca._4946.mreynolds.util.ObservableElement.ElementListener;

public class ObservableList<E> implements List<E> {

	public interface ListListener extends EventListener {
		public void listChanged();
	}

	private ArrayList<E> list;
	private boolean isQuiet = false;

	public ObservableList() {
		list = new ArrayList<>();
	}

	public void quiet() {
		isQuiet = true;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(E e) {
		if (e instanceof ObservableElement)
			((ObservableElement) e).addElementListener(elementChangeListener);

		boolean r = list.add(e);

		fireListChanged();
		return r;
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof ObservableElement)
			((ObservableElement) o).removeElementListener(elementChangeListener);

		boolean r = list.remove(o);

		fireListChanged();
		return r;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c)
			if (e instanceof ObservableElement)
				((ObservableElement) e).addElementListener(elementChangeListener);

		boolean r = list.addAll(c);

		fireListChanged();
		return r;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		for (E e : c)
			if (e instanceof ObservableElement)
				((ObservableElement) e).addElementListener(elementChangeListener);

		boolean r = list.addAll(index, c);

		fireListChanged();
		return r;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object o : c)
			if (o instanceof ObservableElement)
				((ObservableElement) o).removeElementListener(elementChangeListener);

		fireListChanged();
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {

		// TODO: Fix
		fireListChanged();
		return list.retainAll(c);
	}

	@Override
	public void clear() {
		for (E e : list)
			if (e instanceof ObservableElement)
				((ObservableElement) e).removeElementListener(elementChangeListener);
		list.clear();
		fireListChanged();
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public E set(int index, E e) {
		E old = list.set(index, e);
		if (old instanceof ObservableElement)
			((ObservableElement) old).removeElementListener(elementChangeListener);
		if (e instanceof ObservableElement)
			((ObservableElement) e).addElementListener(elementChangeListener);
		fireListChanged();
		return old;
	}

	@Override
	public void add(int index, E e) {
		list.add(index, e);

		if (e instanceof ObservableElement)
			((ObservableElement) e).addElementListener(elementChangeListener);

		fireListChanged();
	}

	@Override
	public E remove(int index) {
		E prev = list.remove(index);

		if (prev instanceof ObservableElement)
			((ObservableElement) prev).removeElementListener(elementChangeListener);
		fireListChanged();

		return prev;
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	ElementListener elementChangeListener = new ElementListener() {
		@Override
		public void pointChanged() {
			fireListChanged();
		}
	};

	private EventListenerList listenerList = new EventListenerList();

	public void addListListener(ListListener l) {
		listenerList.add(ListListener.class, l);
	}

	public void removeListListener(ListListener l) {
		listenerList.remove(ListListener.class, l);
	}

	protected void fireListChanged() {
		if (isQuiet) {
			isQuiet = false;
			return;
		}

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListListener.class) {
				((ListListener) listeners[i + 1]).listChanged();
			}
		}
	}
}
