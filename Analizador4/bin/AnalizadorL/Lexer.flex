package AnalizadorL;
import static AnalizadorL.Tokens.*;
%%
%class Lexer
%type Tokens
L=[a-zA-Z_]
D=[0-9]
espacio=[ ,\t,\r]+
comillas=[\"]+
comillasS=[']+
opAg=[\(,\),\[,\],{,}]
opAr=[*,\+,\-,/,%]
opRe=[<,>]
opLo=[&,|,!]
com=[\,]
SMB=[\!,#,$,%,&,\(,\),*,\+,\,,\-,\.,/,:,;,<,=,>,?,@,\[,\],\^,_,`,{,|,},¿]+
%{
    public String lexeme;
%}
%%
"++" {lexeme=yytext(); return inc;}
"--" {lexeme=yytext(); return dec;}
"+" {lexeme=yytext(); return suma;}
"-" {lexeme=yytext(); return resta;}
"*" {lexeme=yytext(); return mult;}
"/" {lexeme=yytext(); return div;}

-?("(-"{D}+")")|-?{D}+ {lexeme=yytext(); return nume;}
{com} {lexeme=yytext();return coma;}
("/*")({comillas}|{L}|{D}|{espacio}|{SMB}|{comillasS}|{opAg}|{opAr}|{opLo}|{opRe}|"\n")*("*/") {/*Ignore*/}

{comillasS}({L}|{D}){comillasS} {lexeme=yytext(); return litcar;}
{comillasS}({L}|{D})* {return ERRchar;}
{comillasS}({L}|{D})+{comillasS} {return ERRMDig;}

int|char|float {lexeme=yytext(); return Tipo_Dato;}
for|if|else {lexeme=yytext(); return Palabra_Reservada;}

(" "|"\t"|\r)+ {/*Ignore*/}
"<=" {lexeme=yytext(); return meniq;}
">=" {lexeme=yytext(); return mayiq;}
"==" {lexeme=yytext(); return igual;}
"!=" {lexeme=yytext(); return dif;}
"<" {lexeme=yytext(); return men;}
">" {lexeme=yytext(); return may;}

{L}({L}|{D})* {lexeme=yytext(); return id;}
-?([1-9][0-9]*|\-[1-9][0-9]*|0)\.([0-9]*[1-9]|[0-9]*[1-9](e|E)(\+|\-)[1-9][0-9]*) {lexeme=yytext(); return numf;}
"(" {lexeme=yytext(); return parena;}
")" {lexeme=yytext(); return parenc;}
"{" {lexeme=yytext(); return llavea;}
"}" {lexeme=yytext(); return llavec;}
"//".* {/*Ignore*/}
"\n" {lexeme=yytext(); return Salto_de_Linea;}
"=" {lexeme=yytext(); return asignacion;}
";"	{lexeme=yytext();return finSentencia;}

 . {return ERROR;}