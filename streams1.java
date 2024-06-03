package CollectionPractice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class streams1 {
    public static void main(String[] args) {
    	List<String> list = Arrays.asList("apple", "banana", "cherry", "date");

        List<String> upperCaseList = list.stream()
                                         .map(String::toUpperCase)
                                         .collect(Collectors.toList());

        System.out.println(upperCaseList);
    }
}
