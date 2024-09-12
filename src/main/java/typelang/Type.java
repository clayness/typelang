package typelang;

import java.util.List;
import java.util.stream.Collectors;

public interface Type {
    boolean typeEqual(Type other);

    class ErrorT implements Type {
        String _message;

        public ErrorT(String message) {
            _message = message;
        }

        @Override
        public String toString() {
            return "Type error: " + _message;
        }

        public boolean typeEqual(Type other) {
            return other == this;
        }
    }

    class UnitT implements Type {
        private static final UnitT _instance = new UnitT();

        public static UnitT getInstance() {
            return _instance;
        }

        @Override
        public String toString() {
            return "unit";
        }

        public boolean typeEqual(Type other) {
            return other == this;
        }
    }

    class BoolT implements Type {
        private static final BoolT _instance = new BoolT();

        public static BoolT getInstance() {
            return _instance;
        }

        @Override
        public String toString() {
            return "bool";
        }

        public boolean typeEqual(Type other) {
            return other == this;
        }
    }

    class NumT implements Type {
        private static final NumT _instance = new NumT();

        public static NumT getInstance() {
            return _instance;
        }

        @Override
        public String toString() {
            return "num";
        }

        public boolean typeEqual(Type other) {
            return other == this;
        }
    }

    class PairT implements Type {
        protected Type _fst;
        protected Type _snd;

        public PairT(Type fst, Type snd) {
            _fst = fst;
            _snd = snd;
        }

        public Type fst() {
            return _fst;
        }

        public Type snd() {
            return _snd;
        }

        @Override
        public java.lang.String toString() {
            return "(%s %s)".formatted(_fst, _snd);
        }

        public boolean typeEqual(Type other) {
            if (other instanceof PairT pt) {
                return _fst.typeEqual(pt._fst) && _snd.typeEqual(pt._snd);
            }
            return false;
        }
    }

    class ListT extends PairT implements Type {
        public ListT(Type type) {
            super(type, null);

            _snd = this;
        }

        @Override
        public java.lang.String toString() {
            return "List<%s>".formatted(_fst);
        }

        public boolean typeEqual(Type other) {
            if (other instanceof ListT lt) {
                return _fst.typeEqual(lt._fst);
            } else if (other instanceof PairT pt) {
                return _fst.typeEqual(pt._fst) && _snd.typeEqual(pt._snd);
            }
            return false;
        }
    }

    class FuncT implements Type {
        protected List<Type> _argTypes;
        protected Type _returnType;

        public FuncT(List<Type> argTypes, Type returnType) {
            _argTypes = argTypes;
            _returnType = returnType;
        }

        public List<Type> argTypes() {
            return _argTypes;
        }

        public Type returnType() {
            return _returnType;
        }

        @Override
        public java.lang.String toString() {
            return "(%s -> %s)".formatted(
                    _argTypes.stream().map(Object::toString).collect(Collectors.joining(" ")),
                    _returnType
            );
        }

        public boolean typeEqual(Type other) {
            if (other instanceof FuncT ft) {

                List<Type> argTypes = ft._argTypes;
                int size = _argTypes.size();
                if (argTypes.size() == size) {
                    for (int i = 0; i < size; i++) {
                        if (!argTypes.get(i).typeEqual(_argTypes.get(i))) {
                            return false;
                        }
                    }

                    return _returnType.typeEqual(ft._returnType);
                }
            }
            return false;
        }
    }

    class RefT implements Type {
        protected Type _nestType;

        public RefT(Type nestType) {
            _nestType = nestType;
        }

        public Type nestType() {
            return _nestType;
        }

        @Override
        public java.lang.String toString() {
            return "Ref " + _nestType.toString();
        }

        public boolean typeEqual(Type other) {
            if (other instanceof RefT rt) {
                return _nestType.typeEqual(rt._nestType);
            }
            return false;
        }
    }
}
