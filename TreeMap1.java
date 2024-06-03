package CollectionPractice;

import java.util.TreeMap;

public class TreeMap1 {
    public static void main(String[] args) {
        TreeMap<Integer, String> treeMap = new TreeMap<>();

        treeMap.put(5, "Five");
        treeMap.put(1, "One");
        treeMap.put(10, "Ten");
        treeMap.put(3, "Three");
        treeMap.put(7, "Seven");

        System.out.println("TreeMap: " + treeMap);

        System.out.println("Value for key 3: " + treeMap.get(3));

        treeMap.remove(10);
        
        System.out.println("TreeMap after removal: " + treeMap);
    }
}
