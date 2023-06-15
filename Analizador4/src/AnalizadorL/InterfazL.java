package AnalizadorL;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import java.awt.event.*;
import java.net.*;
import java.util.Vector;
import java.io.*;

class Datos {
	private int op;

	public Datos(int op) {
		this.op = op;
	}

	public int Opcion() {
		return op;
	}
}

@SuppressWarnings("serial")
class ArchS extends JDialog {
	private JLabel et, img;
	private JButton b1, b2;
	private Datos datl = null;
	int op = 0;

	public ArchS(JFrame owner, boolean modal) {
		super(owner, modal);
		setTitle("");
		setSize(400, 150);
		setResizable(false);
		setLocationRelativeTo(this);

		JPanel arr = new JPanel();
		URL ruta = getClass().getResource("/AnalizadorL/Recursos/GuardarA.png");
		img = new JLabel(new ImageIcon(ruta));
		arr.add(img);
		et = new JLabel("¿Desea guardar cambios en este archivo?");
		et.setFont(new Font("Arial", 3, 15));
		et.setForeground(new Color(36, 8, 69));
		arr.add(et);
		add(arr, BorderLayout.CENTER);
		arr.setBackground(Color.WHITE);
		b1 = new JButton("Salir");
		b1.setFont(new Font("Arial", 3, 15));
		b1.setForeground(new Color(36, 8, 69));
		b1.setBackground(Color.WHITE);
		b2 = new JButton("Guardar");
		b2.setFont(new Font("Arial", 3, 15));
		b2.setForeground(Color.WHITE);
		b2.setBackground(new Color(36, 8, 69));
		b2.setBorder(BorderFactory.createLineBorder(new Color(36, 8, 69), 5));

		JPanel bot = new JPanel();

		bot.add(b2);
		bot.add(b1);
		add(bot, BorderLayout.SOUTH);
		bot.setBackground(Color.WHITE);
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				op = 1;
				datl = new Datos(op);
				dispose();
				setVisible(false);
			}
		});
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				op = 2;
				datl = new Datos(op);
				dispose();
				setVisible(false);
			}
		});
		datl = new Datos(op);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	public Datos mostrarCuadDialog() {
		setVisible(true);
		return datl;
	}
}

@SuppressWarnings("serial")
public class InterfazL extends JFrame implements ActionListener {
	private java.util.Stack<String> pila = new java.util.Stack<String>(), sema = new java.util.Stack<String>();
	private java.util.Stack<String> condicion = new java.util.Stack<String>(),contaif = new java.util.Stack<String>(),contafor = new java.util.Stack<String>();
	private int x = 0, y = 0, poss;
	//Tokens del lexer    
	private String columnas[] = {"id", "int", "float", "char", ",", ";", "+", "-", "/", "*", "(", ")", "{", "}","<", ">", ">=", "<=", "==", "!=", "=",  "num", "numf", "litcar", "for", "if", "else",  "++", "--", "$", "P", "Tipo", "V", "Sen", "A", "Exp", "Term", "T", "F", "E", "I", "Con", "D", "EL", "For2", "in", "C", "ad"};
	
	//Estados q de transicion
	private String filas[] = {"I0 ", "I1 ", "I2 ", "I3 ", "I4 ", "I5 ", "I6 ", "I7 ", "I8 ", "I9 ", "I10 ", "I11 ", "I12 ", "I13 ", "I14 ", "I15 ", "I16 ", "I17 ", "I18 ", "I19 ", "I20 ", "I21 ", "I22 ", "I23 ", "I24 ", "I25 ", "I26 ", "I27 ", "I28 ", "I29 ", "I30 ", "I31 ", "I32 ", "I33 ", "I34 ", "I35 ", "I36 ", "I37 ", "I38 ", "I39 ", "I40 ", "I41 ", "I42 ", "I43 ", "I44 ", "I45 ", "I46 ", "I47 ", "I48 ", "I49 ", "I50 ", "I51 ", "I52 ", "I53 ", "I54 ", "I55 ", "I56 ", "I57 ", "I58 ", "I59 ", "I60 ", "I61 ", "I62 ", "I63 ", "I64 ", "I65 ", "I66 ", "I67 ", "I68 ", "I69 ", "I70 ", "I71 ", "I72 ", "I73 ", "I74 ", "I75 ", "I76 ", "I77 ", "I78 ", "I79 ", "I80 ", "I81 ", "I82 ", "I83 ", "I84 ", "I85 ", "I86 ", "I87 ", "I88 ", "I89 ", "I90 ", "I91 "};
	private String term[] = { "P", "P", "Tipo", "Tipo", "Tipo", "V", "V", "Sen", "Sen", "Sen", "Sen", "A", "Exp", "Term", "E", "E", "E", "F", "F", "F", "F", "F", "T", "T", "T",  "I", "Con", "D", "D", "D", "D", "D", "D", "EL", "EL", "For2", "in", "in", "C", "ad", "ad", "ad", "ad"};
	
	//Lista de Producciones sintacticas
	private String prod[] = {"P1 ", "P2 ", "P3 ", "P4 ", "P5 ", "P6 ", "P7 ", "P8 ", "P9 ", "P10 ", "P11 ", "P12 ", "P13 ", "P14 ", "P15 ", "P16 ", "P17 ", "P18 ", "P19 ", "P20 ", "P21 ", "P22 ", "P23 ", "P24 ", "P25 ", "P26 ", "P27 ", "P28 ", "P29 ", "P30 ", "P31 ", "P32 ", "P33 ", "P34 ", "P35 ", "P36 ", "P37 ", "P38 ", "P39 ", "P40 ", "P41 ", "P42 ", "P43 "};
	private String comp[] = {"P -> Tipo id V", "P -> Sen", "Tipo -> int", "Tipo -> float", "Tipo -> char", "V -> , id V", "V -> ; P", "Sen -> A Sen", "Sen -> I Sen","Sen -> For2 Sen", "Sen -> ç", "A -> id = Exp ;", "Exp -> Term E", "Term ->  F T", "E -> + Term E", "E -> - Term E", "E -> ç", "F -> id", "F -> ( Exp )", "F -> num", "F -> numf", "F -> litcar",  "T -> * F T", "T -> / F T", "T -> ç", "I -> if ( Con ) { Sen } EL", "Con -> id D id", "D -> <", "D -> >", "D -> <=", "D -> >=", "D -> ==", "D -> !=", "EL -> else { Sen }", "EL -> ç", "For2 -> for ( in ; Con ; ad ) { Sen }", "in -> int C", "in -> C", "C -> id = num", "ad -> id ++", "ad -> id --", "ad -> ++ id", "ad -> -- id",};
	private int pops[] = {6, 2, 2, 2, 2, 6, 4, 4, 4, 4, 0, 8, 4, 4, 6, 6, 0, 2, 6, 2, 2, 2, 6, 6, 0, 16, 6, 2, 2, 2, 2, 2, 2, 8, 0, 22, 4, 2, 6, 4, 4, 4, 4,};
	private int suma[][] = {
	//		    int 0    float 1   char	2
	/*int*/		{0,        1,       -1},
	/*float*/	{1,        1,       -1},
	/*char*/	{-1,      -1,        2}};
	
	private int rest[][] = {
	//		    int 0    float 1   char	2
	/*int*/		{0,        1,        -1},
	/*float*/	{1,        1,        -1},
	/*char*/	{-1,      -1,         2}};
	
	private int divi[][] = {
	//		    int 0    float 1   char	2
	/*int*/		{1,        1,        -1},
	/*float*/	{1,        1,        -1},
	/*char*/	{-1,      -1,        -1}};
	
	private int mult[][] = {
	//		    int 0    float 1   char	2
	/*int*/		{0,        1,        -1},
	/*float*/	{1,        1,        -1},
	/*char*/	{-1,      -1,        -1}};
	
	private boolean igua[][] = {
	//		     int         float    char	
	/*int*/		{true,       false,   false},
	/*float*/	{true,       true,    false},
	/*char*/	{false,      false,   true}};
	
	private boolean condi[][] = {
			//		    int 0    float 1   char	2
			/*int*/		{true,    true,     false},
			/*float*/	{true,    true,     false},
			/*char*/	{false,   false,    true}};
	
	private String trans[][] = {
	//{						  "id"			 "int"  		"float"  		"char" 		      ","			   ";"				"+", 			"-", 			"/", 			"*", 			"(", 		  ")",	          "{",		      "}",				"<", 			">", 			">=", 			"<=", 			"==", 		"!=", 			 "=",  			"num",			 "numf", 		"litcar",		 "for",			 "if", 			"else",			 "++",			 "--",			"$",			"P",			"Tipo",			"V",			"Sen",		    "A",			"Exp",			"Term",			"T",			"F",			"E",			"I",			"Con",			"D",			"EL",			"For2",			"in", 			"C",			"ad"};
	//		        	   	    0		       1               2               3               4                5                6               7               8             9              10              11               12             13             14              15            16              17              18                19               20              21              22              23              24              25	          26              27              28             29            30               31             32                33             34              35               36             37              38              39               40             41               42             43              44             45               46              47       
	/*   0    I0	*/		{"I10 ",		"I4 ",	 		"I5 ", 			"I6 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P11 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I12 ", 		"I11 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P11 ",			"I1 ",			"I2 ",			"saltar ", 		"I3 ",	 		"I7 ",	 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I8 ",	 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"I9 ", 			"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I1	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"Aceptar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I2	*/		{"I13 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I3	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P2 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I4	*/		{"P3 ",			"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I5	*/		{"P4 ",			"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I6	*/		{"P5 ",			"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I7	*/		{"I10 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P11 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I12 ", 		"I11 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P11 ",			"saltar ",		"saltar ",		"saltar ", 		"I14 ", 		"I7 ", 			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I8 ", 	 		"saltar ", 		"saltar ",	 	"saltar ", 		"I9 ", 			"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I8	*/		{"I10 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P11 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I12 ", 		"I11 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P11 ",			"saltar ",		"saltar ",		"saltar ", 		"I15 ", 		"I7 ", 			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I8 ", 	 		"saltar ", 		"saltar ",	 	"saltar ", 		"I9 ", 			"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I9	*/		{"I10 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P11 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I12 ", 		"I11 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P11 ",			"saltar ",		"saltar ",		"saltar ", 		"I16 ", 		"I7 ",	 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I8 ", 	 		"saltar ", 		"saltar ",	 	"saltar ", 		"I9 ", 			"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I10	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"I17 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I11	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I18 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I12	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I19 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I13	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"I21 ",			"I22 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"I20 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I14	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P8 ",	 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P8 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I15	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P9 ", 			"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P9 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I16	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P10 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P10 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I17	*/		{"I26 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I27 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I28 ", 		"I29 ", 		"I30 ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"I23 ", 		"I24 ", 		"saltar ", 		"I25 ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I18	*/		{"I32 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"I31 ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I19	*/		{"I36 ",		"I34 ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"I33 ", 		"I35 ",			"saltar "},	
	/*   0    I20	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P1 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I21	*/		{"I37 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I22	*/		{"I10 ",		"I4 ",	 		"I5 ", 			"I6 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P11 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I12 ", 		"I11 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P11 ",			"I38 ",			"I2 ",			"saltar ", 		"I3 ", 			"I7 ",	 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I8 ", 	 		"saltar ", 		"saltar ",	 	"saltar ", 		"I9 ", 			"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I23	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"I39 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I24	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P17 ",			"I41 ", 		"I42 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P17 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I40 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I25	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P25 ",			"P25 ", 		"P25 ", 		"I45 ", 		"I44 ", 		"saltar ", 		"P25 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I43 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I26	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P18 ",			"P18 ", 		"P18 ", 		"P18 ", 		"P18 ", 		"saltar ", 		"P18 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I27	*/		{"I26 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I27 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I28 ", 		"I29 ", 		"I30 ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"I46 ", 		"I24 ", 		"saltar ", 		"I25 ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I28	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P20 ",			"P20 ", 		"P20 ", 		"P20 ", 		"P20 ", 		"saltar ", 		"P20 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P20 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I29	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P21 ",			"P21 ", 		"P21 ", 		"P21 ", 		"P21 ", 		"saltar ", 		"P21 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P21 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I30	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P22 ",			"P22 ", 		"P22 ", 		"P22 ", 		"P22", 			"saltar ", 		"P22 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I31	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I47 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I32	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I49 ", 	 	"I50 ", 		"I52 ",	 		"I51 ", 		"I53 ", 		"I54 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"I48 ",	 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I33	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"I55 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I34	*/		{"I36 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"I56 ",			"saltar "},	
	/*   0    I35	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P38 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I36	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"I57 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I37	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"I21 ",			"I22 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"I58 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I38	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P7 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I39	*/		{"P12 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P12 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P12 ", 		"P12 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P12 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I40	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P13 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P13 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I41	*/		{"I26 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I27 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I28 ", 		"I29 ", 		"I30 ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I59 ", 		"saltar ", 		"I25 ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I42	*/		{"I26 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I27 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I28 ", 		"I29 ", 		"I30 ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I60 ", 		"saltar ", 		"I25 ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I43	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P14 ",			"P14 ", 		"P14 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P14 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I44	*/		{"I26 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I27 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I28 ", 		"I29 ", 		"I30 ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I61 ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I45	*/		{"I26 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I27 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I28 ", 		"I29 ", 		"I30 ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I62 ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I46	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I63 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I47	*/		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I64 ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I48	*/		{"I65 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I49	*/		{"P28 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I50	*/		{"P29 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},	
	/*   0    I51	*/  	{"P30 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},
	/* 	 0    I52	*/   	{"P31 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},
	/*   0    I53	*/   	{"P32 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},
	/*   0    I54	*/   	{"P33 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},
	/*   0    I55	*/   	{"I32 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"I66 ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},
	/*   0    I56	*/   	{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P37 ",		    "saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},
	/*   0    I57	*/   	{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I67 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},
	/*   0    I58	*/   	{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P6 ",		    "saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar "},
	/*   0    I59	*/ 		{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P17 ",			"I41 ", 		"I42 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P17 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I68 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*60*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P17 ",			"I41 ", 		"I42 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P17 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I69 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*61*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P25 ",			"P25 ", 		"P25 ", 		"I45 ", 		"I44 ", 		"saltar ", 		"P25 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I70 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*62*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P25 ",			"P25 ", 		"P25 ", 		"I45 ", 		"I44 ", 		"saltar ", 		"P25 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I71 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*63*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P19 ",			"P19 ", 		"P19 ", 		"P19 ", 		"P19 ", 		"saltar ", 		"P19 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"P19 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*64*/					{"I10 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P11 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I12 ", 		"I11 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P11 ",			"saltar ",		"saltar ",		"saltar ", 		"I72 ", 		"I7 ", 			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I8 ", 	 		"saltar ", 		"saltar ",	 	"saltar ", 		"I9 ", 			"saltar ", 		"saltar ", 		"saltar "},
	/*65*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P27 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P27 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*66*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"I73 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*67*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P39 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*68*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P15 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P15 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*69*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P16 ",			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P16 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*70*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P23 ",			"P23 ", 		"P23 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P23 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*71*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"P24 ",			"P24 ", 		"P24 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P24 ", 		"saltar ", 		"saltar ", 			"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*72*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I74 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*73*/					{"I76 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"I77 ", 		"I78 ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I75 "},
	/*74*/					{"P35 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P35 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P35 ", 		"P35 ", 		"I80 ",			"saltar ", 		"saltar ", 		"P35 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"I79 ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*75*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I81 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*76*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"I82 ", 		"I83 ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*77*/					{"I84 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*78*/					{"I85 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*79*/					{"P26 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P26 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P26 ", 		"P26 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P26 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*80*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I86 ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*81*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I87 ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*82*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P40 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*83*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P41 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*84*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P42 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*85*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P43 ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*86*/					{"I10 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P11 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I12 ", 		"I11 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P11 ",			"saltar ",		"saltar ",		"saltar ", 		"I88 ", 		"I7 ", 			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I8 ",	 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"I9 ", 			"saltar ", 		"saltar ", 		"saltar "},
	/*87*/					{"I10 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P11 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I12 ", 		"I11 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P11 ",			"saltar ",		"saltar ",		"saltar ", 		"I89 ", 		"I7 ", 			"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I8 ", 		 	"saltar ", 		"saltar ",	 	"saltar ", 		"I9 ", 			"saltar ", 		"saltar ", 		"saltar "},
	/*88*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I90 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*89*/					{"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"I91 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*90*/					{"P34 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P34 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P34 ", 		"P34 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P34 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},
	/*91*/					{"P36 ",		"saltar ", 		"saltar ", 		"saltar ",		"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P36 ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"P36 ", 		"P36 ", 		"saltar ",		"saltar ", 		"saltar ", 		"P36 ",			"saltar ",		"saltar ",		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 	 	"saltar ", 		"saltar ",	 	"saltar ", 		"saltar ", 		"saltar ", 		"saltar ", 		"saltar "},	
	};
	private JMenuBar barraM;
	private JMenu opm1, opm2, opsub, opsub1, opsub2;
	private JMenuItem op2, op3, op4, op5, op6, op7, op8, op9, op10, op11, op12, op13, op14, op15, op16, op17, op18;
	private Archivo oba;
	private JButton b1, b2, b3, b4, b5, b6, b7;
	private JTextField ct;
	private JLabel im;
	private String codigo = "", tok = "", incre = "";
	private JTextPane artx;
	private JTextArea num, consola;
	private JFileChooser archivo;
	private ArchS Seg;
	private Datos da;
	private int op, tam = 12, tl = 0;
	private String letra = "Arial", nombre = "", nombre1 = "", sig ="";
	private String nomidp = " ", chars = " ", ints = " " , floats = " ", idnueva, tipos = "", errorssin = "";
	@SuppressWarnings("unused")
	private int pos = 0, antpos = 0 ,confor=0,conif=0,conel=0;
	private JTabbedPane pestañas;
	private boolean col, reser, band = true, bano = true, banfor = true, banid = true, banel = false, bandera = true, b01 = true, b02 = true, b03 = true, b04 = true, b05 = true, b06 = true, b07 = true, b08 = true, b09 = true, b10 = true;
	private StyleContext sc;
	@SuppressWarnings("unused")
	private DefaultStyledDocument doc;
	JTable tabla, tabla1,tabla2;
	DefaultTableModel modelo, modelo1,modelo2;
	String TS[][], errant = "", idm = "", idf = "";
	Vector<String> titulos, titulos1,titulos2;
	@SuppressWarnings("rawtypes")
	Vector<Vector> datos, datos1, datos2;
	Vector<String> vvec;
	String id[],tipo[], asig, tasig, anyyl,con1, con2;
	boolean ban1=true,ban2=true, bann = true;
	boolean comm = true, vers = true, hacer=true,tokens=false,vari=true;
	boolean parentesis=false;
	String tant = "";
	String tipoD;
	
	@SuppressWarnings("rawtypes")
	public InterfazL() {
		setTitle("CatCode");
		setSize(1000, 600);
		setLocationRelativeTo(this);
		Image icon = new ImageIcon(getClass().getResource("/AnalizadorL/Recursos/RutaA.png")).getImage();
		setIconImage(icon);

		JPanel arr = new JPanel();
		URL ruta = getClass().getResource("/AnalizadorL/Recursos/RutaA.png");
		im = new JLabel(new ImageIcon(ruta));
		ct = new JTextField(75);
		ct.setEnabled(false);
		ct.setDisabledTextColor(new Color(175, 223, 248));
		ct.setBackground(/* new Color(105, 105, 255) */new Color(36, 8, 69));
		ct.setFont(new Font(letra, Font.BOLD, 13));
		ct.setBorder(null);
		arr.setBackground(new Color(36, 8, 69));
		arr.add(im);
		arr.add(ct);
		add(arr, BorderLayout.SOUTH);

		barraM = new JMenuBar();
		setJMenuBar(barraM);
		opm1 = new JMenu("Archivo");
		opm2 = new JMenu("Ajustes");
		barraM.add(opm1);
		barraM.add(opm2);

		ruta = getClass().getResource("/AnalizadorL/Recursos/ArchivoA.png");
		op2 = new JMenuItem("Nuevo              ", new ImageIcon(ruta));
		op2.setMnemonic('R');
		op2.setToolTipText("Crea un archivo nuevo");
		op2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		opm1.add(op2);

		ruta = getClass().getResource("/AnalizadorL/Recursos/GuardarA.png");
		op3 = new JMenuItem("Guardar", new ImageIcon(ruta));
		op3.setMnemonic('R');
		op3.setToolTipText("Guarda el archivo");
		op3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));

		ruta = getClass().getResource("/AnalizadorL/Recursos/AbrirA.png");
		op4 = new JMenuItem("Abrir", new ImageIcon(ruta));
		op4.setMnemonic('R');
		op4.setToolTipText("Abre un archivo");
		op4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		opm1.add(op4);
		opm1.add(op3);

		ruta = getClass().getResource("/AnalizadorL/Recursos/CerrarA.png");
		op6 = new JMenuItem("Cerrar", new ImageIcon(ruta));
		op6.setMnemonic('R');
		op6.setToolTipText("Cierra el archivo");
		op6.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		opm1.add(op6);

		opm1.addSeparator();
		op5 = new JMenuItem("Salir");
		op5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
		opm1.add(op5);
		barraM.setBackground(Color.WHITE);

		opsub1 = new JMenu("Zoom              ");
		opm2.add(opsub1);
		ruta = getClass().getResource("/AnalizadorL/Recursos/acercarseG.png");
		op7 = new JMenuItem("Incrementar", new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/disminuirG.png");
		op18 = new JMenuItem("Disminuir", new ImageIcon(ruta));
		opsub1.add(op7);
		opsub1.add(op18);

		opsub2 = new JMenu("Fuente              ");
		opm2.add(opsub2);
		ruta = getClass().getResource("/AnalizadorL/Recursos/ArialG.png");
		op8 = new JMenuItem("Arial", new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/SansG.png");
		op9 = new JMenuItem("Sans Serif", new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/HelvG.png");
		op11 = new JMenuItem("Helvetica", new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/CalibriG.png");
		op12 = new JMenuItem("Calibri", new ImageIcon(ruta));
		opsub2.add(op8);
		opsub2.add(op9);
		opsub2.add(op11);
		opsub2.add(op12);

		opsub = new JMenu("Tema              ");
		opm2.add(opsub);
		ruta = getClass().getResource("/AnalizadorL/Recursos/BlancoG.png");
		op13 = new JMenuItem("Claro", new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/NegroG.png");
		op14 = new JMenuItem("Obscuro", new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/AzulCG.png");
		op15 = new JMenuItem("Azul claro", new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/AzulOG.png");
		op16 = new JMenuItem("Azul obscuro", new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/GrisG.png");
		op17 = new JMenuItem("Gris", new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/AzulG.png");
		op10 = new JMenuItem("Azul", new ImageIcon(ruta));
		opsub.add(op13);
		opsub.add(op17);
		opsub.add(op14);
		opsub.add(op15);
		opsub.add(op10);
		opsub.add(op16);

		JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ruta = getClass().getResource("/AnalizadorL/Recursos/TrianguloA.png");
		b1 = new JButton(new ImageIcon(ruta));
		b1.setToolTipText("Run");
		b1.setBackground(new Color(235, 235, 255));
		b1.setBorder(null);
		ruta = getClass().getResource("/AnalizadorL/Recursos/ArchivoA.png");
		b2 = new JButton(new ImageIcon(ruta));
		b2.setToolTipText("Crea un archivo nuevo");
		b2.setBackground(new Color(235, 235, 255));
		b2.setBorder(null);
		ruta = getClass().getResource("/AnalizadorL/Recursos/acercarseG.png");
		b3 = new JButton(new ImageIcon(ruta));
		b3.setToolTipText("Zoom +");
		b3.setBackground(new Color(235, 235, 255));
		b3.setBorder(null);
		ruta = getClass().getResource("/AnalizadorL/Recursos/disminuirG.png");
		b4 = new JButton(new ImageIcon(ruta));
		b4.setToolTipText("Zoom -");
		b4.setBackground(new Color(235, 235, 255));
		b4.setBorder(null);
		ruta = getClass().getResource("/AnalizadorL/Recursos/GuardarA.png");
		b5 = new JButton(new ImageIcon(ruta));
		b5.setToolTipText("Guarda el contenido del archivo de texto");
		b5.setBackground(new Color(235, 235, 255));
		b5.setBorder(null);
		ruta = getClass().getResource("/AnalizadorL/Recursos/AbrirA.png");
		b6 = new JButton(new ImageIcon(ruta));
		b6.setToolTipText("Abre un archivo");
		b6.setBackground(new Color(235, 235, 255));
		b6.setBorder(null);
		ruta = getClass().getResource("/AnalizadorL/Recursos/CerrarA.png");
		b7 = new JButton(new ImageIcon(ruta));
		b7.setToolTipText("Cerrar archivo");
		b7.setBackground(new Color(235, 235, 255));
		b7.setBorder(null);

		bot.add(b2);
		bot.add(b6);
		bot.add(b5);
		bot.add(b7);
		bot.add(b1);
		bot.add(b3);
		bot.add(b4);

		JComboBox<ImageIcon> comboBox = new JComboBox<ImageIcon>();
		ruta = getClass().getResource("/AnalizadorL/Recursos/ArialM.png");
		comboBox.addItem(new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/SansM.png");
		comboBox.addItem(new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/HelvM.png");
		comboBox.addItem(new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/CalibriM.png");
		comboBox.addItem(new ImageIcon(ruta));
		bot.add(comboBox);

		JComboBox<ImageIcon> comboBox2 = new JComboBox<ImageIcon>();
		ruta = getClass().getResource("/AnalizadorL/Recursos/TemaC.png");
		comboBox2.addItem(new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/TemaG.png");
		comboBox2.addItem(new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/TemaO.png");
		comboBox2.addItem(new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/TemaAc.png");
		comboBox2.addItem(new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/TemaA.png");
		comboBox2.addItem(new ImageIcon(ruta));
		ruta = getClass().getResource("/AnalizadorL/Recursos/TemaAo.png");
		comboBox2.addItem(new ImageIcon(ruta));
		bot.add(comboBox2);

		bot.setBackground(new Color(235, 235, 255));
		add(bot, BorderLayout.NORTH);

		pestañas = new JTabbedPane();

		JPanel p = new JPanel(new BorderLayout());
		JScrollPane scroll = new JScrollPane(p);

		num = new JTextArea();
		num.setBackground(Color.WHITE);
		num.setFont(new Font(letra, Font.BOLD, tam));
		num.setBackground(new Color(235, 235, 255));
		p.add(num, BorderLayout.WEST);
		artx = new JTextPane();
		artx.setBackground(new Color(220, 220, 255));
		artx.setFont(new Font(letra, Font.PLAIN, tam));
		p.add(artx);
		
		pestañas.addTab("Archivo.mal", scroll);
		ruta = getClass().getResource("/AnalizadorL/Recursos/iconArc.png");
		pestañas.setIconAt(0, new ImageIcon(ruta));
		pestañas.setBackgroundAt(0, Color.WHITE);

		this.sc = new StyleContext();
		this.doc = new DefaultStyledDocument(sc);

		getContentPane().add(pestañas);
		JPanel p2 = new JPanel(new BorderLayout());
		JScrollPane scroll2 = new JScrollPane(p2);
		consola = new JTextArea();
		consola.setBackground(Color.WHITE);
		consola.setFont(new Font(letra, Font.PLAIN, tam));
		p2.add(consola);
		pestañas.addTab("Consola", scroll2);
		ruta = getClass().getResource("/AnalizadorL/Recursos/Consola.png");
		pestañas.setIconAt(1, new ImageIcon(ruta));
		pestañas.setBackgroundAt(1, Color.WHITE);

		getContentPane().add(pestañas);
		titulos = new Vector<String>();
		titulos.add("Token");
		// titulos.add("Patron");
		titulos.add("Componente");
		datos = new Vector<Vector>();
		modelo = new DefaultTableModel(datos, titulos);
		tabla = new JTable(modelo);
		tabla.setFont(new Font(letra, Font.PLAIN, tam));
		tabla.setRowHeight(tam);
		tabla.getTableHeader().setFont(new Font(letra, Font.PLAIN, tam));
		JScrollPane scroll3 = new JScrollPane(tabla);
		pestañas.addTab("Componentes", scroll3);
		ruta = getClass().getResource("/AnalizadorL/Recursos/Tabla.png");
		pestañas.setIconAt(2, new ImageIcon(ruta));
		pestañas.setBackgroundAt(2, Color.WHITE);

		getContentPane().add(pestañas);
		titulos1 = new Vector<String>();
		titulos1.add("Pila");
		titulos1.add("Entrada");
		titulos1.add("Acción");
		titulos1.add("Pila semantica");
		datos1 = new Vector<Vector>();
		modelo1 = new DefaultTableModel(datos1, titulos1);
		tabla1 = new JTable(modelo1);
		tabla1.setFont(new Font(letra, Font.PLAIN, tam));
		tabla1.setRowHeight(tam);
		tabla1.getTableHeader().setFont(new Font(letra, Font.PLAIN, tam));
		JScrollPane scroll4 = new JScrollPane(tabla1);
		pestañas.addTab("Pila", scroll4);
		pestañas.setIconAt(3, new ImageIcon(ruta));
		pestañas.setBackgroundAt(3, Color.WHITE);

		getContentPane().add(pestañas);
		titulos2 = new Vector<String>();
		titulos2.add("Token");
		titulos2.add("Nombre");
		titulos2.add("Tipo");
		datos2 = new Vector<Vector>();
		modelo2 = new DefaultTableModel(datos2, titulos2);
		tabla2 = new JTable(modelo2);
		tabla2.setFont(new Font(letra, Font.PLAIN, tam));
		tabla2.setRowHeight(tam);
		tabla2.getTableHeader().setFont(new Font(letra, Font.PLAIN, tam));
		JScrollPane scroll5 = new JScrollPane(tabla2);
		pestañas.addTab("Simbolos", scroll5);
		//ruta = getClass().getResource("/AnalizadorL/Recursos/Tabla.png");
		pestañas.setIconAt(4, new ImageIcon(ruta));
		pestañas.setBackgroundAt(4, Color.WHITE);

		TS = new String[100][2];

		archivo = new JFileChooser();
		oba = new Archivo();

		col = true;
		num.setEditable(false);
		consola.setEditable(false);
		artx.setEnabled(false);
		b1.setEnabled(false);
		b5.setEnabled(false);
		op6.setEnabled(false);
		op3.setEnabled(false);
		b7.setEnabled(false);

		FileNameExtensionFilter filtro = new FileNameExtensionFilter("MAL Documentos", "mal");
		archivo.setFileFilter(filtro);

		op2.addActionListener(this);
		op3.addActionListener(this);
		op4.addActionListener(this);
		op6.addActionListener(this);
		op7.addActionListener(this);
		op8.addActionListener(this);
		op9.addActionListener(this);
		op10.addActionListener(this);
		op11.addActionListener(this);
		op12.addActionListener(this);
		op13.addActionListener(this);
		op14.addActionListener(this);
		op15.addActionListener(this);
		op16.addActionListener(this);
		op17.addActionListener(this);
		op18.addActionListener(this);
		op5.addActionListener(this);
		b1.addActionListener(this);
		b2.addActionListener(this);
		b5.addActionListener(this);
		b3.addActionListener(this);
		b4.addActionListener(this);
		b6.addActionListener(this);
		b7.addActionListener(this);
		artx.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE | e.getKeyCode() == KeyEvent.VK_ENTER
						| e.getKeyCode() == KeyEvent.VK_TAB) {
				}
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					pos--;
					antpos--;
					Contar();
				}
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					Contar();
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					pos--;
					antpos--;
					Contar();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				switch (comboBox.getSelectedIndex()) {
				case 0:
					letra = "Arial";
					Fuente(tam, letra);
					break;
				case 1:
					letra = "Sans Serif";
					Fuente(tam, letra);
					break;
				case 2:
					letra = "Helvética";
					Fuente(tam, letra);
					break;
				case 3:
					letra = "Calibri";
					Fuente(tam, letra);
					break;
				}
			}
		});
		comboBox2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				switch (comboBox2.getSelectedIndex()) {

				case 0:
					col = false;
					// num.setBackground(Color.WHITE);
					artx.setBackground(Color.WHITE);
					num.setForeground(Color.BLACK);
					artx.setForeground(Color.BLACK);

					consola.setBackground(Color.WHITE);
					consola.setForeground(Color.BLACK);

					tabla.setBackground(Color.WHITE);
					tabla.setForeground(Color.BLACK);

					tabla.getTableHeader().setForeground(Color.WHITE);
					tabla.getTableHeader().setBackground(Color.BLACK);

					tabla1.setBackground(Color.WHITE);
					tabla1.setForeground(Color.BLACK);

					tabla1.getTableHeader().setForeground(Color.WHITE);
					tabla1.getTableHeader().setBackground(Color.BLACK);

					tabla2.setBackground(Color.WHITE);
					tabla2.setForeground(Color.BLACK);

					tabla2.getTableHeader().setForeground(Color.WHITE);
					tabla2.getTableHeader().setBackground(Color.BLACK);

					break;
				case 2:
					col = false;
					num.setBackground(Color.DARK_GRAY);
					artx.setBackground(Color.BLACK);
					num.setForeground(Color.WHITE);
					artx.setForeground(Color.WHITE);

					consola.setBackground(Color.BLACK);
					consola.setForeground(Color.WHITE);

					tabla.setBackground(Color.BLACK);
					tabla.setForeground(Color.WHITE);
					tabla.getTableHeader().setForeground(Color.WHITE);
					tabla.getTableHeader().setBackground(Color.DARK_GRAY);

					tabla1.setBackground(Color.BLACK);
					tabla1.setForeground(Color.WHITE);
					tabla1.getTableHeader().setForeground(Color.WHITE);
					tabla1.getTableHeader().setBackground(Color.DARK_GRAY);
					
					tabla2.setBackground(Color.BLACK);
					tabla2.setForeground(Color.WHITE);
					tabla2.getTableHeader().setForeground(Color.WHITE);
					tabla2.getTableHeader().setBackground(Color.DARK_GRAY);
					break;
				case 3:
					col = false;
					num.setBackground(new Color(22, 8, 90));
					artx.setBackground(new Color(217, 239, 255));
					num.setForeground(Color.WHITE);
					artx.setForeground(Color.BLACK);

					consola.setBackground(new Color(217, 239, 255));
					consola.setForeground(Color.BLACK);

					tabla.setBackground(new Color(217, 239, 255));
					tabla.setForeground(Color.BLACK);
					tabla.getTableHeader().setForeground(new Color(217, 239, 255));
					tabla.getTableHeader().setBackground(Color.BLACK);

					tabla1.setBackground(new Color(217, 239, 255));
					tabla1.setForeground(Color.BLACK);
					tabla1.getTableHeader().setForeground(new Color(217, 239, 255));
					tabla1.getTableHeader().setBackground(Color.BLACK);
					
					tabla2.setBackground(new Color(217, 239, 255));
					tabla2.setForeground(Color.BLACK);
					tabla2.getTableHeader().setForeground(new Color(217, 239, 255));
					tabla2.getTableHeader().setBackground(Color.BLACK);
					break;
				case 5:
					col = false;
					num.setBackground(new Color(217, 239, 255));
					artx.setBackground(new Color(22, 8, 90));
					num.setForeground(Color.BLACK);
					artx.setForeground(Color.WHITE);

					consola.setBackground(new Color(22, 8, 90));
					consola.setForeground(Color.WHITE);

					tabla.setBackground(new Color(22, 8, 90));
					tabla.setForeground(Color.WHITE);
					tabla.getTableHeader().setForeground(new Color(22, 8, 90));
					tabla.getTableHeader().setBackground(Color.WHITE);

					tabla1.setBackground(new Color(22, 8, 90));
					tabla1.setForeground(Color.WHITE);
					tabla1.getTableHeader().setForeground(new Color(22, 8, 90));
					tabla1.getTableHeader().setBackground(Color.WHITE);
					
					tabla2.setBackground(new Color(22, 8, 90));
					tabla2.setForeground(Color.WHITE);
					tabla2.getTableHeader().setForeground(new Color(22, 8, 90));
					tabla2.getTableHeader().setBackground(Color.WHITE);
					
					break;
				case 1:
					col = false;
					num.setBackground(Color.GRAY);
					artx.setBackground(Color.LIGHT_GRAY);
					num.setForeground(Color.WHITE);
					artx.setForeground(Color.BLACK);

					consola.setBackground(Color.LIGHT_GRAY);
					consola.setForeground(Color.BLACK);

					tabla.setBackground(Color.LIGHT_GRAY);
					tabla.setForeground(Color.BLACK);
					tabla.getTableHeader().setForeground(Color.LIGHT_GRAY);
					tabla.getTableHeader().setBackground(Color.BLACK);

					tabla1.setBackground(Color.LIGHT_GRAY);
					tabla1.setForeground(Color.BLACK);
					tabla1.getTableHeader().setForeground(Color.LIGHT_GRAY);
					tabla1.getTableHeader().setBackground(Color.BLACK);
					
					tabla2.setBackground(Color.LIGHT_GRAY);
					tabla2.setForeground(Color.BLACK);
					tabla2.getTableHeader().setForeground(Color.LIGHT_GRAY);
					tabla2.getTableHeader().setBackground(Color.BLACK);
					
					break;
				case 4:
					col = false;
					num.setBackground(new Color(217, 239, 255));
					artx.setBackground(new Color(185, 216, 237));
					num.setForeground(new Color(6, 58, 95));
					artx.setForeground(new Color(32, 70, 96));

					consola.setBackground(new Color(185, 216, 237));
					consola.setForeground(new Color(32, 70, 96));

					tabla.setBackground(new Color(185, 216, 237));
					tabla.setForeground(new Color(32, 70, 96));
					tabla.getTableHeader().setForeground(new Color(185, 216, 237));
					tabla.getTableHeader().setBackground(new Color(32, 70, 96));

					tabla1.setBackground(new Color(185, 216, 237));
					tabla1.setForeground(new Color(32, 70, 96));
					tabla1.getTableHeader().setForeground(new Color(185, 216, 237));
					tabla1.getTableHeader().setBackground(new Color(32, 70, 96));
					
					tabla2.setBackground(new Color(185, 216, 237));
					tabla2.setForeground(new Color(32, 70, 96));
					tabla2.getTableHeader().setForeground(new Color(185, 216, 237));
					tabla2.getTableHeader().setBackground(new Color(32, 70, 96));
					
					break;
				}
			}
		});

		setVisible(true);
		addWindowListener(new WindowListener() {

			public void windowActivated(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowOpened(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {

				if (artx.isEnabled())
					Cerrar();

				e.getWindow().dispose();
			}

		});
	}

	public static void main(String[] args) {
		new InterfazL();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b1) {
			this.Guardar();
			pestañas.setSelectedIndex(1);
			reser = true;
			bano = true;
			band = true;
			bandera = true;
			b01 = true;
			b02 = true;
			b03 = true;
			bann = true;
			errorssin = "";
			nomidp = " ";
			nombre = "";
			nombre1 = "";
			chars = " ";
			ints = " ";
			floats = " ";
			contafor.clear();
			contaif.clear();
			conif = 0;
			confor = 0;
			codigo = "#include <stdio.h>\n\nvoid main()\n{\n";
			this.Analizar();
		}
		if (e.getSource() == op2 || e.getSource() == b2)
			this.CrearArc();
		if (e.getSource() == op3 || e.getSource() == b5)
			this.Guardar();
		if (e.getSource() == op4 || e.getSource() == b6)
			this.AbrirArc();
		if (e.getSource() == op5) {
			if (artx.isEnabled())
				this.Cerrar();
			System.exit(0);
		}
		if (e.getSource() == op6 || e.getSource() == b7) {
			this.Cerrar();
			num.setText("1 \n");
		}
		if (e.getSource() == op7 || e.getSource() == b3) {
			tam += 2;
			this.Fuente(tam, letra);
		}
		if (e.getSource() == op18 || e.getSource() == b4) {
			tam -= 2;
			this.Fuente(tam, letra);
		}

		if (e.getSource() == op8) {
			letra = "Arial";
			this.Fuente(tam, letra);
		}
		if (e.getSource() == op9) {
			letra = "Sans Serif";
			this.Fuente(tam, letra);
		}
		if (e.getSource() == op11) {
			letra = "Helvética";
			this.Fuente(tam, letra);
		}
		if (e.getSource() == op12) {
			letra = "Calibri";
			this.Fuente(tam, letra);
		}
		if (e.getSource() == op13) {
			col = false;
			// num.setBackground(Color.WHITE);
			artx.setBackground(Color.WHITE);
			num.setForeground(Color.BLACK);
			artx.setForeground(Color.BLACK);

			consola.setBackground(Color.WHITE);
			consola.setForeground(Color.BLACK);

			tabla.setBackground(Color.WHITE);
			tabla.setForeground(Color.BLACK);
			tabla.getTableHeader().setForeground(Color.WHITE);
			tabla.getTableHeader().setBackground(Color.BLACK);

			tabla1.setBackground(Color.WHITE);
			tabla1.setForeground(Color.BLACK);
			tabla1.getTableHeader().setForeground(Color.WHITE);
			tabla1.getTableHeader().setBackground(Color.BLACK);
			
			tabla2.setBackground(Color.WHITE);
			tabla2.setForeground(Color.BLACK);
			tabla2.getTableHeader().setForeground(Color.WHITE);
			tabla2.getTableHeader().setBackground(Color.BLACK);
		}
		if (e.getSource() == op14) {
			col = false;
			num.setBackground(Color.DARK_GRAY);
			artx.setBackground(Color.BLACK);
			num.setForeground(Color.WHITE);
			artx.setForeground(Color.WHITE);

			consola.setBackground(Color.BLACK);
			consola.setForeground(Color.WHITE);

			tabla.setBackground(Color.BLACK);
			tabla.setForeground(Color.WHITE);
			tabla.getTableHeader().setForeground(Color.WHITE);
			tabla.getTableHeader().setBackground(Color.DARK_GRAY);

			tabla1.setBackground(Color.BLACK);
			tabla1.setForeground(Color.WHITE);
			tabla1.getTableHeader().setForeground(Color.WHITE);
			tabla1.getTableHeader().setBackground(Color.DARK_GRAY);
			
			tabla2.setBackground(Color.BLACK);
			tabla2.setForeground(Color.WHITE);
			tabla2.getTableHeader().setForeground(Color.WHITE);
			tabla2.getTableHeader().setBackground(Color.DARK_GRAY);
		}
		if (e.getSource() == op15) {
			col = false;
			num.setBackground(new Color(22, 8, 90));
			artx.setBackground(new Color(217, 239, 255));
			num.setForeground(Color.WHITE);
			artx.setForeground(Color.BLACK);

			consola.setBackground(new Color(217, 239, 255));
			consola.setForeground(Color.BLACK);

			tabla.setBackground(new Color(217, 239, 255));
			tabla.setForeground(Color.BLACK);
			tabla.getTableHeader().setForeground(new Color(217, 239, 255));
			tabla.getTableHeader().setBackground(Color.BLACK);

			tabla1.setBackground(new Color(217, 239, 255));
			tabla1.setForeground(Color.BLACK);
			tabla1.getTableHeader().setForeground(new Color(217, 239, 255));
			tabla1.getTableHeader().setBackground(Color.BLACK);
			
			tabla2.setBackground(new Color(217, 239, 255));
			tabla2.setForeground(Color.BLACK);
			tabla2.getTableHeader().setForeground(new Color(217, 239, 255));
			tabla2.getTableHeader().setBackground(Color.BLACK);
		}
		if (e.getSource() == op16) {
			col = false;
			num.setBackground(new Color(217, 239, 255));
			artx.setBackground(new Color(22, 8, 90));
			num.setForeground(Color.BLACK);
			artx.setForeground(Color.WHITE);

			consola.setBackground(new Color(22, 8, 90));
			consola.setForeground(Color.WHITE);

			tabla.setBackground(new Color(22, 8, 90));
			tabla.setForeground(Color.WHITE);
			tabla.getTableHeader().setForeground(new Color(22, 8, 90));
			tabla.getTableHeader().setBackground(Color.WHITE);

			tabla1.setBackground(new Color(22, 8, 90));
			tabla1.setForeground(Color.WHITE);
			tabla1.getTableHeader().setForeground(new Color(22, 8, 90));
			tabla1.getTableHeader().setBackground(Color.WHITE);
		
			tabla2.setBackground(new Color(22, 8, 90));
			tabla2.setForeground(Color.WHITE);
			tabla2.getTableHeader().setForeground(new Color(22, 8, 90));
			tabla2.getTableHeader().setBackground(Color.WHITE);
		
		}
		if (e.getSource() == op17) {
			col = false;
			num.setBackground(Color.GRAY);
			artx.setBackground(Color.LIGHT_GRAY);
			num.setForeground(Color.WHITE);
			artx.setForeground(Color.BLACK);

			consola.setBackground(Color.LIGHT_GRAY);
			consola.setForeground(Color.BLACK);

			tabla.setBackground(Color.LIGHT_GRAY);
			tabla.setForeground(Color.BLACK);
			tabla.getTableHeader().setForeground(Color.LIGHT_GRAY);
			tabla.getTableHeader().setBackground(Color.BLACK);

			tabla1.setBackground(Color.LIGHT_GRAY);
			tabla1.setForeground(Color.BLACK);
			tabla1.getTableHeader().setForeground(Color.LIGHT_GRAY);
			tabla1.getTableHeader().setBackground(Color.BLACK);
			
			tabla2.setBackground(Color.LIGHT_GRAY);
			tabla2.setForeground(Color.BLACK);
			tabla2.getTableHeader().setForeground(Color.LIGHT_GRAY);
			tabla2.getTableHeader().setBackground(Color.BLACK);
		}
		if (e.getSource() == op10) {
			col = false;
			num.setBackground(new Color(217, 239, 255));
			artx.setBackground(new Color(185, 216, 237));
			num.setForeground(new Color(6, 58, 95));
			artx.setForeground(new Color(32, 70, 96));

			consola.setBackground(new Color(185, 216, 237));
			consola.setForeground(new Color(32, 70, 96));

			tabla.setBackground(new Color(185, 216, 237));
			tabla.setForeground(new Color(32, 70, 96));
			tabla.getTableHeader().setForeground(new Color(185, 216, 237));
			tabla.getTableHeader().setBackground(new Color(32, 70, 96));

			tabla1.setBackground(new Color(185, 216, 237));
			tabla1.setForeground(new Color(32, 70, 96));
			tabla1.getTableHeader().setForeground(new Color(185, 216, 237));
			tabla1.getTableHeader().setBackground(new Color(32, 70, 96));

			tabla2.setBackground(new Color(185, 216, 237));
			tabla2.setForeground(new Color(32, 70, 96));
			tabla2.getTableHeader().setForeground(new Color(185, 216, 237));
			tabla2.getTableHeader().setBackground(new Color(32, 70, 96));

		}
	}

	public void CrearArc() {
		archivo.setDialogTitle("Crear en: ");
		int returnVal = archivo.showSaveDialog(null);
		File ruta = null;
		archivo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			ruta = archivo.getSelectedFile();

		String exte = null;
		try {
			exte = ruta.getName();
			if (exte.lastIndexOf(".") != -1 && exte.lastIndexOf(".") != 0)
				exte.substring(exte.lastIndexOf(".") + 1);
			else
				exte = null;
		} catch (NullPointerException s) {
		}

		if (exte != null) {
			int res = JOptionPane.showConfirmDialog(null, "El archivo ya existe \n¿Desea reemplazarlo?", "",
					JOptionPane.YES_NO_OPTION);
			if (res == 0) {
				String noc = null;
				try {
					noc = ruta.toString();
					oba.Borrar(noc);
				} catch (NullPointerException g) {
				}
				boolean ban = oba.Crear(noc);
				if (ban) {
					ct.setText(noc);
					oba.Abrir(noc);
					oba.Mostrar();
					op6.setEnabled(true);
					op3.setEnabled(true);
					b7.setEnabled(true);
					b5.setEnabled(true);
					b1.setEnabled(true);
					artx.setEnabled(true);

					if (col) {
						// num.setBackground(Color.WHITE);
						artx.setBackground(Color.WHITE);
						num.setForeground(Color.BLACK);
						artx.setForeground(Color.BLACK);
					}

					JOptionPane.showMessageDialog(null, "Archivo reemplazado");
				} else
					;
				// JOptionPane.showMessageDialog(null, "Error: Ruta requerida", "Archivo no
				// creado",JOptionPane.ERROR_MESSAGE);

			}
		} else {
			String noc = null;
			try {
				noc = ruta.toString();
				noc += ".mal";
			} catch (NullPointerException g) {
			}
			boolean ban = oba.Crear(noc);
			if (ban) {
				ct.setText(noc);
				oba.Abrir(noc);
				oba.Mostrar();
				artx.setEnabled(true);
				op6.setEnabled(true);
				b7.setEnabled(true);
				op3.setEnabled(true);
				b5.setEnabled(true);
				b1.setEnabled(true);

				if (col) {
					// num.setBackground(Color.WHITE);
					artx.setBackground(Color.WHITE);
					num.setForeground(Color.BLACK);
					artx.setForeground(Color.BLACK);
				}

			} else
				;
			// JOptionPane.showMessageDialog(null, "Error: Ruta requerida", "Archivo no
			// creado",JOptionPane.ERROR_MESSAGE);
		}
	}

	public void AbrirArc() {
		artx.setText("");
		archivo.setDialogTitle("Escoja un archivo");
		// archivo.showOpenDialog(null);
		File ruta = null;
		int returnVal = archivo.showOpenDialog(null);
		archivo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			ruta = archivo.getSelectedFile();

		String noc = null;
		try {
			noc = ruta.toString();
		} catch (NullPointerException g) {
		}

		boolean ban = oba.Abrir(noc);
		if (ban) {
			ct.setText(noc);
			op6.setEnabled(false);
			b7.setEnabled(false);
			op3.setEnabled(false);
			b5.setEnabled(false);
			b1.setEnabled(true);
			this.Mostrar();
			artx.setEnabled(true);

			if (col) {
				// num.setBackground(Color.WHITE);
				artx.setBackground(Color.WHITE);
				num.setForeground(Color.BLACK);
				artx.setForeground(Color.BLACK);
			}
//				this.Palabra();				
		} else {
			artx.setText("");
			ct.setText("");
//				JOptionPane.showMessageDialog(null, "Error: No se puede crear el archivo \nUsted selecciono abrir", "Error: Accion invalida",JOptionPane.ERROR_MESSAGE);
		}
		this.Contar();
	}

	public void Cerrar() {
		if (op == 1) {
			oba.Cerrar();
			op2.setEnabled(true);
			b2.setEnabled(true);
			op4.setEnabled(true);
			b6.setEnabled(true);
			op6.setEnabled(false);
			b7.setEnabled(false);
			op3.setEnabled(false);
			b5.setEnabled(false);
			b1.setEnabled(false);
			ct.setText("");
			artx.setText("");
			artx.setEnabled(false);

			artx.setBackground(new Color(220, 220, 255));
		} else {
			Seg = new ArchS(InterfazL.this, true);
			da = Seg.mostrarCuadDialog();
			int opc = da.Opcion();
			if (opc == 1) {
				oba.Cerrar();
				op2.setEnabled(true);
				b2.setEnabled(true);
				op4.setEnabled(true);
				b6.setEnabled(true);
				op6.setEnabled(false);
				b7.setEnabled(false);
				op3.setEnabled(false);
				b1.setEnabled(false);
				b5.setEnabled(false);
				ct.setText("");
				artx.setText("");
				artx.setEnabled(false);

				artx.setBackground(new Color(220, 220, 255));
			}
			if (opc == 2) {
				String rug = ct.getText();
				String texg = artx.getText();
				oba.Grabar(rug, texg);
				oba.Cerrar();
				op2.setEnabled(true);
				b2.setEnabled(true);
				op4.setEnabled(true);
				b6.setEnabled(true);
				op6.setEnabled(false);
				b7.setEnabled(false);
				op3.setEnabled(false);
				b1.setEnabled(false);
				b5.setEnabled(false);
				ct.setText("");
				artx.setText("");
				artx.setEnabled(false);

				artx.setBackground(new Color(220, 220, 255));
			}
		}
	}

	public void Guardar() {
		String rug = ct.getText();
		String texg = artx.getText();
		oba.Grabar(rug, texg);
		op = 1;
	}

	public void Mostrar() {
		String cont = oba.Mostrar();
		op = -1;
		op6.setEnabled(true); // cerrar
		b7.setEnabled(true);
		op3.setEnabled(true);
		b5.setEnabled(true); // guardar
		b1.setEnabled(true);
		artx.setEnabled(true);
		artx.setText(cont);
	}

	public void Fuente(int tam, String letra) {
		num.setFont(new Font(letra, Font.BOLD, tam));
		artx.setFont(new Font(letra, Font.PLAIN, tam));
		consola.setFont(new Font(letra, Font.PLAIN, tam));
		tabla.setFont(new Font(letra, Font.PLAIN, tam));
		tabla.setRowHeight(tam);
		tabla.getTableHeader().setFont(new Font(letra, Font.PLAIN, tam));

		tabla1.setFont(new Font(letra, Font.PLAIN, tam));
		tabla1.setRowHeight(tam);
		tabla1.getTableHeader().setFont(new Font(letra, Font.PLAIN, tam));
	
		tabla2.setFont(new Font(letra, Font.PLAIN, tam));
		tabla2.setRowHeight(tam);
		tabla2.getTableHeader().setFont(new Font(letra, Font.PLAIN, tam));
	}
	
	@SuppressWarnings("unchecked")
	public void Analizar() {
		String elemento = null;
		poss = 1;
		this.Guardar();
		modelo.setRowCount(0);
		modelo1.setRowCount(0);
		modelo2.setRowCount(0);
		@SuppressWarnings("rawtypes")
		Vector dt;
		elemento = artx.getText();
		if(!elemento.isEmpty()) {
		    try {
		    	Reader lector = new BufferedReader(new FileReader(ct.getText()));
		        Lexer lexer = new Lexer(lector);
		        while (true) 
		        {
		        	if(band) 
		        	{
		        		Tokens tokens = lexer.yylex();
			            if (tokens == null) {
			                this.Sintactico("$");
			                return;
			            }
			            switch (tokens) {
			            	case ERROR:
			            		band = false;
			            		errorssin += "Error lexico en la linea "+ poss +" Simbolo no definido.\n";
								consola.setForeground(Color.RED);
								consola.setText(errorssin);
								this.LlenadoTabla("Error", "Error");
			                    break;
			            	case ERRMDig:
			            		band = false;
			            		errorssin += "Error lexico en la linea "+ poss +" caracter no valido.\n";
								consola.setForeground(Color.RED);
								consola.setText(errorssin);
								this.LlenadoTabla("Error", "Error");
			                    break;
			            	case ERRchar:
			            		band = false;
			            		errorssin +=  "Error lexico en la linea "+ poss +" falta cerrar comillas.\n";
								consola.setForeground(Color.RED);
								consola.setText(errorssin);
								this.LlenadoTabla("Error", "Error");
			                    break;
			                case coma: 
			                	dt = new Vector<Object>();
								dt.add(",");
								dt.add(",");
								modelo.addRow(dt);
								this.Sintactico(",");
			                    break;
			                case litcar:
			                	dt = new Vector<Object>();
								dt.add(lexer.lexeme);
								dt.add("literal char");
								modelo.addRow(dt);
								if(!bano)
									this.PilaSem("char");
								nombre = lexer.lexeme;
								this.Sintactico("litcar");
			                    break;
			                case id:
			                	dt = new Vector<Object>();
								dt.add(lexer.lexeme);
								idnueva = lexer.lexeme;
								if(reser)
								{
									if(this.BusquedaID(idnueva)) {
										errorssin += "Error Semantico en la linea "+ poss +" el identificador ' " + idnueva + " ' ha sido usado anteriormente.\n";
										consola.setForeground(Color.RED);
										consola.setText(errorssin);
										this.LlenadoTabla(idnueva, "Error");
										band = false;
										reser = false;
									}
									else {
										nomidp += idnueva + " ";
										if(tipos.equals("int"))
											ints += idnueva + " ";
										else
											if(tipos.equals("float"))
												floats += idnueva + " ";
											else 
												chars += idnueva + " ";
										codigo += tipos + " "+ idnueva + ";\n";
										this.TablaComponentes("id", idnueva, tipos);
									}
								}
								if(!bano)
								{
									if(this.BusquedaID(lexer.lexeme))
									{
										this.PilaSem(this.Verificador(lexer.lexeme));
										if(bann)
										{
											for(int i = 1; i <= 10; i++)
												codigo += "float" + " V" + i + ";\n";
											bann = false;
										}
									}
									else
									{
										errorssin += "Error Semantico en la linea "+ poss +" el identificador ' " + idnueva + " ' no existe.\n";
										consola.setForeground(Color.RED);
										consola.setText(errorssin);
										this.LlenadoTabla(idnueva, "Error");
										band = false;
										reser = false;
									}
								}
								dt.add("id");
								modelo.addRow(dt);
								nombre = lexer.lexeme;
								if(!banid) 
									this.GeneraCodigo(lexer.lexeme);
								this.Sintactico("id");
			                    break;
			                case Palabra_Reservada:
			                	dt = new Vector<Object>();
								dt.add(lexer.lexeme);
								dt.add(lexer.lexeme);
								modelo.addRow(dt);
								if(lexer.lexeme.equals("if"))
								{
									banid = false;
									condicion.push("if");
									conif++;
									contaif.push(conif+"");
								}
								if(lexer.lexeme.equals("for"))
								{
									banfor = false;
									condicion.push("for");
									confor++;
									contafor.push(confor+"");
								}
								if(lexer.lexeme.equals("else"))
								{
									conel++;
									condicion.push("else");
									codigo += "Else"+contaif.peek()+": \n";
									banel=true;
//									conif++;
								}
								this.Sintactico(lexer.lexeme);
			                	break;
			                case Tipo_Dato:
			                	dt = new Vector<Object>();
								dt.add(lexer.lexeme);
								dt.add(lexer.lexeme);
								tipos = lexer.lexeme;
								modelo.addRow(dt);
								reser = true;
								bano = true;
								this.Sintactico(lexer.lexeme);
			                	break;
			                case nume:
			                	dt = new Vector<Object>();
								dt.add(lexer.lexeme);
								dt.add("numero entero");
								modelo.addRow(dt);
								if(!bano)
									this.PilaSem("int");
								if(!banfor) 
									this.GeneraCodigo(lexer.lexeme);
								nombre = lexer.lexeme;
								this.Sintactico("num");
			                	break;
			                case numf:
			                	dt = new Vector<Object>();
								dt.add(lexer.lexeme);
								dt.add("numero flotante");
								modelo.addRow(dt);
								if(!bano)
									this.PilaSem("float");
								nombre = lexer.lexeme;
								this.Sintactico("numf");
			                	break;
			                case Salto_de_Linea:
			                	poss++;
			                	break;
			                default:
							dt = new Vector<Object>();
							String sim = tokens.toString();
							
							if (sim.equals("meniq")) {
								sim = "<=";
								sig = sim;
							}
							else if (sim.equals("mayiq")) {
								sim = ">=";
								sig = sim;
							}
							else if (sim.equals("dif")) {
								sim = "!=";
								sig = sim;
							}
							else if (sim.equals("igual")) {
								sim = "==";
								sig = sim;
							}
							else if (sim.equals("men")) {
								sim = "<";
								sig = sim;
							}
							else if (sim.equals("may")) {
								sim = ">";
								sig = sim;
							}
							else if (sim.equals("asignacion"))
							{
								nombre1 = nombre;
								sim = "=";
							}
							else if (sim.equals("inc"))
								sim = "++";
							else if (sim.equals("dec"))
								sim = "--";
							else if (sim.equals("suma"))
								sim = "+";
							else if (sim.equals("resta"))
								sim = "-";
							else if (sim.equals("parena"))
								sim = "(";
							else if (sim.equals("parenc"))
								sim = ")";
							else if (sim.equals("llavea"))
								sim = "{";
							else if (sim.equals("llavec")) 
							{
								sim = "}";
								
							}
							else if (sim.equals("finSentencia")) {
								sim = ";";
								reser = false;
								bano = false;
							} else if (sim.equals("mult"))
								sim = "*";
							else if (sim.equals("div"))
								sim = "/";
							dt.add(sim);
							dt.add(sim);
							modelo.addRow(dt);
							this.Sintactico(sim);
			                break;
			            }
		        	}
		        	else
		        		return;
		        }
		    } 
		    catch (FileNotFoundException ex) {}
		    catch (IOException ex) {}
		}
		else
			 this.Sintactico("$");
	}
	
	public void PilaSem(String tipo)
	{
		if(band)
			sema.push(tipo);
	}
	
	public void Sintactico(String token)
	{
		String acc;
		if(band)
			if(bandera) {
				errorssin = "";
				sema.clear();
				pila.clear();
				pila.push("$");
				pila.push("I0");
				bandera = false;
				this.Sintactico(token);
			}
			else
			{
				int possi = -1;
				this.PosY(token);
				this.PosX();
				if (trans[x][y].equals("saltar "))
				{
					consola.setForeground(Color.RED);
					Errores(token, poss, parentesis);
					this.LlenadoTabla(token, "Error");
					band = false;
				}
				else
					if (trans[x][y].equals("Aceptar "))
					{
						consola.setForeground(new Color(41, 111, 1 ));
						consola.setText("Cadena Aceptada...");
						this.LlenadoTabla(token, "Cadena Aceptada");
						codigo+="printf(\" \");\n}";
						String rug = "";
						for(int i = 0; i <= ct.getText().length(); i++)
							if(ct.getText().charAt(i) != '.')
								rug += ct.getText().charAt(i);
							else
							{
								rug += ".txt";
								break;
							}
						oba.Grabar(rug, codigo);
					}
					else
					{
						for(int i = 0; i < prod.length; i++)
							if(trans[x][y].equals(prod[i])) 
							{
								possi = i;
								break;
							}
						if(possi != -1)
						{
							if(possi == 11) 
							{
								this.Iguasem();
								this.GeneraIgualacion(nombre1);
							}
							if(possi == 14)
							{
								this.Sumasem();
								this.GeneraOperaciones("+");
							}
							if(possi == 15)
							{
								this.Restsem();
								this.GeneraOperaciones("-");
							}
							if(possi == 22)
							{
								this.Multsem();
								this.GeneraOperaciones("*");
							}
							if(possi == 23)
							{
								this.Divisem();
								this.GeneraOperaciones("/");
							}
							
							if(possi == 26)
							{
								if(condicion.peek().equals("if"))
								{
									this.Condisem();
									this.Generaif(sig, "Else"+contaif.peek());
									
								}
								else
								{
									//Se realiza la condicional del medio for
									this.Condifor();
									conif++;
									contaif.push("F"+contafor.peek());
									this.Generaif2(sig, "For"+contafor.peek());
									contaif.pop();
								}
								banid = true;
							}
							
							//Primera condicional del for
							if(possi == 38)
							{
								banfor = true;
								this.Condifor();
								this.GeneraIgualacion(nombre1);
								codigo += "For"+contafor.peek()+":\n";
								banid = false;
							}
							
							//Ultima condicional del for
							if(possi == 39 || possi == 41)
							{
								// ++id, id++
								incre = "+";
								tok = nombre1;
								this.Adicion();
							}
							
							if(possi == 40 || possi == 42)
							{
								//--id, id--
								incre = "-";
								tok = nombre1;
								this.Adicion();
							}
							
							if(possi == 17 || possi == 19 || possi == 20 || possi == 21)
							{
								this.GeneraCodigo(nombre);
							}
						
							String cosa = (prod[possi] + comp[possi]);
							this.LlenadoTabla(token, cosa);
							int cant = pops[possi], cont = 0;
							if(cant != 0)
								do {
									pila.pop();
									cont++;
								}
								while(cont < cant);
							//Se mete la transicion de Produccion (P)
							this.PosY(term[possi]);
							this.PosX();
							pila.push(term[possi]);
							pila.push(trans[x][y]);
							this.Sintactico(token);
						}
						else
						{
							//Accion cambio de estado Q	Dezplaza
							acc = "Desp " + token + " a " + trans[x][y];
							this.LlenadoTabla(token, acc);
							pila.push(token);
							pila.push(trans[x][y]);
							
							if(token.equals("}"))
							{
								if(condicion.peek().equals("for"))
								{
									this.GeneraCodigo(tok);
									this.GeneraCodigo("1");
									this.GeneraOperaciones(incre);
									this.GeneraIgualacion(tok);
									codigo += "goto End_For"+contafor.peek()+";\nEnd_For"+contafor.peek()+":\n";
									//contafor.pop();									
								}
								else
									if(condicion.peek().equals("if"))
									{
										codigo += "goto End_If"+contaif.peek()+";\n";
//										if(!banel)
//											contaif.pop();	
									}
									else
										if(condicion.peek().equals("else"))
										{
											
											codigo += "goto End_If"+contaif.peek()+";\n";
											codigo += "End_If"+contaif.peek()+":\n";
											banel=false;
											contaif.pop();	
										}
								condicion.pop();
							}
						}
					}		
			}
	}
	
	private void GeneraIgualacion(String ope)
	{
		if(!b10)
		{
			codigo += ope + " = V10;\n";
			b10 = true;
		}
		else
			if(!b09)
			{
				codigo += ope + " = V9;\n";
				b09 = true;
			}
			else
				if(!b08)
				{
					codigo += ope + " = V8;\n";
					b08 = true;
				}
				else
					if(!b07)
					{
						codigo += ope + " = V7;\n";
						b07 = true;
					}
					else
						if(!b06)
						{
							codigo += ope + " = V6;\n";
							b06 = true;
						}
						else
							if(!b05)
							{
								codigo += ope + " = V5;\n";
								b05 = true;
							}
							else
								if(!b04)
								{
									codigo += ope + " = V4;\n";
									b04 = true;
								}
								else
									if(!b03)
									{
										codigo += ope + " = V3;\n";
										b03 = true;
									}
									else
										if(!b02)
										{
											codigo += ope + " = V2;\n";
											b02 = true;
										}
										else
										{
											codigo += ope + " = V1;\n";
											b01 = true;
										}
	}
	
	public void Errores(String token, int i, boolean paren)
	{
		String resultado = "";
		
		if (pila.peek().equals("I0 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', 'un tipo de dato', '}', 'for' o 'if'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I1 ")) {
			resultado += "Error sintactico en la linea " + i +": Error en '"+token+"' se esperaba 'fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I2 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I3 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I4 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I5 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I6 ")) {
			resultado += "Error sintactico en la linea " + i +": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I7 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for' o 'if'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I8 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for' o 'if'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I9 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for' o 'if'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I10 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '='.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I11 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '('.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I12 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '('.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I13 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ',' o ';'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I14 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '}' o 'fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I15 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '}' o 'fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I16 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '}' o 'fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I17 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'num', 'caracter', 'flotante', 'id', '(', o 'fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I18 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I19 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id' o 'int'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I20 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'Fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I21 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I22 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', 'un tipo de dato', '}', 'for' o 'if'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I23 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I24 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I25 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '/', '*', ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I26 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '/', '*', ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I27 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'num', 'caracter', 'flotante', 'id' o '('.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I28 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '/', '*', ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I29 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '/', '*', ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I30 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '/', '*', ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I31 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I32 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '<', '>', '<=', '>=', '==' o '!='.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I33 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I34 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I35 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I36 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '='.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I37 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';' o ','.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I38 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'Fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I39 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for', 'if' o 'Fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I40 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I41 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '(', 'num', 'flotante' o 'carcater'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I42 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '(', 'num', 'flotante' o 'carcater'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I43 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I44 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '(', 'num', 'flotante' o 'carcater'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I45 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '(', 'num', 'flotante' o 'carcater'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I46 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I47 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '{'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I48 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I49 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I50 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I51 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I52 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I53 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I54 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I55 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I56 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I57 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'num'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I58 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'Fin de sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I59 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I60 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I61 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '/', '*', ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I62 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '/', '*', ';', '+', '-' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I63 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '/', '*', ';', '+', '-', ')' o 'Fin de Sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I64 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for', 'if' o 'Fin de Sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I65 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I66 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I67 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I68 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I69 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';' o ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I70 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';', ')', '+' o '-'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I71 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ';', ')', '+' o '-'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I72 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '}'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I73 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '++' o '--'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I74 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', 'for', 'if', 'else', '}' o 'Fin de Sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I75 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I76 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '++' o '--'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I77 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I78 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I79 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for', 'if' o 'Fin de Sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I80 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '{'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I81 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '{'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I82 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I83 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I84 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I85 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba ')'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I86 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for', 'if' o 'Fin de Sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I87 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for', 'if' o 'Fin de Sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I88 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '}'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I89 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba '}'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I90 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for', 'if' o 'Fin de Sentencia'.\n";
			consola.setText(resultado);
		} else if (pila.peek().equals("I91 ")) {
			resultado += "Error sintactico en la linea " + i + ": Error en '"+token+"' se esperaba 'id', '}', 'for', 'if' o 'Fin de Sentencia'.\n";
			consola.setText(resultado);
		}
	}
	
	private void Generaif(String ope, String msj) 
	{
		if(!b10)
		{
			codigo += "V9 = V9 "+ ope + " V10;\n";
			codigo += "IF"+conif+":\nif (!V9)\n\tgoto "+msj+";\n";
			b10 = true;
		}
		else
			if(!b09)
			{
				codigo += "V8 = V8 "+ ope + " V9;\n";
				codigo += "IF"+conif+":\nif (!V8)\n\tgoto "+msj+";\n";
				b09 = true;
			}
			else
				if(!b08)
				{
					codigo += "V7 = V7 "+ ope + " V8;\n";
					codigo += "IF"+conif+":\nif (!V7)\n\tgoto "+msj+";\n";
					b08 = true;
				}
				else
					if(!b07)
					{
						codigo += "V6 = V6 "+ ope + " V7;\n";
						codigo += "IF"+conif+":\nif (!V6)\n\tgoto "+msj+";\n";
						b07 = true;
					}
					else
						if(!b06)
						{
							codigo += "V5 = V5 "+ ope + " V6;\n";
							codigo += "IF"+conif+":\nif (!V5)\n\tgoto "+msj+";\n";
							b06 = true;
						}
						else
							if(!b05)
							{
								codigo += "V4 = V4 "+ ope + " V5;\n";
								codigo += "IF"+conif+":\nif (!V4)\n\tgoto "+msj+";\n";
								b05 = true;
							}
							else
								if(!b04)
								{
									codigo += "V3 = V3 "+ ope + " V4;\n";
									codigo += "IF"+conif+":\nif (!V3)\n\tgoto "+msj+";\n";
									b04 = true;
								}
								else
									if(!b03)
									{
										codigo += "V2 = V2 "+ ope + " V3;\n";
										codigo += "IF"+conif+":\nif (!V2)\n\tgoto "+msj+";\n";
										b03 = true;
									}
									else
										if(!b02)
										{
											codigo += "V1 = V1 "+ ope + " V2;\n";
											codigo += "IF"+conif+":\nif (!V1)\n\tgoto "+msj+";\n";
											b02 = true;
										}
	}

	private void Generaif2(String ope, String msj) 
	{
		if(!b10)
		{
			codigo += "V9 = V9 "+ ope + " V10;\n";
			codigo += "if (!V9)\n\tgoto "+msj+";\n";
			b10 = true;
		}
		else
			if(!b09)
			{
				codigo += "V8 = V8 "+ ope + " V9;\n";
				codigo += "if (!V8)\n\tgoto "+msj+";\n";
				b09 = true;
			}
			else
				if(!b08)
				{
					codigo += "V7 = V7 "+ ope + " V8;\n";
					codigo += "if (!V7)\n\tgoto "+msj+";\n";
					b08 = true;
				}
				else
					if(!b07)
					{
						codigo += "V6 = V6 "+ ope + " V7;\n";
						codigo += "if (!V6)\n\tgoto "+msj+";\n";
						b07 = true;
					}
					else
						if(!b06)
						{
							codigo += "V5 = V5 "+ ope + " V6;\n";
							codigo += "if (!V5)\n\tgoto "+msj+";\n";
							b06 = true;
						}
						else
							if(!b05)
							{
								codigo += "V4 = V4 "+ ope + " V5;\n";
								codigo += "if (!V4)\n\tgoto "+msj+";\n";
								b05 = true;
							}
							else
								if(!b04)
								{
									codigo += "V3 = V3 "+ ope + " V4;\n";
									codigo += "if (!V3)\n\tgoto "+msj+";\n";
									b04 = true;
								}
								else
									if(!b03)
									{
										codigo += "V2 = V2 "+ ope + " V3;\n";
										codigo += "if (!V2)\n\tgoto "+msj+";\n";
										b03 = true;
									}
									else
										if(!b02)
										{
											codigo += "V1 = V1 "+ ope + " V2;\n";
											codigo += "if (!V1)\n\tgoto "+msj+";\n";
											b02 = true;
										}
	}
	private void GeneraOperaciones(String ope)
	{
		if(!b10)
		{
			codigo += "V9 = V9 "+ ope + " V10;\n";
			b10 = true;
		}
		else
			if(!b09)
			{
				codigo += "V8 = V8 "+ ope + " V9;\n";
				b09 = true;
			}
			else
				if(!b08)
				{
					codigo += "V7 = V7 "+ ope + " V8;\n";
					b08 = true;
				}
				else
					if(!b07)
					{
						codigo += "V6 = V6 "+ ope + " V7;\n";
						b07 = true;
					}
					else
						if(!b06)
						{
							codigo += "V5 = V5 "+ ope + " V6;\n";
							b06 = true;
						}
						else
							if(!b05)
							{
								codigo += "V4 = V4 "+ ope + " V5;\n";
								b05 = true;
							}
							else
								if(!b04)
								{
									codigo += "V3 = V3 "+ ope + " V4;\n";
									b04 = true;
								}
								else
									if(!b03)
									{
										codigo += "V2 = V2 "+ ope + " V3;\n";
										b03 = true;
									}
									else
										if(!b02)
										{
											codigo += "V1 = V1 "+ ope + " V2;\n";
											b02 = true;
										}
										else
										{
											codigo += ope + " = V1;\n";
											b01 = true;
										}
	}
	
	private void GeneraCodigo(String nombre)
	{
		if(b01)
		{
			codigo += "V1 = " + nombre + ";\n";
			b01 = false;
		}
		else
			if(b02)
			{
				codigo += "V2 = " + nombre + ";\n";
				b02 = false;
			}
			else
				if(b03)
				{
					codigo += "V3 = " + nombre + ";\n";
					b03 = false;
				}
				else
					if(b04)
					{
						codigo += "V4 = " + nombre + ";\n";
						b04 = false;
					}
					else
						if(b05)
						{
							codigo += "V5 = " + nombre + ";\n";
							b05 = false;
						}
						else
							if(b06)
							{
								codigo += "V6 = " + nombre + ";\n";
								b06 = false;
							}
							else
								if(b07)
								{
									codigo += "V7 = " + nombre + ";\n";
									b07 = false;
								}
								else
									if(b08)
									{
										codigo += "V8 = " + nombre + ";\n";
										b08 = false;
									}
									else
										if(b09)
										{
											codigo += "V9 = " + nombre + ";\n";
											b09 = false;
										}
										else
										{
											codigo += "V10 = " + nombre + ";\n";
											b10 = false;
										}
						
	}
	
	private void Sumasem()
	{
		int x, y;
		x = this.Bussem();
		y = this.Bussem();
		sema.push(this.Tipo(suma[x][y]));
	}
	
	private void Restsem()
	{
		int x, y;
		x = this.Bussem();
		y = this.Bussem();
		sema.push(this.Tipo(rest[x][y]));
	}
	
	private void Multsem()
	{
		int x, y;
		x = this.Bussem();
		y = this.Bussem();
		sema.push(this.Tipo(mult[x][y]));
	}
	
	private void Divisem()
	{
		int x, y;
		x = this.Bussem();
		y = this.Bussem();
		sema.push(this.Tipo(divi[x][y]));
	}
	
	private void Condisem()
	{
		int x, y;
		x = this.Bussem();
		y = this.Bussem();
		if(!condi[y][x])
		{
			errorssin += "Error Semantico en la linea "+ poss +" comparacion invalida.\n";
			consola.setForeground(Color.RED);
			consola.setText(errorssin);
			this.LlenadoTabla("Error", "Error");
			band = false;
		}
	}
	
	private void Condifor()
	{
		String x, y;
		x = sema.pop();
		y = sema.pop();
		if(!x.equals(y))
		{
			errorssin += "Error Semantico en la linea "+ poss +" tipo de dato invalido.\n";
			consola.setForeground(Color.RED);
			consola.setText(errorssin);
			this.LlenadoTabla("Error", "Error");
			band = false;
		}
	}
	
	private void Adicion()
	{
		String x;
		x = sema.pop();
		if(!x.equals("int"))
		{
			errorssin += "Error Semantico en la linea "+ poss +" tipo de dato invalido.\n";
			consola.setForeground(Color.RED);
			consola.setText(errorssin);
			this.LlenadoTabla("Error", "Error");
			band = false;
		}
	}
	
	private void Iguasem()
	{
		int x, y;
		x = this.Bussem();
		y = this.Bussem();
		if(!igua[y][x])
		{
			errorssin += "Error Semantico en la linea "+ poss +" operacion invalida.\n";
			consola.setForeground(Color.RED);
			consola.setText(errorssin);
			this.LlenadoTabla("Error", "Error");
			band = false;
		}
	}
	
	private int Bussem()
	{
		String cim = sema.pop();
		if(cim.equals("int"))
			return 0;
		else
			if(cim.equals("float"))
				return 1;
			else
				return 2;
	}
	
	private String Tipo(int tipo)
	{
		if(tipo == 0)
			return "int";
		else
			if(tipo == 1)
				return "float";
			else
				if(tipo == 2)
					return "char";
				else
				{
					errorssin += "Error Semantico en la linea "+ poss +" operacion invalida.\n";
					consola.setForeground(Color.RED);
					consola.setText(errorssin);
					this.LlenadoTabla("Error", "Error");
					band = false;
					return "";
				}
	}

	private int PosX() {
		int posi = 0;
		for (int i = 0; i < filas.length; i++)
			if (filas[i].equals(pila.peek())) {
				posi = -1;
				x = i;
				break;
		}
		if(posi != 0)
			return x;
		else
			return x = 0;
	}

	private int PosY(String token) {
		for (int i = 0; i < columnas.length; i++)
			if (token.equals(columnas[i])) {
				y = i;
				break;
			}
		return y;
	}
	
	private boolean BusquedaID(String iden)	{
		String pal = "";
		for (int j = 0; j < nomidp.length(); j++)
			if (nomidp.charAt(j) != ' ')
				pal += nomidp.charAt(j) + "";
			else
				if(pal.equals(iden))
					return true;
				else
					pal = "";
		return false;
	}
	
	private String Verificador(String iden) {
		String pal = "";
		for (int j = 0; j < ints.length(); j++)
			if (ints.charAt(j) != ' ')
				pal += ints.charAt(j) + "";
			else
				if(pal.equals(iden))
					return "int";
				else
					pal = "";
		pal = "";
		for (int j = 0; j < chars.length(); j++)
			if (chars.charAt(j) != ' ')
				pal += chars.charAt(j) + "";
			else
				if(pal.equals(iden))
					return "char";
				else
					pal = "";
		pal = "";
		for (int j = 0; j < floats.length(); j++)
			if (floats.charAt(j) != ' ')
				pal += floats.charAt(j) + "";
			else
				if(pal.equals(iden))
					return "float";
				else
					pal = "";
		return "";
	}
	
	@SuppressWarnings("unchecked")
	private void LlenadoTabla(String token, String accion) {
		@SuppressWarnings("rawtypes")
		Vector dtsin = new Vector<Object>();
		String pal = "", cont = "", sem, contt = "";
		java.util.Stack<String> pilaaux = new java.util.Stack<String>(), pilaau = new java.util.Stack<String>();
		
		while (!pila.empty()) {
			pal = pila.pop();
			pilaaux.push(pal);
		}
		while (!pilaaux.empty()) {
			pal = pilaaux.pop();
			cont += pal + " ";
			pila.push(pal);
		}
		
		while (!sema.empty()) {
			sem = sema.pop();
			pilaau.push(sem);
		}
		while (!pilaau.empty()) {
			sem = pilaau.pop();
			contt += sem + " ";
			sema.push(sem);
		}
		//Contenido de la pila
		dtsin.add(cont);
		//Token de entrada
		dtsin.add(token);
		//La accion a tomar "Pila auxiliar"
		dtsin.add(accion);
		if(accion.equals("Error"))
			dtsin.add("Error");
		else
			dtsin.add(contt);
		modelo1.addRow(dtsin);
	}
	
	@SuppressWarnings("unchecked")
	private void TablaComponentes(String componente, String nombre, String tipo) {
		@SuppressWarnings("rawtypes")
		Vector dtsin = new Vector<Object>();
		dtsin.add(componente);
		dtsin.add(nombre);
		dtsin.add(tipo);
		modelo2.addRow(dtsin);
	}
	
	@SuppressWarnings("unchecked")
	public void TSimbolos(String tok, String tipo) {
		@SuppressWarnings("rawtypes")
		Vector v = new Vector<Object>();
		v.add(tipo);
		v.add(tok);
		modelo2.addRow(v);
	}
	
	private void Contar() {
		tl = 1;
		num.setText(tl + "\n");
		for (int i = 0; i < artx.getText().length(); i++) {
			if (artx.getText().charAt(i) == '\n') {
				tl++;
				num.setText(num.getText() + tl + " \n");
			}
		}
	}	
}