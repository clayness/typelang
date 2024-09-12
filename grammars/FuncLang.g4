grammar FuncLang;

import DefineLang; //Import all rules from DefineLang grammar.

 exp returns [Exp ast]: 
		  va=varexp     { $ast = $va.ast; }
		| num=numexp    { $ast = $num.ast; }
		| bl=boolexp    { $ast = $bl.ast; }
        | add=addexp    { $ast = $add.ast; }
        | sub=subexp    { $ast = $sub.ast; }
        | mul=multexp   { $ast = $mul.ast; }
        | div=divexp    { $ast = $div.ast; }
        | let=letexp    { $ast = $let.ast; }
        | lam=lambdaexp { $ast = $lam.ast; }
        | call=callexp  { $ast = $call.ast; }
        | i=ifexp       { $ast = $i.ast; }
        | less=lessexp  { $ast = $less.ast; }
        | eq=equalexp   { $ast = $eq.ast; }
        | gt=greaterexp { $ast = $gt.ast; }
        | car=carexp    { $ast = $car.ast; }
        | cdr=cdrexp    { $ast = $cdr.ast; }
        | cons=consexp  { $ast = $cons.ast; }
        | list=listexp  { $ast = $list.ast; }
        | nl=nullexp    { $ast = $nl.ast; }
        ;

 // New Expressions for FuncLang

 lambdaexp returns [LambdaExp ast] 
        locals [ArrayList<String> formals = new ArrayList<String>(); ] :
 		'(' Lambda
 			'(' (id=Identifier { $formals.add($id.text); } )* ')'
 			body=exp 
 		')' { $ast = new LambdaExp($formals, $body.ast); }
 		;

 callexp returns [CallExp ast] 
        locals [ArrayList<Exp> arguments = new ArrayList<Exp>();  ] :
 		'(' f=exp 
 			( e=exp { $arguments.add($e.ast); } )* 
 		')' { $ast = new CallExp($f.ast,$arguments); }
 		;

 ifexp returns [IfExp ast] :
 		'(' If 
 		    e1=exp 
 			e2=exp 
 			e3=exp 
 		')' { $ast = new IfExp($e1.ast,$e2.ast,$e3.ast); }
 		;

 lessexp returns [LessExp ast] :
 		'(' Less 
 		    e1=exp 
 			e2=exp 
 		')' { $ast = new LessExp($e1.ast,$e2.ast); }
 		;

 equalexp returns [EqualExp ast] :
 		'(' Equal 
 		    e1=exp 
 			e2=exp 
 		')' { $ast = new EqualExp($e1.ast,$e2.ast); }
 		;

 greaterexp returns [GreaterExp ast] :
 		'(' Greater 
 		    e1=exp 
 			e2=exp 
 		')' { $ast = new GreaterExp($e1.ast,$e2.ast); }
 		;

// Expressions related to list

 carexp returns [CarExp ast] :
 		'(' Car 
 		    e=exp 
 		')' { $ast = new CarExp($e.ast); }
 		;

 cdrexp returns [CdrExp ast] :
 		'(' Cdr 
 		    e=exp 
 		')' { $ast = new CdrExp($e.ast); }
 		;

 consexp returns [ConsExp ast] :
 		'(' Cons 
 		    e1=exp 
 			e2=exp 
 		')' { $ast = new ConsExp($e1.ast,$e2.ast); }
 		;

 listexp returns [ListExp ast] 
        locals [ArrayList<Exp> list = new ArrayList<Exp>();] :
 		'(' List
 		    ( e=exp { $list.add($e.ast); } )* 
 		')' { $ast = new ListExp($list); }
 		;

 nullexp returns [NullExp ast] :
 		'(' Null 
 		    e=exp 
 		')' { $ast = new NullExp($e.ast); }
 		;

 boolexp returns [BoolExp ast] :
 		TrueLiteral { $ast = new BoolExp(true); } 
 		| FalseLiteral { $ast = new BoolExp(false); } 
 		;
 


 // Lexical Specification of this Programming Language
 //  - lexical specification rules start with uppercase
 
 Lambda : 'lambda' ;
 If : 'if' ; 
 Car : 'car' ; 
 Cdr : 'cdr' ; 
 Cons : 'cons' ; 
 List : 'list' ; 
 Null : 'null?' ; 
 Less : '<' ;
 Equal : '=' ;
 Greater : '>' ;
 TrueLiteral : '#t' ;
 FalseLiteral : '#f' ;