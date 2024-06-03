package CollectionPractice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class lambda4 {
	public static void main(String[] args) {
		
	List<String> list = Arrays.asList("b", "a", "d", "c");
	Collections.sort(list, (s1, s2) -> s1.compareTo(s2));
	System.out.println(list);
    }
}
