package application;

import java.io.FileNotFoundException;
import java.io.IOException;

import menu.Utilities;
import sgbd.GlobalManager;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException{
		GlobalManager.init();
		String commande;
		System.out.println("Bienvenue sur Mini-SGBD (version 1.0)");
		System.out.println("Tapez 'help', pour de l'aide.");
		do{
			commande = Utilities.affichageMenu();
		}while(!commande.equals("exit"));
	}
}
