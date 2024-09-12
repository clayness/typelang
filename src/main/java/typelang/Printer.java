package typelang;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Printer {
    public void print(Value v) {
        if (v instanceof Value.UnitVal) {
            System.out.println();
        } else if (!v.toString().isEmpty()) {
            System.out.println(v);
        }
    }

    public void print(Type t) {
        if (!t.toString().isEmpty()) {
            System.out.println(t);
        }
    }

    public void print(Exception e) {
        System.out.println(e.getMessage());
    }

    public static class Formatter<T> implements AST.Visitor<String, T> {

        public String visit(AST.AddExp e, Env<T> env) {
            return "(+ %s)".formatted(
                    e.all().stream()
                            .map(exp -> exp.accept(this, env))
                            .map(Object::toString)
                            .collect(Collectors.joining(" ")));
        }

        public String visit(AST.UnitExp e, Env<T> env) {
            return "unit";
        }

        public String visit(AST.NumExp e, Env<T> env) {
            return "" + e.v();
        }

        @Override
        public String visit(AST.BoolExp e, Env<T> env) {
            return e.b() ? "#t" : "#f";
        }

        public String visit(AST.DivExp e, Env<T> env) {
            return "(/ %s)".formatted(
                    e.all().stream()
                            .map(exp -> exp.accept(this, env))
                            .map(Object::toString)
                            .collect(Collectors.joining(" ")));
        }

        public String visit(AST.MultExp e, Env<T> env) {
            return "(* %s)".formatted(
                    e.all().stream()
                            .map(exp -> exp.accept(this, env))
                            .map(Object::toString)
                            .collect(Collectors.joining(" ")));
        }

        public String visit(AST.Program p, Env<T> env) {
            return p.e().accept(this, env);
        }

        public String visit(AST.SubExp e, Env<T> env) {
            return "(- %s)".formatted(
                    e.all().stream()
                            .map(exp -> exp.accept(this, env))
                            .map(Object::toString)
                            .collect(Collectors.joining(" ")));
        }

        public String visit(AST.VarExp e, Env<T> env) {
            return e.name();
        }

        public String visit(AST.LetExp e, Env<T> env) {
            return "(let (%s) %s)".formatted(
                    IntStream.range(0, e.names().size())
                            .mapToObj(i -> "(%s : %s %s)".formatted(
                                    e.names().get(i),
                                    e.varTypes().get(i),
                                    e.value_exps().get(i).accept(this, env)))
                            .map(Object::toString)
                            .collect(Collectors.joining(" ")),
                    e.body().accept(this, env)
            );
        }

        public String visit(AST.DefineDecl d, Env<T> env) {
            return "(define %s : %s %s)".formatted(
                    d.name(),
                    d.type(),
                    d.value_exp().accept(this, env));
        }

        @Override
        public String visit(AST.LambdaExp e, Env<T> env) {
            return "(lambda (%s) %s)".formatted(
                    IntStream.range(0, e.formals().size())
                            .mapToObj(i -> "%s : %s".formatted(
                                    e.formals().get(i),
                                    e.formal_types().get(i)))
                            .map(Object::toString)
                            .collect(Collectors.joining(" ")),
                    e.body().accept(this, env));
        }

        @Override
        public String visit(AST.CallExp e, Env<T> env) {
            return "(%s %s)".formatted(
                    e.operator().accept(this, env),
                    e.operands().stream()
                            .map(opnd -> opnd.accept(this, env))
                            .map(Object::toString)
                            .collect(Collectors.joining(" "))
            );
        }

        @Override
        public String visit(AST.IfExp e, Env<T> env) {
            return "(if %s %s %s)".formatted(
                    e.conditional().accept(this, env),
                    e.then_exp().accept(this, env),
                    e.else_exp().accept(this, env));
        }

        @Override
        public String visit(AST.LessExp e, Env<T> env) {
            return "(< %s %s)".formatted(
                    e.first_exp().accept(this, env),
                    e.second_exp().accept(this, env));
        }

        @Override
        public String visit(AST.EqualExp e, Env<T> env) {
            return "(= %s %s)".formatted(
                    e.first_exp().accept(this, env),
                    e.second_exp().accept(this, env));
        }

        @Override
        public String visit(AST.GreaterExp e, Env<T> env) {
            return "(> %s %s)".formatted(
                    e.first_exp().accept(this, env),
                    e.second_exp().accept(this, env));
        }

        @Override
        public String visit(AST.CarExp e, Env<T> env) {
            return "(car %s)".formatted(e.arg().accept(this, env));
        }

        @Override
        public String visit(AST.CdrExp e, Env<T> env) {
            return "(cdr %s)".formatted(e.arg().accept(this, env));
        }

        @Override
        public String visit(AST.ConsExp e, Env<T> env) {
            return "(cons %s %s)".formatted(
                    e.fst().accept(this, env),
                    e.snd().accept(this, env));
        }

        @Override
        public String visit(AST.ListExp e, Env<T> env) {
            return "(list : %s %s)".formatted(
                    e.type(),
                    e.elems().stream()
                            .map(exp -> exp.accept(this, env))
                            .map(Object::toString)
                            .collect(Collectors.joining(" ")));
        }

        @Override
        public String visit(AST.NullExp e, Env<T> env) {
            return "(null? %s)".formatted(e.arg().accept(this, env));
        }

        @Override
        public String visit(AST.RefExp e, Env<T> env) {
            return "(ref : %s %s)".formatted(
                    e.type(),
                    e.value_exp().accept(this, env));
        }

        @Override
        public String visit(AST.AssignExp e, Env<T> env) {
            return "(set! %s %s)".formatted(
                    e.lhs_exp().accept(this, env),
                    e.rhs_exp().accept(this, env));
        }

        @Override
        public String visit(AST.DerefExp e, Env<T> env) {
            return "(deref %s)".formatted(e.loc_exp().accept(this, env));
        }

        @Override
        public String visit(AST.FreeExp e, Env<T> env) {
            return "(free %s)".formatted(e.value_exp().accept(this, env));
        }
    }
}
