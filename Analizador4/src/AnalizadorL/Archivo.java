package AnalizadorL;

import java.io.*;

public class Archivo
{
	private FileInputStream arc = null;
	private String cont = "";
	private byte dat[] = new byte[30];
	private int led = 0;
	
	public boolean Crear(String nom)
	{
		FileOutputStream esc;
		try
		{
			esc = new FileOutputStream(nom);
			esc.close();
		}
		catch (NullPointerException e)
		{
			return false;
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}
	
	public boolean Abrir(String ruta)
	{	
		cont = "";
		try
		{
			arc = new FileInputStream(ruta);
			do
			{
				led = arc.read(dat);
				if(led != -1)
					cont += new String(dat, 0, led);
			}
			while(led != -1);
			arc.close();
		}
		catch (NullPointerException e)
		{
			return false;
		}
		catch(IOException e)
		{
			return false;
		}
		return true;
	}
	
	public void Cerrar()
	{
		arc = null;
		cont = "";
	}
	
	public void Grabar(String nom, String con)
	{
		FileOutputStream arc = null;
		try
		{
			arc = new FileOutputStream(nom);
			arc.write(con.getBytes());
			arc.close();
		}
		catch(IOException e)
		{}
	}
	
	public String Mostrar()
	{
		return cont;
	}
	
	public void Borrar(String ruta)
	{
		FileOutputStream arc = null;
		String con = "";
		try
		{
			arc = new FileOutputStream(ruta);
			arc.write(con.getBytes());
			arc.close();
		}
		catch(IOException e)
		{}
	}

}