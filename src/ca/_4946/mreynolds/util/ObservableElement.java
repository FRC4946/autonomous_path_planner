package ca._4946.mreynolds.util;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

public class ObservableElement {

	public interface ElementListener extends EventListener {
		public void changed();
	}

	private EventListenerList listenerList = new EventListenerList();

	public void addElementListener(ElementListener l) {
		listenerList.add(ElementListener.class, l);
	}

	public void removeElementListener(ElementListener l) {
		listenerList.remove(ElementListener.class, l);
	}

	protected void fireElementChanged() {

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ElementListener.class) {
				((ElementListener) listeners[i + 1]).changed();
			}
		}
	}

}
