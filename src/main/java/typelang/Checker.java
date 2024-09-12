package typelang;

import typelang.AST.*;
import typelang.Type.*;

import java.util.ArrayList;
import java.util.List;

public class Checker implements Visitor<Type, Type> {
    private final Printer.Formatter<Type> ts = new Printer.Formatter<>();

    private Env<Type> globalEnv;

    public Checker() {
        this.globalEnv = new Env.EmptyEnv<>();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean assignable(Type t1, Type t2) {
        if (t2 instanceof UnitT) {
            return true;
        } else {
            return t1.typeEqual(t2);
        }
    }

    public static void main(String[] args) {
        System.out.println("""
                TypeLang: Type a program to check and press the enter key,
                e.g. ((lambda (x: num y: num z : num) (+ x (+ y z))) 1 2 3)\s
                or try (let ((x : num 2)) x)\s
                or try (car (list : num  1 2 8))\s
                or try (ref : num 2)\s
                or try  (let ((a : Ref num (ref : num 2))) (set! a (deref a)))\s
                Press Ctrl + C to exit.""");
        try (Reader reader = new Reader()) {
            Printer printer = new Printer();
            // type checker
            Checker checker = new Checker();
            while (true) { // Read-Eval-Print-Loop (also known as REPL)
                Program p;
                try {
                    p = reader.read();
                    if (p == null) {
                        System.out.println();
                        break;
                    } else if (p.e() == null) {
                        System.out.println();
                    } else {
                        // run the type checker
                        Type t = checker.check(p);
                        printer.print(t);
                    }
                } catch (Env.LookupException e) {
                    printer.print(e);
                } catch (Exception e) {
                    System.err.println("Error:" + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error closing the input stream: " + e.getMessage());
        }

    }

    Type check(Program p) {
        return p.accept(this, globalEnv);
    }

    @Override
    public Type visit(Program p, Env<Type> env) {
        Env<Type> new_env = env;
        for (DefineDecl d : p.decls()) {
            Type type = d.accept(this, new_env);
            if (type instanceof ErrorT) {
                return type;
            }
            Type dType = d.type();
            if (!type.typeEqual(dType)) {
                return new ErrorT("Expected " + dType.toString() + " found " + type + " in " + ts.visit(d, null));
            }
            new_env = new_env.extend(d.name(), dType);
        }
        return p.e().accept(this, new_env);
    }

    @Override
    public Type visit(UnitExp e, Env<Type> env) {
        return Type.UnitT.getInstance();
    }

    @Override
    public Type visit(NumExp e, Env<Type> env) {
        return Type.NumT.getInstance();
    }

    @Override
    public Type visit(BoolExp e, Env<Type> env) {
        return Type.BoolT.getInstance();
    }

    private Type visitCompoundArithExp(CompoundArithExp e, Env<Type> env, String printNode) {
        List<Exp> operands = e.all();

        for (Exp exp : operands) {
            Type intermediate = exp.accept(this, env); // Static
            // type-checking
            if (intermediate instanceof ErrorT) {
                return intermediate;
            }

            if (!(intermediate instanceof Type.NumT)) {
                return new ErrorT("expected num found " + intermediate.toString() + " in " + printNode);
            }
        }

        return NumT.getInstance();
    }

    @Override
    public Type visit(AddExp e, Env<Type> env) {
        return visitCompoundArithExp(e, env, ts.visit(e, null));
    }

    @Override
    public Type visit(DivExp e, Env<Type> env) {
        return visitCompoundArithExp(e, env, ts.visit(e, null));
    }

    @Override
    public Type visit(MultExp e, Env<Type> env) {
        return visitCompoundArithExp(e, env, ts.visit(e, null));
    }

    @Override
    public Type visit(SubExp e, Env<Type> env) {
        return visitCompoundArithExp(e, env, ts.visit(e, null));
    }

    @Override
    public Type visit(VarExp e, Env<Type> env) {
        try {
            return env.get(e.name());
        } catch (Exception ex) {
            return new ErrorT("Variable " + e.name() + " has not been declared in " + ts.visit(e, null));
        }
    }

    @Override
    public Type visit(LetExp e, Env<Type> env) {
        List<String> names = e.names();
        List<Exp> value_exps = e.value_exps();
        List<Type> types = e.varTypes();
        List<Type> values = new ArrayList<>(value_exps.size());

        int i = 0;
        for (Exp exp : value_exps) {
            Type type = exp.accept(this, env);
            if (type instanceof ErrorT) {
                return type;
            }

            Type argType = types.get(i);
            if (!type.typeEqual(argType)) {
                return new ErrorT("The declared type of the " + i + " let variable and the actual type mismatch, expect " + argType.toString() + " found " + type + " in " + ts.visit(e, null));
            }

            values.add(type);
            i++;
        }

        Env<Type> new_env = env;
        for (int index = 0; index < names.size(); index++) {
            new_env = new_env.extend(names.get(index), values.get(index));
        }

        return e.body().accept(this, new_env);
    }

    @Override
    public Type visit(DefineDecl d, Env<Type> env) {
        globalEnv = globalEnv.extend(d.name(), d.type());
        return d.value_exp().accept(this, globalEnv);
    }

    @Override
    public Type visit(LambdaExp e, Env<Type> env) {
        List<String> names = e.formals();
        List<Type> types = e.formal_types();

        //FuncT ft = (FuncT) type;
        String message = "The number of formal parameters and the number of arguments in the function type do not match in ";
        if (types.size() == names.size()) {
            Env<Type> new_env = env;
            int index = 0;
            for (Type argType : types) {
                new_env = new_env.extend(names.get(index), argType);
                index++;
            }

            Type bodyType = e.body().accept(this, new_env);

            if (bodyType instanceof ErrorT) {
                return bodyType;
            }

            //create a new function type with arguments, and the type of
            //the body as the return type. Notice, that the body type isn't
            //given in any type annotation, but being computed here.
            return new FuncT(types, bodyType);
        }

        return new ErrorT(message + ts.visit(e, null));
    }

    @Override
    public Type visit(CallExp e, Env<Type> env) {
        Exp operator = e.operator();
        List<Exp> operands = e.operands();

        Type type = operator.accept(this, env);
        if (type instanceof ErrorT) {
            return type;
        }

        String message = "Expect a function type in the call expression, found " + type.toString() + " in ";
        if (type instanceof FuncT ft) {

            List<Type> argTypes = ft.argTypes();
            int size_actuals = operands.size();
            int size_formals = argTypes.size();

            message = "The number of arguments expected is " + size_formals + " found " + size_actuals + " in ";
            if (size_actuals == size_formals) {
                for (int i = 0; i < size_actuals; i++) {
                    Exp operand = operands.get(i);
                    Type operand_type = operand.accept(this, env);

                    if (operand_type instanceof ErrorT) {
                        return operand_type;
                    }

                    if (!assignable(argTypes.get(i), operand_type)) {
                        return new ErrorT("The expected type of the " + i + " argument is " + argTypes.get(i).toString() + " found " + operand_type.toString() + " in " + ts.visit(e, null));
                    }
                }
                return ft.returnType();
            }
        }
        return new ErrorT(message + ts.visit(e, null));
    }

    @Override
    public Type visit(IfExp e, Env<Type> env) {
        Exp cond = e.conditional();
        Type condType = cond.accept(this, env);
        if (condType instanceof ErrorT) {
            return condType;
        }

        if (!(condType instanceof BoolT)) {
            return new ErrorT("The condition should have boolean type, found " + condType.toString() + " in " + ts.visit(e, null));
        }

        Type thentype = e.then_exp().accept(this, env);
        if (thentype instanceof ErrorT) {
            return thentype;
        }

        Type elsetype = e.else_exp().accept(this, env);
        if (elsetype instanceof ErrorT) {
            return elsetype;
        }

        if (thentype.typeEqual(elsetype)) {
            return thentype;
        }

        return new ErrorT("The then and else expressions should have the same " + "type, then has type " + thentype + " else has type " + elsetype.toString() + " in " + ts.visit(e, null));
    }

    private Type visitBinaryComparator(BinaryComparator e, Env<Type> env, String printNode) {
        Exp first_exp = e.first_exp();
        Exp second_exp = e.second_exp();

        Type first_type = first_exp.accept(this, env);
        if (first_type instanceof ErrorT) {
            return first_type;
        }

        Type second_type = second_exp.accept(this, env);
        if (second_type instanceof ErrorT) {
            return second_type;
        }

        if (!(first_type instanceof NumT)) {
            return new ErrorT("The first argument of a binary expression " + "should be num Type, found " + first_type.toString() + " in " + printNode);
        }

        if (!(second_type instanceof NumT)) {
            return new ErrorT("The second argument of a binary expression " + "should be num Type, found " + second_type.toString() + " in " + printNode);
        }

        return BoolT.getInstance();
    }

    @Override
    public Type visit(LessExp e, Env<Type> env) {
        return visitBinaryComparator(e, env, ts.visit(e, null));
    }

    @Override
    public Type visit(EqualExp e, Env<Type> env) {
        return visitBinaryComparator(e, env, ts.visit(e, null));
    }

    @Override
    public Type visit(GreaterExp e, Env<Type> env) {
        return visitBinaryComparator(e, env, ts.visit(e, null));
    }

    @Override
    public Type visit(CarExp e, Env<Type> env) {
        Exp exp = e.arg();
        Type type = exp.accept(this, env);
        if (type instanceof ErrorT) {
            return type;
        }

        if (type instanceof PairT pt) {
            return pt.fst();
        }

        return new ErrorT("The car expect an expression of type Pair, found " + type.toString() + " in " + ts.visit(e, null));
    }

    @Override
    public Type visit(CdrExp e, Env<Type> env) {
        Exp exp = e.arg();
        Type type = exp.accept(this, env);
        if (type instanceof ErrorT) {
            return type;
        }

        if (type instanceof PairT pt) {
            return pt.snd();
        }

        return new ErrorT("The cdr expect an expression of type Pair, found " + type.toString() + " in " + ts.visit(e, null));
    }

    @Override
    public Type visit(ConsExp e, Env<Type> env) {
        Exp fst = e.fst();
        Exp snd = e.snd();

        Type t1 = fst.accept(this, env);
        if (t1 instanceof ErrorT) {
            return t1;
        }

        Type t2 = snd.accept(this, env);
        if (t2 instanceof ErrorT) {
            return t2;
        }

        return new PairT(t1, t2);
    }

    @Override
    public Type visit(ListExp e, Env<Type> env) {
        List<Exp> elems = e.elems();
        Type type = e.type();

        int index = 0;
        for (Exp elem : elems) {
            Type elemType = elem.accept(this, env);
            if (elemType instanceof ErrorT) {
                return elemType;
            }

            if (!assignable(type, elemType)) {
                return new ErrorT("The " + index + " expression should have type " + type.toString() + " found " + elemType.toString() + " in " + ts.visit(e, null));
            }
            index++;
        }
        return new ListT(type);
    }

    @Override
    public Type visit(NullExp e, Env<Type> env) {
        Exp arg = e.arg();
        Type type = arg.accept(this, env);
        if (type instanceof ErrorT) {
            return type;
        }

        if (type instanceof ListT) {
            return BoolT.getInstance();
        }

        return new ErrorT("The null? expects an expression of type List, found " + type.toString() + " in " + ts.visit(e, null));
    }

    @Override
    public Type visit(RefExp e, Env<Type> env) {
        Exp value = e.value_exp();
        Type type = e.type();
        Type expType = value.accept(this, env);
        if (type instanceof ErrorT) {
            return type;
        }

        if (expType.typeEqual(type)) {
            return new RefT(type);
        }

        return new ErrorT("The Ref expression expects type " + type.toString() + " found " + expType + " in " + ts.visit(e, null));
    }

    @Override
    public Type visit(DerefExp e, Env<Type> env) {
        Exp exp = e.loc_exp();
        Type type = exp.accept(this, env);
        if (type instanceof ErrorT) {
            return type;
        }

        if (type instanceof RefT rt) {
            return rt.nestType();
        }

        return new ErrorT("The dereference expression expects a reference type " + "found " + type.toString() + " in " + ts.visit(e, null));
    }

    @Override
    public Type visit(AssignExp e, Env<Type> env) {
        Exp lhs_exp = e.lhs_exp();
        Type lhsType = lhs_exp.accept(this, env);
        if (lhsType instanceof ErrorT) {
            return lhsType;
        }

        if (lhsType instanceof RefT rt) {
            Exp rhs_exp = e.rhs_exp();
            Type rhsType = rhs_exp.accept(this, env);
            if (rhsType instanceof ErrorT) {
                return rhsType;
            }

            Type nested = rt.nestType();

            if (rhsType.typeEqual(nested)) {
                return rhsType;
            }

            return new ErrorT("The inner type of the reference type is " + nested.toString() + " the rhs type is " + rhsType + " in " + ts.visit(e, null));
        }

        return new ErrorT("The lhs of the assignment expression expects a " + "reference type found " + lhsType.toString() + " in " + ts.visit(e, null));
    }

    @Override
    public Type visit(FreeExp e, Env<Type> env) {
        Exp exp = e.value_exp();
        Type type = exp.accept(this, env);

        if (type instanceof ErrorT) {
            return type;
        }

        if (type instanceof RefT) {
            return UnitT.getInstance();
        }

        return new ErrorT("The free expression expects a reference type " + "found " + type.toString() + " in " + ts.visit(e, null));
    }
}
