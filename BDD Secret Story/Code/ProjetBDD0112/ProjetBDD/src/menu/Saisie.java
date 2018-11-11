package menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class Saisie {
	//M�thodes
	public static String lireChaine(String message){
	 	String ligne = null;
	 	try{
	 		//conversion d'un flux binaire en un flux de caractères (caractères Unicode)
	 		InputStreamReader isr = new InputStreamReader(System.in);
	 		//construction d'un tampon pour optimiser la lecture du flux de caractères
	 		BufferedReader br = new BufferedReader(isr);
	 		System.out.print(message);
	 		// lecture d'une ligne
	 		ligne = br.readLine();
	 	}
	 	catch (IOException e){
	 		System.err.println(e.getMessage());
	 	}
	 	return ligne;
	 }// fin lireChaine
	 public static int lireEntier(String message){
	 	return Integer.parseInt(lireChaine(message));
	 }
	 public static double lireReel(String message){
	 	return Double.parseDouble(lireChaine(message));
	 }

}