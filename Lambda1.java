package CollectionPractice;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Lambda1 {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("apple", "banana", "cherry", "date");

        // Using Predicate to filter strings that start with 'a'
        Predicate<String> startsWithA = s -> s.startsWith("a");

        List<String> filteredList = list.stream()
                                        .filter(startsWithA)
                                        .collect(Collectors.toList());

        System.out.println(filteredList); // Output: [apple]
    }
}
