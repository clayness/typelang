grammar RefLang;

import FuncLang; //Import all rules from FuncLang grammar.

exp returns [Exp ast]: 
		  va=varexp        { $ast = $va.ast; }
		| num=numexp       { $ast = $num.ast; }
		| bl=boolexp       { $ast = $bl.ast; }
    	| add=addexp       { $ast = $add.ast; }
    	| sub=subexp       { $ast = $sub.ast; }
    	| mul=multexp      { $ast = $mul.ast; }
    	| div=divexp       { $ast = $div.ast; }
    	| let=letexp       { $ast = $let.ast; }
    	| lam=lambdaexp    { $ast = $lam.ast; }
    	| call=callexp     { $ast = $call.ast; }
    	| i=ifexp          { $ast = $i.ast; }
    	| less=lessexp     { $ast = $less.ast; }
    	| eq=equalexp      { $ast = $eq.ast; }
    	| gt=greaterexp    { $ast = $gt.ast; }
    	| car=carexp       { $ast = $car.ast; }
    	| cdr=cdrexp       { $ast = $cdr.ast; }
    	| cons=consexp     { $ast = $cons.ast; }
    	| list=listexp     { $ast = $list.ast; }
    	| nl=nullexp       { $ast = $nl.ast; }
    	| ref=refexp       { $ast = $ref.ast; }       // new for RefLang
    	| deref=derefexp   { $ast = $deref.ast; }     // new for RefLang
    	| assign=assignexp { $ast = $assign.ast; }    // new for RefLang
    	| free=freeexp     { $ast = $free.ast; }      // new for RefLang
    	;

 // New Expressions for RefLang
refexp returns [RefExp ast] :
    '(' Ref e=exp ')' { $ast = new RefExp($e.ast); }
    ;

derefexp returns [DerefExp ast] :
    '(' Deref e=exp ')' { $ast = new DerefExp($e.ast); }
    ;

assignexp returns [AssignExp ast] :
    '(' Assign e1=exp e2=exp ')' { $ast = new AssignExp($e1.ast, $e2.ast); }
    ;

freeexp returns [FreeExp ast] :
    '(' Free e=exp ')' { $ast = new FreeExp($e.ast); }
    ;
         
 // Lexical Specification of this Programming Language
 //  - lexical specification rules start with uppercase
 Ref    : 'ref' ;
 Deref  : 'deref' ;
 Assign : 'set!' ;
 Free   : 'free' ;