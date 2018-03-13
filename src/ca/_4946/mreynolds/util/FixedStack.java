package ca._4946.mreynolds.util;

public class FixedStack<E extends Object> {

	private Object[] m_stack;
	private int m_capacity;
	private int m_head;
	private int m_size;

	public FixedStack(int cap) {
		m_capacity = cap;
		m_head = 0;
		m_size = 0;
		m_stack = new Object[m_capacity];
	}

	@SuppressWarnings("unchecked")
	public E pop() {
		if (m_size <= 0)
			return null;

		E val = (E) m_stack[m_head];
		m_size--;
		m_head = (m_head + m_capacity - 1) % m_capacity;
		return val;
	}

	public void push(E val) {
		m_head = (m_head + 1) % m_capacity;
		m_size = Math.min(m_capacity, m_size + 1);
		m_stack[m_head] = val;
	}

	public boolean isEmpty() {
		return m_size < 1;
	}
}
