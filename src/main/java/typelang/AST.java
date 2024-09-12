package typelang;

import java.util.ArrayList;
import java.util.List;

/**
 * This class hierarchy represents expressions in the abstract syntax tree
 * manipulated by this interpreter.
 *
 * @author hridesh
 */
public interface AST {
    interface Visitor<V, T> {
        // This interface should contain a signature for each concrete AST node.
        V visit(AST.AddExp e, Env<T> env);

        V visit(AST.UnitExp e, Env<T> env);

        V visit(AST.NumExp e, Env<T> env);

        V visit(AST.BoolExp e, Env<T> env);

        V visit(AST.DivExp e, Env<T> env);

        V visit(AST.MultExp e, Env<T> env);

        V visit(AST.Program p, Env<T> env);

        V visit(AST.SubExp e, Env<T> env);

        V visit(AST.VarExp e, Env<T> env);

        V visit(AST.LetExp e, Env<T> env);

        V visit(AST.DefineDecl d, Env<T> env);

        V visit(AST.LambdaExp e, Env<T> env);

        V visit(AST.CallExp e, Env<T> env);

        V visit(AST.IfExp e, Env<T> env);

        V visit(AST.LessExp e, Env<T> env);

        V visit(AST.EqualExp e, Env<T> env);

        V visit(AST.GreaterExp e, Env<T> env);

        V visit(AST.CarExp e, Env<T> env);

        V visit(AST.CdrExp e, Env<T> env);

        V visit(AST.ConsExp e, Env<T> env);

        V visit(AST.ListExp e, Env<T> env);

        V visit(AST.NullExp e, Env<T> env);

        V visit(AST.RefExp e, Env<T> env);

        V visit(AST.AssignExp e, Env<T> env);

        V visit(AST.DerefExp e, Env<T> env);

        V visit(AST.FreeExp e, Env<T> env);
    }

    abstract class ASTNode implements AST {
        public abstract <V, T> V accept(Visitor<V, T> visitor, Env<T> env);
    }

    class Program extends ASTNode {
        final List<DefineDecl> _decls;
        final Exp _e;

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

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    abstract class Exp extends ASTNode {
        /* no-op */
    }

    class VarExp extends Exp {
        final String _name;

        public VarExp(String name) {
            _name = name;
        }

        public String name() {
            return _name;
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class UnitExp extends Exp {

        public UnitExp() {
            /* no-op */
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }

    }

    class NumExp extends Exp {
        final double _val;

        public NumExp(double v) {
            _val = v;
        }

        public double v() {
            return _val;
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    abstract class CompoundArithExp extends Exp {
        final List<Exp> _rest = new ArrayList<>();

        public CompoundArithExp(List<Exp> args) {
            _rest.addAll(args);
        }

        public List<Exp> all() {
            return _rest;
        }

        public void add(Exp e) {
            _rest.add(e);
        }

    }

    class AddExp extends CompoundArithExp {

        public AddExp(List<Exp> args) {
            super(args);
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class SubExp extends CompoundArithExp {

        public SubExp(List<Exp> args) {
            super(args);
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class DivExp extends CompoundArithExp {

        public DivExp(List<Exp> args) {
            super(args);
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class MultExp extends CompoundArithExp {

        public MultExp(List<Exp> args) {
            super(args);
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class BoolExp extends Exp {
        final boolean _val;

        public BoolExp(boolean b) {
            _val = b;
        }

        public boolean b() {
            return _val;
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
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
        private final List<String> _names;
        private final List<Type> _varTypes; // added for TypeLang
        private final List<Exp> _value_exps;
        private final Exp _body;

        public LetExp(List<String> names, List<Type> varTypes, List<Exp> value_exps, Exp body) {
            _names = names;
            _varTypes = varTypes;
            _value_exps = value_exps;
            _body = body;
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
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

    class DefineDecl extends Exp {
        private final String _name;
        private final Type _type; // added for TypeLang
        private final Exp _value_exp;

        public DefineDecl(String name, Type type, Exp value_exp) {
            _name = name;
            _type = type;
            _value_exp = value_exp;
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
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

    class LambdaExp extends Exp {
        private final List<String> _formals;
        private final List<Type> _types; // added for TypeLang
        private final Exp _body;

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

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class CallExp extends Exp {
        private final Exp _operator;
        private final List<Exp> _operands;

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

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class IfExp extends Exp {
        private final Exp _conditional;
        private final Exp _then_exp;
        private final Exp _else_exp;

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

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class LessExp extends BinaryComparator {
        public LessExp(Exp first_exp, Exp second_exp) {
            super(first_exp, second_exp);
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
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

    class EqualExp extends BinaryComparator {
        public EqualExp(Exp first_exp, Exp second_exp) {
            super(first_exp, second_exp);
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class GreaterExp extends BinaryComparator {
        public GreaterExp(Exp first_exp, Exp second_exp) {
            super(first_exp, second_exp);
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class CarExp extends Exp {
        private final Exp _arg;

        public CarExp(Exp arg) {
            _arg = arg;
        }

        public Exp arg() {
            return _arg;
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class CdrExp extends Exp {
        private final Exp _arg;

        public CdrExp(Exp arg) {
            _arg = arg;
        }

        public Exp arg() {
            return _arg;
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

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

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

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

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    class NullExp extends Exp {
        private final Exp _arg;

        public NullExp(Exp arg) {
            _arg = arg;
        }

        public Exp arg() {
            return _arg;
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }
    }

    /**
     * A ref expression has the syntax
     * <p>
     * (ref expression)
     *
     * @author hridesh
     */
    class RefExp extends Exp {
        private final Exp _value_exp;
        private final Type _type; // added for TypeLang

        public RefExp(Exp value_exp, Type type) {
            _value_exp = value_exp;
            _type = type;
        }

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
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

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
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

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
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

        public <V, T> V accept(Visitor<V, T> visitor, Env<T> env) {
            return visitor.visit(this, env);
        }

        public Exp value_exp() {
            return _value_exp;
        }

    }
}
