package CollectionPractice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class streams2 {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("apple", "banana", "cherry", "date");

        List<String> filteredList = list.stream()
                                        .filter(s -> s.startsWith("a"))
                                        .collect(Collectors.toList());

        System.out.println(filteredList);
    }
}
