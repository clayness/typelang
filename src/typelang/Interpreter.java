package typelang;

import java.io.IOException;

import typelang.AST.*;

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
                Press Ctrl + C to exit.""");

        Reader reader = new Reader();
        Evaluator eval = new Evaluator(reader);
        Printer printer = new Printer();

        // adding the type checker
        Checker checker = new Checker();

        //noinspection InfiniteLoopStatement
        while (true) { // Read-Eval-Print-Loop (also known as REPL)
            Program p;
            try {
                p = reader.read();
                if (p._e == null) continue;

                // type check the program here
                Type t = checker.check(p);

                if (t instanceof Type.ErrorT) {
                    // print the type checking error
                    printer.print(t);
                } else {
                    // only evaluate if type checking passes
                    Value val = eval.valueOf(p);
                    printer.print(val);
                }
            } catch (Env.LookupException e) {
                printer.print(e);
            } catch (IOException e) {
                System.out.println("Error reading input:" + e.getMessage());
            } catch (NullPointerException e) {
                System.out.println("Error:" + e.getMessage());
            }
        }

    }
}
