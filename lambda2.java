package CollectionPractice;

public class lambda2 {
	@FunctionalInterface
	interface MyFunctionalInterface {
	    void myMethod();
	}

	public static void main(String[] args) {
		
		MyFunctionalInterface myFunc = () -> System.out.println("Lambda Expression Example");
		myFunc.myMethod();
    }
	

}
