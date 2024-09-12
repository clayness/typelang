grammar DefineLang;

import VarLang; //Import all rules from VarLang grammar.

// We are redefining programs to be zero or more define declarations 
// followed by an optional expression.
program returns [Program ast]
 		locals [
 		    ArrayList<DefineDecl> defs = new ArrayList<DefineDecl>();,
 		    Exp expr = new UnitExp();
        ] :
		(def=definedecl { $defs.add($def.ast); } )* (e=exp { $expr = $e.ast; } )? 
		{ $ast = new Program($defs, $expr); }
		;

// New declaration for global definitions.
definedecl returns [DefineDecl ast] :
 		'(' Define 
 			id=Identifier
 			e=exp
 		')' { $ast = new DefineDecl($id.text, $e.ast); }
 		;

 // Lexical Specification of this Programming Language
 //  - lexical specification rules start with uppercase
 
Define : 'define' ;
