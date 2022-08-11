package dd;


public class dd {
	public dd(int a) {
		this.a = a;
	}

	int func() {
		int b = 1;
		for (int i = 1; i < a; i++) {
			System.out.println(a + "a");
			b = a * i + b;
			System.out.println(b + "BBBBB");
		}
		return a + b;
	}

	int a;

	public static void main(String[] args) {
		dd obj = new dd(3);
		obj.a = 5;
		int b = obj.func();
		System.out.print(obj.a + b);
	}

}
