package edu.hendrix.ev3.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public class FixedSizeArray<T> implements DeepCopyable<FixedSizeArray<T>> {
	private T[] array;
	private BitSet availableIndices;
	private UnaryOperator<T> copier;
	
	@SuppressWarnings("unchecked")
	public FixedSizeArray(int size, UnaryOperator<T> copier) {
		array = (T[])new Object[size];
		this.copier = copier;
		availableIndices = new BitSet();
		availableIndices.set(0, size);
	}
	
	public int capacity() {
		return array.length;
	}
	
	public int size() {
		return capacity() - availableIndices.cardinality();
	}
	
	public void put(int i, T value) {
		array[i] = value;
		availableIndices.clear(i);
	}
	
	public void add(T value) {
		if (isFull()) {
			throw new IllegalStateException("Array is full");
		}
		put(getLowestAvailable(), value);
	}
	
	public T remove(int i) {
		T value = array[i];
		array[i] = null;
		availableIndices.set(i);
		return value;
	}
	
	public T get(int i) {
		return array[i];
	}
	
	public boolean isAvailable(int i) {
		return availableIndices.get(i);
	}
	
	public boolean containsKey(int i) {
		return !isAvailable(i);
	}
	
	public boolean isFull() {
		return getLowestAvailable() == -1;
	}
	
	public int getLowestAvailable() {
		return availableIndices.nextSetBit(0);
	}
	
	public int nextAvailable(int i) {
		return next(i, result -> isAvailable(result));
	}
	
	public int getLowestInUse() {
		return availableIndices.nextClearBit(0);
	}
	
	public int nextInUse(int i) {
		return next(i, result -> containsKey(result));
	}
	
	private int next(int i, IntFunction<Boolean> halt) {
		int result = i + 1;
		while (result < capacity() && !halt.apply(result)) {
			result += 1;
		}
		return result;		
	}

	@Override
	public FixedSizeArray<T> deepCopy() {
		FixedSizeArray<T> copy = new FixedSizeArray<>(size(), copier);
		for (int i = getLowestInUse(); i < array.length; i = nextInUse(i)) {
			copy.put(i, copier.apply(get(i)));
		}
		return copy;
	}
	
	public ArrayList<T> values() {
		ArrayList<T> values = new ArrayList<>();
		for (int i = getLowestInUse(); i < array.length; i = nextInUse(i)) {
			values.add(get(i));
		}
		return values;
	}
}
