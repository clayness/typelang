package typelang;

import java.util.List;

import typelang.AST.Exp;

public interface Value {
    String tostring();

    class RefVal implements Value {
        private final int _loc;

        public RefVal(int loc) {
            _loc = loc;
        }

        public String tostring() {
            return "loc:" + this._loc;
        }

        public int loc() {
            return _loc;
        }
    }

    class FunVal implements Value {
        private final Env<Value> _env;
        private final List<String> _formals;
        private final Exp _body;

        public FunVal(Env<Value> env, List<String> formals, Exp body) {
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

        public Exp body() {
            return _body;
        }

        public String tostring() {
            StringBuilder result = new StringBuilder("(lambda ( ");
            for (String formal : _formals) {
                result.append(formal).append(" ");
            }
            result.append(") ");
            result.append(_body.accept(new Printer.Formatter(), _env));
            return result + ")";
        }
    }

    class NumVal implements Value {
        private final double _val;

        public NumVal(double v) {
            _val = v;
        }

        public double v() {
            return _val;
        }

        public String tostring() {
            int tmp = (int) _val;
            if (tmp == _val) {
                return "" + tmp;
            } else {
                return "" + _val;
            }
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

        public String tostring() {
            if (_val) {
                return "#t";
            } else {
                return "#f";
            }
        }
    }

    class StringVal implements Value {
        private final java.lang.String _val;

        public StringVal(String v) {
            _val = v;
        }

        public String v() {
            return _val;
        }

        public java.lang.String tostring() {
            return _val;
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

        public java.lang.String tostring() {
            if (isList()) {
                return listToString();
            } else {
                return "(" + _fst.tostring() + " " + _snd.tostring() + ")";
            }
        }

        boolean isList() {
            if (_snd instanceof Value.Null) {
                return true;
            } else {
                return _snd instanceof PairVal && ((PairVal) _snd).isList();
            }
        }

        java.lang.String listToString() {
            StringBuilder result = new StringBuilder("(");
            result.append(_fst.tostring());
            Value next = _snd;
            while (!(next instanceof Value.Null)) {
                result.append(" ").append(((PairVal) next)._fst.tostring());
                next = ((PairVal) next)._snd;
            }
            return result + ")";
        }
    }

    class Null implements Value {
        public Null() {
            /* no-op */
        }

        public String tostring() {
            return "()";
        }
    }

    class UnitVal implements Value {
        public String tostring() {
            return "";
        }
    }

    class DynamicError implements Value {
        private final String message;

        @SuppressWarnings("unused")
        public DynamicError() {
            this("Unknown dynamic error.");
        }

        public DynamicError(String message) {
            this.message = message;
        }

        public String tostring() {
            return message;
        }
    }
}
