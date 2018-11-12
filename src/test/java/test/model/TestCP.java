package test.model;

public class TestCP {
	public static void main(String[] args) {
		Child child = new Child();
		child.setUsername("username:child");
		child.setPassword("password:child");
		
		Parent parent = new Parent();
		parent.setUsername("username:parent");
		
		InvokeMethod invoke = new InvokeMethod();
		invoke.invoke(child);
		
		if(child instanceof Parent) {
			System.out.println(1);
		}
		if(child instanceof Child) {
			System.out.println(child.getPassword() + ", "+child.getUsername());
		}
	}
}
