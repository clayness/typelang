package typelang;

import java.util.List;

public interface Value {

    class NumVal implements Value {
        private final double _val;

        public NumVal(double v) {
            _val = v;
        }

        public double v() {
            return _val;
        }

        @Override
        public String toString() {
            return Double.toString(_val);
        }
    }

    class UnitVal implements Value {
        @Override
        public String toString() {
            return "";
        }
    }

    class DynamicError implements Value {
        private final String message;

        public DynamicError(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    class FunVal implements Value { //New in the typelang
        private final Env<Value> _env;
        private final List<String> _formals;
        private final AST.Exp _body;

        public FunVal(Env<Value> env, List<String> formals, AST.Exp body) {
            _env = env;
            _formals = formals;
            _body = body;
        }

        public Env<Value> env() {
            return _env;
        }

        public List<String> formals() {
            return _formals;
        }

        public AST.Exp body() {
            return _body;
        }

        @Override
        public String toString() {
            return "(lambda (%s) %s)".formatted(
                    String.join(" ", _formals),
                    _body.accept(new Printer.Formatter<>(), _env));
        }
    }

    class BoolVal implements Value {
        private final boolean _val;

        public BoolVal(boolean v) {
            _val = v;
        }

        public boolean v() {
            return _val;
        }

        @Override
        public String toString() {
            return _val ? "#t" : "#f";
        }
    }

    class PairVal implements Value {
        protected Value _fst;
        protected Value _snd;

        public PairVal(Value fst, Value snd) {
            _fst = fst;
            _snd = snd;
        }

        public Value fst() {
            return _fst;
        }

        public Value snd() {
            return _snd;
        }

        @Override
        public java.lang.String toString() {
            if (isList()) {
                return listToString();
            } else {
                return "(" + _fst.toString() + " " + _snd.toString() + ")";
            }
        }

        private boolean isList() {
            if (_snd instanceof Value.Null) {
                return true;
            } else if (_snd instanceof PairVal p) {
                return p.isList();
            } else {
                return false;
            }
        }

        private java.lang.String listToString() {
            StringBuilder result = new StringBuilder("(");
            result.append(_fst.toString());
            Value next = _snd;
            while (!(next instanceof Value.Null)) {
                result.append(" ").append(((PairVal) next)._fst.toString());
                next = ((PairVal) next)._snd;
            }
            return result + ")";
        }
    }

    class Null implements Value {
        @Override
        public String toString() {
            return "()";
        }
    }

    class RefVal implements Value {
        private final int _loc;

        public RefVal(int loc) {
            _loc = loc;
        }

        @Override
        public String toString() {
            return "loc:" + this._loc;
        }

        public int loc() {
            return _loc;
        }
    }
}
