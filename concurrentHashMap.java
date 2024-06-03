package CollectionPractice;

import java.util.concurrent.ConcurrentHashMap;

public class concurrentHashMap {
    public static void main(String[] args) {
        ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                map.put(i, "Value" + i);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                map.put(i, "Value" + i);
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        map.forEach((key, value) -> System.out.println(key + " : " + value));
    }
}
