grammar TypeLang;

import RefLang;

@header {
import typelang.Type;
import typelang.Type.*;
}

definedecl returns [DefineDecl ast] :
    '(' Define
        id=Identifier ':' t=type
        e=exp
    ')' { $ast = new DefineDecl($id.text, $t.ty, $e.ast); }
    ;

// ******************* Type Expressions for TypeLang **********************
type returns [Type ty]: 
      bty=booltype   { $ty = $bty.ty; }
    | fty=funtype    { $ty = $fty.ty; }
    | nty=numtype    { $ty = $nty.ty; }
    | lty=listtype   { $ty = $lty.ty; }
    | pty=pairtype   { $ty = $pty.ty; }
    | rty=reftype    { $ty = $rty.ty; }
    | uty=unittype   { $ty = $uty.ty; }
    ;

booltype returns [BoolT ty] : 
    Bool { $ty = Type.BoolT.getInstance(); }
    ;

funtype returns [FuncT ty]
    locals [ArrayList<Type> argtypes = new ArrayList<Type>();  ] :
    '('
        ( ty1=type {  $argtypes.add($ty1.ty); } )*
          '->'
          ty2=type
    ')' { $ty = new FuncT($argtypes, $ty2.ty); }
    ;

numtype returns [NumT ty] : 
    Num { $ty = Type.NumT.getInstance(); }
    ;

listtype returns [ListT ty] :
    ListT '<' ty1=type '>' { $ty = new ListT($ty1.ty); }
    ;

pairtype returns [PairT ty] :
    '(' ty1=type ',' ty2=type ')' {  $ty = new PairT($ty1.ty, $ty2.ty); }
    ;

reftype returns [RefT ty] :
    RefT ty1=type { $ty = new RefT($ty1.ty); }
    ;

unittype returns [UnitT ty] : 
    UnitT { $ty = Type.UnitT.getInstance(); }
    ;

// ******************* New Expressions for RefLang **********************
refexp returns [RefExp ast] :
    '(' Ref ':' ty1=type e=exp ')' { $ast = new RefExp($e.ast, $ty1.ty); }
    ;

// New Expressions for FuncLang
lambdaexp returns [LambdaExp ast]
    locals [
        ArrayList<String> formals = new ArrayList<String>(); ,
        ArrayList<Type> types = new ArrayList<Type>()
    ] :
    '(' Lambda
        '(' (id=Identifier ':' ty1=type { $formals.add($id.text); $types.add($ty1.ty); } )* ')'
        body=exp
    ')' { $ast = new LambdaExp($formals, $types, $body.ast); }
    ;

listexp returns [ListExp ast]
    locals [ArrayList<Exp> list = new ArrayList<Exp>(); ] :
    '(' List ':' ty=type
        ( e=exp { $list.add($e.ast); } )*
    ')' { $ast = new ListExp($ty.ty,$list); }
    ;

letexp  returns [LetExp ast]
    locals [
        ArrayList<String> names   = new ArrayList<String>();,
        ArrayList<Type> types     = new ArrayList<Type>();,
        ArrayList<Exp> value_exps = new ArrayList<Exp>();
    ] :
    '(' Let
        '(' ( '(' id=Identifier ':' ty1=type e=exp ')' { $names.add($id.text); $types.add($ty1.ty); $value_exps.add($e.ast); } )+  ')'
        body=exp
    ')' { $ast = new LetExp($names, $types, $value_exps, $body.ast); }
    ;

// Lexical Specification of this Programming Language
//  - lexical specification rules start with uppercase
//------------------------------------------------------
// Lexer tokens for new type definitions
Num     : 'num' ;
Bool    : 'bool' ;
ListT   : 'List' ;
RefT    : 'Ref' ;
UnitT   : 'unit' ;
