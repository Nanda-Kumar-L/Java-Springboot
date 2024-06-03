package CollectionPractice;

import java.util.function.Function;

public class function_FI {
    public static void main(String[] args) {
        Function<String, Integer> stringLengthFunction = s -> s.length();

        Integer length = stringLengthFunction.apply("apple");

        System.out.println(length);
    }
}
