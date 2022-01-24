public class Main {
    public static void main(String[] args) {
        String[] stenslScript = {"println(\"hello world\")",
                "println(2+2+2+2)",
                "var string myFirstVar = \"hello1\"",
                "println(2+2)",
                "myFirstVar = 5",
                "println(myFirstVar)"};
        Interpreter.runStensl(stenslScript);
    }
}
