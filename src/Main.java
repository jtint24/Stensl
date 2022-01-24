public class Main {
    public static void main(String[] args) {
        String[] stenslScript = {
                "println(\"hello there! Let's run some Stensl.\")",
                "var string number = \"h\"",
                "println(\"number is: \"&str(number))",
                "number = number+1",
                "println(\"now number is: \"&str(number))"};
        Interpreter.runStensl(stenslScript);
    }
}
