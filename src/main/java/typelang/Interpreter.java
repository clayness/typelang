package typelang;

import typelang.AST.Program;

/**
 * This main class implements the Read-Eval-Print-Loop of the interpreter with
 * the help of Reader, Evaluator, and Printer classes.
 *
 * @author hridesh
 */
public class Interpreter {
    public static void main(String[] args) {
        System.out.println("""
                TypeLang: Type a program to evaluate and press the enter key,
                e.g. ((lambda (x: num y: num z : num) (+ x (+ y z))) 1 2 3)\s
                or try (let ((x : num 2)) x)\s
                or try (car (list : num  1 2 8))\s
                or try (ref : num 2)\s
                or try  (let ((a : Ref num (ref : num 2))) (set! a (deref a)))\s
                Press Ctrl + C to exit.
                """);
        try (Reader reader = new Reader()) {
            Evaluator eval = new Evaluator();
            Printer printer = new Printer();
            // adding the type checker
            Checker checker = new Checker();
            // Read-Eval-Print-Loop (also known as REPL)
            while (true) {
                Program p;
                try {
                    p = reader.read();
                    if (p == null) {
                        System.out.println();
                        break;
                    } else if (p._e == null) {
                        System.out.println();
                    } else {
                        // type check the program here
                        Type t = checker.check(p);
                        if (t instanceof Type.ErrorT) {
                            printer.print(t);
                        } else {
                            Value val = eval.valueOf(p);
                            printer.print(val);
                        }
                    }
                } catch (Env.LookupException e) {
                    printer.print(e);
                } catch (Exception e) {
                    System.out.println("Error:" + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error closing input stream: " + e.getMessage());
        }
    }
}
