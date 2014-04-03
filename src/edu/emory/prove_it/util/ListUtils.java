package edu.emory.prove_it.util;
import java.util.ArrayList;

public class ListUtils {
	
	public static <T> ArrayList<T> union(ArrayList<T> a, ArrayList<T> b) {
		ArrayList<T> result = new ArrayList<T>();
		
		for (T t : a)
			if (! result.contains(t))
				result.add(t);
		for (T t : b)
			if (! result.contains(t))
				result.add(t);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> listOf(T... args) {
		ArrayList<T> result = new ArrayList<T>();
		
		for (T t : args)
			result.add(t);
		
		return result;
	}
	
	public static <T> ArrayList<T> removeDuplicates(ArrayList<T> a) {
		ArrayList<T> result = new ArrayList<T>();
		
		for (T t : a)
			if (! result.contains(t))
				result.add(t);
		
		return result;
	}
	
}