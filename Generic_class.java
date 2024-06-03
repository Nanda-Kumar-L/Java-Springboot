package CollectionPractice;

public class Generic_class<T> {
    private T content;

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public static void main(String[] args) {
    	Generic_class<String> stringBox = new Generic_class<>();
        stringBox.setContent("Hello");
        System.out.println("String content: " + stringBox.getContent());

        Generic_class<Integer> integerBox = new Generic_class<>();
        integerBox.setContent(123);
        System.out.println("Integer content: " + integerBox.getContent());
    }
}
