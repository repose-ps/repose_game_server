package com.repose.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for collections.
 * 
 * @author Robert Guidry
 */
public final class CollectionUtil {

	/**
	 * Creates a new List from the specified collection that is sorted for classes
	 * that extend {@link Comparable}.
	 * 
	 * @param <T>        the comparable type
	 * @param collection the collection of types
	 * @return the new list of sorted types
	 */
	public static <T extends Comparable<T>> List<T> getSortedList(Collection<T> collection) {
		final List<T> alphabetizedList = new ArrayList<>();
		alphabetizedList.addAll(collection);
		Collections.sort(alphabetizedList);
		return alphabetizedList;
	}

	/**
	 * The {@code CollectionUtil} class is not instantiated.
	 */
	private CollectionUtil() {
	}

}
