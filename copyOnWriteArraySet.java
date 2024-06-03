package CollectionPractice;

import java.util.concurrent.CopyOnWriteArraySet;

public class copyOnWriteArraySet {
    public static void main(String[] args) {
        CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();

        set.add("A");
        set.add("B");
        set.add("C");

        Thread writer = new Thread(() -> {
            set.add("D");
            set.remove("A");
            set.add("C");
        });

        Thread reader = new Thread(() -> {
            for (String item : set) {
                System.out.println(item);
            }
        });

        writer.start();
        reader.start();

        try {
            writer.join();
            reader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final set: " + set);
    }
}
