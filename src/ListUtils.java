import java.util.ArrayList;

public class ListUtils {
	
	static <T> ArrayList<T> union(ArrayList<T> a, ArrayList<T> b) {
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
	static <T> ArrayList<T> listOf(T... args) {
		ArrayList<T> result = new ArrayList<T>();
		
		for (T t : args)
			result.add(t);
		
		return result;
	}
	
	static <T> ArrayList<T> removeDuplicates(ArrayList<T> a) {
		ArrayList<T> result = new ArrayList<T>();
		
		for (T t : a)
			if (! result.contains(t))
				result.add(t);
		
		return result;
	}
	
}
