package typelang;

import typelang.Env.ExtendEnv;

import java.util.ArrayList;
import java.util.List;

import static typelang.AST.*;
import static typelang.Value.NumVal;
import static typelang.Value.UnitVal;

public class Evaluator implements Visitor<Value, Value> {

    private final Printer.Formatter<Value> ts = new Printer.Formatter<>();

    private Env<Value> globalEnv = new Env.EmptyEnv<>();
    private final Heap heap = new Heap.Heap16Bit();

    Value valueOf(Program p) {
        return p.accept(this, globalEnv);
    }

    @Override
    public Value visit(AddExp e, Env<Value> env) {
        List<Exp> operands = e.all();
        double result = 0;
        for (Exp exp : operands) {
            NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
            result += intermediate.v(); //Semantics of AddExp in terms of the target language.
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(UnitExp e, Env<Value> env) {
        return new UnitVal();
    }

    @Override
    public Value visit(NumExp e, Env<Value> env) {
        return new NumVal(e.v());
    }

    @Override
    public Value visit(BoolExp e, Env<Value> env) {
        return new Value.BoolVal(e.b());
    }

    @Override
    public Value visit(DivExp e, Env<Value> env) {
        List<Exp> operands = e.all();
        NumVal lVal = (NumVal) operands.getFirst().accept(this, env);
        double result = lVal.v();
        for (int i = 1; i < operands.size(); i++) {
            NumVal rVal = (NumVal) operands.get(i).accept(this, env);
            result = result / rVal.v();
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(MultExp e, Env<Value> env) {
        List<Exp> operands = e.all();
        double result = 1;
        for (Exp exp : operands) {
            NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
            result *= intermediate.v(); //Semantics of MultExp.
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(Program p, Env<Value> env) {
        try {
            for (DefineDecl d : p.decls()) {
                d.accept(this, globalEnv);
            }
            return p.e().accept(this, globalEnv);
        } catch (ClassCastException e) {
            return new Value.DynamicError(e.getMessage());
        }
    }

    @Override
    public Value visit(SubExp e, Env<Value> env) {
        List<Exp> operands = e.all();
        NumVal lVal = (NumVal) operands.getFirst().accept(this, env);
        double result = lVal.v();
        for (int i = 1; i < operands.size(); i++) {
            NumVal rVal = (NumVal) operands.get(i).accept(this, env);
            result = result - rVal.v();
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(VarExp e, Env<Value> env) { // New for varlang
        return env.get(e.name());
    }

    @Override
    public Value visit(LetExp e, Env<Value> env) { // New for varlang.
        List<String> names = e.names();
        List<Exp> value_exps = e.value_exps();
        List<Value> values = new ArrayList<>(value_exps.size());

        for (Exp exp : value_exps) {
            values.add(exp.accept(this, env));
        }

        Env<Value> new_env = env;
        for (int i = 0; i < names.size(); i++) {
            new_env = new_env.extend(names.get(i), values.get(i));
        }

        return e.body().accept(this, new_env);
    }

    @Override
    public Value visit(DefineDecl e, Env<Value> env) { // New for typelang.
        String name = e.name();
        Exp value_exp = e.value_exp();
        Value value = value_exp.accept(this, env);
        globalEnv = globalEnv.extend(name, value);
        return new Value.UnitVal();
    }

    @Override
    public Value visit(LambdaExp e, Env<Value> env) {
        // Create a function value with three components:
        //  1. formal parameters of the function - e.formals()
        //  2. actual body of the function - e.body()
        //  3. mapping from the free variables in the function body to their values.
        return new Value.FunVal(env, e.formals(), e.body());
    }

    @Override
    public Value visit(CallExp e, Env<Value> env) {
        Value result = e.operator().accept(this, env);
        if (!(result instanceof Value.FunVal operator)) {
            return new Value.DynamicError("Operator not a function in call " + ts.visit(e, null));
        }
        // Dynamic checking
        List<Exp> operands = e.operands();

        // Call-by-value semantics
        List<Value> actuals = new ArrayList<>(operands.size());
        for (Exp exp : operands) {
            actuals.add(exp.accept(this, env));
        }

        List<String> formals = operator.formals();
        if (formals.size() != actuals.size()) {
            return new Value.DynamicError("Argument mismatch in call " + ts.visit(e, null));
        }

        Env<Value> closure_env = operator.env();
        Env<Value> fun_env = ExtendEnv.append(closure_env, globalEnv);
        for (int index = 0; index < formals.size(); index++) {
            fun_env = fun_env.extend(formals.get(index), actuals.get(index));
        }

        return operator.body().accept(this, fun_env);
    }

    @Override
    public Value visit(IfExp e, Env<Value> env) {
        Object result = e.conditional().accept(this, env);
        if (!(result instanceof Value.BoolVal condition)) {
            return new Value.DynamicError("Condition not a boolean in expression " + ts.visit(e, env));
        }
        //Dynamic checking
        if (condition.v()) {
            return e.then_exp().accept(this, env);
        } else {
            return e.else_exp().accept(this, env);
        }
    }

    @Override
    public Value visit(LessExp e, Env<Value> env) {
        Value.NumVal first = (Value.NumVal) e.first_exp().accept(this, env);
        Value.NumVal second = (Value.NumVal) e.second_exp().accept(this, env);
        return new Value.BoolVal(first.v() < second.v());
    }

    @Override
    public Value visit(EqualExp e, Env<Value> env) {
        Value.NumVal first = (Value.NumVal) e.first_exp().accept(this, env);
        Value.NumVal second = (Value.NumVal) e.second_exp().accept(this, env);
        return new Value.BoolVal(first.v() == second.v());
    }

    @Override
    public Value visit(GreaterExp e, Env<Value> env) {
        Value.NumVal first = (Value.NumVal) e.first_exp().accept(this, env);
        Value.NumVal second = (Value.NumVal) e.second_exp().accept(this, env);
        return new Value.BoolVal(first.v() > second.v());
    }

    @Override
    public Value visit(CarExp e, Env<Value> env) {
        Value.PairVal pair = (Value.PairVal) e.arg().accept(this, env);
        return pair.fst();
    }

    @Override
    public Value visit(CdrExp e, Env<Value> env) {
        Value.PairVal pair = (Value.PairVal) e.arg().accept(this, env);
        return pair.snd();
    }

    @Override
    public Value visit(ConsExp e, Env<Value> env) {
        Value first = e.fst().accept(this, env);
        Value second = e.snd().accept(this, env);
        return new Value.PairVal(first, second);
    }

    @Override
    public Value visit(ListExp e, Env<Value> env) {
        List<Exp> elemExps = e.elems();
        int length = elemExps.size();
        if (length == 0) {
            return new Value.Null();
        }

        //Order of evaluation: left to right e.g. (list (+ 3 4) (+ 5 4))
        Value[] elems = new Value[length];
        for (int i = 0; i < length; i++) {
            elems[i] = elemExps.get(i).accept(this, env);
        }

        Value result = new Value.Null();
        for (int i = length - 1; i >= 0; i--) {
            result = new Value.PairVal(elems[i], result);
        }
        return result;
    }

    @Override
    public Value visit(NullExp e, Env<Value> env) {
        Value val = e.arg().accept(this, env);
        return new Value.BoolVal(val instanceof Value.Null);
    }

    @Override
    public Value visit(RefExp e, Env<Value> env) {
        Exp value_exp = e.value_exp();
        Value value = value_exp.accept(this, env);
        return heap.ref(value);
    }

    @Override
    public Value visit(AssignExp e, Env<Value> env) {
        Exp rhs = e.rhs_exp();
        Exp lhs = e.lhs_exp();
        //Note the order of evaluation below.
        Value rhs_val = rhs.accept(this, env);
        Value.RefVal loc = (Value.RefVal) lhs.accept(this, env);
        return heap.setref(loc, rhs_val);
    }

    @Override
    public Value visit(DerefExp e, Env<Value> env) {
        Exp loc_exp = e.loc_exp();
        Value.RefVal loc = (Value.RefVal) loc_exp.accept(this, env);
        return heap.deref(loc);
    }

    @Override
    public Value visit(FreeExp e, Env<Value> env) {
        Exp value_exp = e.value_exp();
        Value.RefVal loc = (Value.RefVal) value_exp.accept(this, env);
        heap.free(loc);
        return new Value.UnitVal();
    }

}
