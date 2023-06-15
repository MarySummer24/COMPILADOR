package AnalizadorL;

import java.io.File;

public class ALexico
{
	public static void main(String[] args)
	{
		//Cambiar ruta para cada compu
		String ruta="D:/users/alelo/eclipse-workspace/Analizador4/src/AnalizadorL/Lexer.flex";
		//URL ruta = getClass().getResource("/AnalizadorL/Lexer.flex");
		generateLexer(ruta);
	}

	public static void generateLexer(String ruta) {
		File archivo = new File(ruta);
		JFlex.Main.generate(archivo);
	}
}
