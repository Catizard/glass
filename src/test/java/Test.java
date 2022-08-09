public class Test {
    static class Foo {
        public int a = 1;
        public int get() {
            return a;
        }
    }
    
    static class Boo extends Foo {
    }

    public static void main(String[] args) {
        Boo boo = new Boo();
        int i = boo.get();
        System.out.println(i);
    }
}
