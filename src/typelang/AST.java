package typelang;

import java.util.ArrayList;
import java.util.List;

/**
 * This class hierarchy represents expressions in the abstract syntax tree
 * manipulated by this interpreter.
 *
 * @author hridesh
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface AST {
    interface Visitor<T, U> {
        // This interface should contain a signature for each concrete AST node.
        T visit(AST.AddExp e, U env);

        T visit(AST.UnitExp e, U env);

        T visit(AST.NumExp e, U env);

        T visit(AST.StrExp e, U env);

        T visit(AST.BoolExp e, U env);

        T visit(AST.DivExp e, U env);

        T visit(AST.ErrorExp e, U env);

        T visit(AST.MultExp e, U env);

        T visit(AST.Program p, U env);

        T visit(AST.SubExp e, U env);

        T visit(AST.VarExp e, U env);

        T visit(AST.LetExp e, U env);

        T visit(AST.DefineDecl d, U env);

        T visit(AST.ReadExp e, U env);

        T visit(AST.EvalExp e, U env);

        T visit(AST.LambdaExp e, U env);

        T visit(AST.CallExp e, U env);

        T visit(AST.LetrecExp e, U env);

        T visit(AST.IfExp e, U env);

        T visit(AST.LessExp e, U env);

        T visit(AST.EqualExp e, U env);

        T visit(AST.GreaterExp e, U env);

        T visit(AST.CarExp e, U env);

        T visit(AST.CdrExp e, U env);

        T visit(AST.ConsExp e, U env);

        T visit(AST.ListExp e, U env);

        T visit(AST.NullExp e, U env);

        T visit(AST.IsNullExp e, U env);

        T visit(AST.IsProcedureExp e, U env);

        T visit(AST.IsListExp e, U env);

        T visit(AST.IsPairExp e, U env);

        T visit(AST.IsUnitExp e, U env);

        T visit(AST.IsNumberExp e, U env);

        T visit(AST.IsStringExp e, U env);

        T visit(AST.IsBooleanExp e, U env);

        T visit(AST.RefExp e, U env);

        T visit(AST.DerefExp e, U env);

        T visit(AST.AssignExp e, U env);

        T visit(AST.FreeExp e, U env);
    }

    abstract class ASTNode implements AST {
        public abstract Object accept(Visitor visitor, Object env);
    }

    class Program extends ASTNode {
        List<DefineDecl> _decls;
        Exp _e;

        public Program(List<DefineDecl> decls, Exp e) {
            _decls = decls;
            _e = e;
        }

        public Exp e() {
            return _e;
        }

        public List<DefineDecl> decls() {
            return _decls;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    abstract class Exp extends ASTNode {
        /* no-op */
    }

    //region  + Atomic Expressions
    class UnitExp extends Exp {

        public UnitExp() {
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class BoolExp extends Exp {
        boolean _val;

        public BoolExp(boolean v) {
            _val = v;
        }

        public boolean v() {
            return _val;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class NumExp extends Exp {
        double _val;

        public NumExp(double v) {
            _val = v;
        }

        public double v() {
            return _val;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class StrExp extends Exp {
        String _val;

        public StrExp(String v) {
            _val = v;
        }

        public String v() {
            return _val;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }
    //endregion

    //region + ArithLang Expressions (compound)
    abstract class CompoundArithExp extends Exp {
        List<Exp> _rest;

        @SuppressWarnings("unused")
        public CompoundArithExp() {
            _rest = new ArrayList<>();
        }

        public CompoundArithExp(Exp fst) {
            _rest = new ArrayList<>();
            _rest.add(fst);
        }

        public CompoundArithExp(List<Exp> args) {
            _rest = new ArrayList<>();
            _rest.addAll(args);
        }

        public CompoundArithExp(Exp fst, List<Exp> rest) {
            _rest = new ArrayList<>();
            _rest.add(fst);
            _rest.addAll(rest);
        }

        public CompoundArithExp(Exp fst, Exp second) {
            _rest = new ArrayList<>();
            _rest.add(fst);
            _rest.add(second);
        }

        @SuppressWarnings("unused")
        public Exp fst() {
            return _rest.get(0);
        }

        @SuppressWarnings("unused")
        public Exp snd() {
            return _rest.get(1);
        }

        public List<Exp> all() {
            return _rest;
        }

        @SuppressWarnings("unused")
        public void add(Exp e) {
            _rest.add(e);
        }

    }

    class AddExp extends CompoundArithExp {
        public AddExp(Exp fst) {
            super(fst);
        }

        public AddExp(List<Exp> args) {
            super(args);
        }

        public AddExp(Exp fst, List<Exp> rest) {
            super(fst, rest);
        }

        public AddExp(Exp left, Exp right) {
            super(left, right);
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class SubExp extends CompoundArithExp {

        public SubExp(Exp fst) {
            super(fst);
        }

        public SubExp(List<Exp> args) {
            super(args);
        }

        public SubExp(Exp fst, List<Exp> rest) {
            super(fst, rest);
        }

        public SubExp(Exp left, Exp right) {
            super(left, right);
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class DivExp extends CompoundArithExp {
        public DivExp(Exp fst) {
            super(fst);
        }

        public DivExp(List<Exp> args) {
            super(args);
        }

        public DivExp(Exp fst, List<Exp> rest) {
            super(fst, rest);
        }

        public DivExp(Exp left, Exp right) {
            super(left, right);
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class MultExp extends CompoundArithExp {
        public MultExp(Exp fst) {
            super(fst);
        }

        public MultExp(List<Exp> args) {
            super(args);
        }

        public MultExp(Exp fst, List<Exp> rest) {
            super(fst, rest);
        }

        public MultExp(Exp left, Exp right) {
            super(left, right);
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }
    //endregion

    //region + Var/DefineLang Expressions (variables)
    class VarExp extends Exp {
        String _name;

        public VarExp(String name) {
            _name = name;
        }

        public String name() {
            return _name;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * A let expression has the syntax
     * <p>
     * (let ((name expression)* ) expression)
     *
     * @author hridesh
     */
    class LetExp extends Exp {
        List<String> _names;
        List<Type> _varTypes; // added for TypeLang
        List<Exp> _value_exps;
        Exp _body;

        public LetExp(List<String> names, List<Type> varTypes, List<Exp> value_exps, Exp body) {
            _names = names;
            _varTypes = varTypes;
            _value_exps = value_exps;
            _body = body;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }

        public List<String> names() {
            return _names;
        }

        public List<Type> varTypes() {
            return _varTypes;
        }

        public List<Exp> value_exps() {
            return _value_exps;
        }

        public Exp body() {
            return _body;
        }
    }

    /**
     * A letrec expression has the syntax
     * <p>
     * (letrec ((name expression)* ) expression)
     *
     * @author hridesh
     */
    class LetrecExp extends Exp {
        List<String> _names;
        List<Type> _types;
        List<Exp> _fun_exps;
        Exp _body;

        public LetrecExp(List<String> names, List<Type> types, List<Exp> fun_exps, Exp body) {
            _names = names;
            _types = types;
            _fun_exps = fun_exps;
            _body = body;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }

        public List<String> names() {
            return _names;
        }

        public List<Type> types() {
            return _types;
        }

        public List<Exp> fun_exps() {
            return _fun_exps;
        }

        public Exp body() {
            return _body;
        }
    }

    /**
     * A define declaration has the syntax
     * <p>
     * (define name expression)
     *
     * @author hridesh
     */
    class DefineDecl extends Exp {
        String _name;
        Type _type; // added for TypeLang
        Exp _value_exp;

        public DefineDecl(String name, Type type, Exp value_exp) {
            _name = name;
            _type = type;
            _value_exp = value_exp;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }

        public String name() {
            return _name;
        }

        public Type type() {
            return _type;
        }

        public Exp value_exp() {
            return _value_exp;
        }
    }
    //endregion

    //region + FuncLang Expressions (lambda/call)

    /**
     * An anonymous procedure declaration has the syntax
     *
     * @author hridesh
     */
    class LambdaExp extends Exp {
        List<String> _formals;
        List<Type> _types; // added for TypeLang
        Exp _body;

        public LambdaExp(List<String> formals, List<Type> types, Exp body) {
            _formals = formals;
            _types = types;
            _body = body;
        }

        public List<String> formals() {
            return _formals;
        }

        public List<Type> formal_types() {
            return _types;
        }

        public Exp body() {
            return _body;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * A call expression has the syntax
     *
     * @author hridesh
     */
    class CallExp extends Exp {
        Exp _operator;
        List<Exp> _operands;

        public CallExp(Exp operator, List<Exp> operands) {
            _operator = operator;
            _operands = operands;
        }

        public Exp operator() {
            return _operator;
        }

        public List<Exp> operands() {
            return _operands;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }
    //endregion

    //region + FuncLang Expressions (conditionals)

    /**
     * An if expression has the syntax
     * <p>
     * (if conditional_expression true_expression false_expression)
     *
     * @author hridesh
     */
    class IfExp extends Exp {
        Exp _conditional;
        Exp _then_exp;
        Exp _else_exp;

        public IfExp(Exp conditional, Exp then_exp, Exp else_exp) {
            _conditional = conditional;
            _then_exp = then_exp;
            _else_exp = else_exp;
        }

        public Exp conditional() {
            return _conditional;
        }

        public Exp then_exp() {
            return _then_exp;
        }

        public Exp else_exp() {
            return _else_exp;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    abstract class BinaryComparator extends Exp {
        private final Exp _first_exp;
        private final Exp _second_exp;

        BinaryComparator(Exp first_exp, Exp second_exp) {
            _first_exp = first_exp;
            _second_exp = second_exp;
        }

        public Exp first_exp() {
            return _first_exp;
        }

        public Exp second_exp() {
            return _second_exp;
        }
    }

    /**
     * A less expression has the syntax
     * <p>
     * ( < first_expression second_expression )
     *
     * @author hridesh
     */
    class LessExp extends BinaryComparator {
        public LessExp(Exp first_exp, Exp second_exp) {
            super(first_exp, second_exp);
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * An equal expression has the syntax
     * <p>
     * ( == first_expression second_expression )
     *
     * @author hridesh
     */
    class EqualExp extends BinaryComparator {
        public EqualExp(Exp first_exp, Exp second_exp) {
            super(first_exp, second_exp);
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * A greater expression has the syntax
     * <p>
     * ( > first_expression second_expression )
     *
     * @author hridesh
     */
    class GreaterExp extends BinaryComparator {
        public GreaterExp(Exp first_exp, Exp second_exp) {
            super(first_exp, second_exp);
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }
    //endregion

    //region + FuncLang Expressions (pair/list)

    /**
     * A car expression has the syntax
     * <p>
     * ( car expression )
     *
     * @author hridesh
     */
    class CarExp extends Exp {
        private final Exp _arg;

        public CarExp(Exp arg) {
            _arg = arg;
        }

        public Exp arg() {
            return _arg;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * A cdr expression has the syntax
     * <p>
     * ( car expression )
     *
     * @author hridesh
     */
    class CdrExp extends Exp {
        private final Exp _arg;

        public CdrExp(Exp arg) {
            _arg = arg;
        }

        public Exp arg() {
            return _arg;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * A cons expression has the syntax
     * <p>
     * ( cons expression expression )
     *
     * @author hridesh
     */
    class ConsExp extends Exp {
        private final Exp _fst;
        private final Exp _snd;

        public ConsExp(Exp fst, Exp snd) {
            _fst = fst;
            _snd = snd;
        }

        public Exp fst() {
            return _fst;
        }

        public Exp snd() {
            return _snd;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * A list expression has the syntax
     * <p>
     * ( list expression* )
     *
     * @author hridesh
     */
    class ListExp extends Exp {
        private final List<Exp> _elems;
        private final Type _type; // added for TypeLang

        public ListExp(Type type, List<Exp> elems) {
            _type = type;
            _elems = elems;
        }

        public Type type() {
            return _type;
        }

        public List<Exp> elems() {
            return _elems;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * A null expression has the syntax
     * <p>
     * ( null? expression )
     *
     * @author hridesh
     */
    class NullExp extends Exp {
        private final Exp _arg;

        public NullExp(Exp arg) {
            _arg = arg;
        }

        public Exp arg() {
            return _arg;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }
    //endregion

    //region + RefLang Expressions (ref, deref, etc.)

    /**
     * A ref expression has the syntax
     * <p>
     * (ref expression)
     *
     * @author hridesh
     */
    class RefExp extends Exp {
        private final Exp _value_exp;
        Type _type; // added for TypeLang

        public RefExp(Exp value_exp, Type type) {
            _value_exp = value_exp;
            _type = type;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }

        public Exp value_exp() {
            return _value_exp;
        }

        public Type type() {
            return _type;
        }
    }

    /**
     * A deref expression has the syntax
     * <p>
     * (deref expression)
     *
     * @author hridesh
     */
    class DerefExp extends Exp {
        private final Exp _loc_exp;

        public DerefExp(Exp loc_exp) {
            _loc_exp = loc_exp;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }

        public Exp loc_exp() {
            return _loc_exp;
        }

    }

    /**
     * An assign expression has the syntax
     * <p>
     * (set! expression expression)
     *
     * @author hridesh
     */
    class AssignExp extends Exp {
        private final Exp _lhs_exp;
        private final Exp _rhs_exp;

        public AssignExp(Exp lhs_exp, Exp rhs_exp) {
            _lhs_exp = lhs_exp;
            _rhs_exp = rhs_exp;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }

        public Exp lhs_exp() {
            return _lhs_exp;
        }

        public Exp rhs_exp() {
            return _rhs_exp;
        }
    }

    /**
     * A free expression has the syntax
     * <p>
     * (free expression)
     *
     * @author hridesh
     */
    class FreeExp extends Exp {
        private final Exp _value_exp;

        public FreeExp(Exp value_exp) {
            _value_exp = value_exp;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }

        public Exp value_exp() {
            return _value_exp;
        }
    }
    //endregion

    //region + Other Expressions (read, eval, etc.)

    /**
     * Eval expression: evaluate the program that is _val
     *
     * @author hridesh
     */
    class EvalExp extends Exp {
        private final Exp _code;

        public EvalExp(Exp code) {
            _code = code;
        }

        public Exp code() {
            return _code;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * Read expression: reads the file that is _file
     *
     * @author hridesh
     */
    class ReadExp extends Exp {
        private final Exp _file;

        public ReadExp(Exp file) {
            _file = file;
        }

        public Exp file() {
            return _file;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class IsBooleanExp extends Exp {
        private final Exp e;

        public IsBooleanExp(Exp e) {
            this.e = e;
        }

        Exp exp() {
            return this.e;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class IsNumberExp extends Exp {
        private final Exp e;

        public IsNumberExp(Exp e) {
            this.e = e;
        }

        Exp exp() {
            return this.e;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class IsStringExp extends Exp {
        private final Exp e;

        public IsStringExp(Exp e) {
            this.e = e;
        }

        Exp exp() {
            return this.e;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class IsProcedureExp extends Exp {
        private final Exp e;

        public IsProcedureExp(Exp e) {
            this.e = e;
        }

        Exp exp() {
            return this.e;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class IsUnitExp extends Exp {
        private final Exp e;

        public IsUnitExp(Exp e) {
            this.e = e;
        }

        Exp exp() {
            return this.e;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class IsPairExp extends Exp {
        private final Exp e;

        public IsPairExp(Exp e) {
            this.e = e;
        }

        Exp exp() {
            return this.e;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class IsListExp extends Exp {
        private final Exp e;

        public IsListExp(Exp e) {
            this.e = e;
        }

        Exp exp() {
            return this.e;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class IsNullExp extends Exp {
        private final Exp e;

        public IsNullExp(Exp e) {
            this.e = e;
        }

        Exp exp() {
            return this.e;
        }

        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }

    class ErrorExp extends Exp {
        public Object accept(Visitor visitor, Object env) {
            return visitor.visit(this, env);
        }
    }
    //endregion
}
