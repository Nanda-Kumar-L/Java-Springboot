package CollectionPractice;

import java.util.concurrent.CopyOnWriteArrayList;

public class copyOnWriteArrayList {
    public static void main(String[] args) {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

        list.add("A");
        list.add("B");
        list.add("C");

        Thread writer = new Thread(() -> {
            list.add("D");
            list.remove("A");
            list.add("E");
        });

        Thread reader = new Thread(() -> {
            for (String item : list) {
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

        System.out.println("Final list: " + list);
    }
}
