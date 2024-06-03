package CollectionPractice;

import java.util.ArrayList;
import java.util.List;

public class generics_typeSafety {
    public static void main(String[] args) {
        List list = new ArrayList(); 
        list.add("Hello");
        list.add(123); 

        for (Object obj : list) {

            String str = (String) obj;  // Causes ClassCastException at runtime
            System.out.println(str);
        }
    }
}
