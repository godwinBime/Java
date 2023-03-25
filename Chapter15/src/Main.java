public class Main {
    public static void main(String[] args) {
        display("Beans", "Food", "Bread");
    }
    static void display(String... stu){
        for (String s: stu)
            System.out.println(s + " ");
    }
}