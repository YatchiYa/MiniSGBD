package app;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import sgbd.DBManager;
// Main

public class Main {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		
		DBManager dbManager = new DBManager();
		dbManager.init();

		Scanner sc = new Scanner(System.in);
		String commande;
		String exit = "exit";
		
		

		System.out.println("feel welcome to mini_SGBD");
		System.out.println(" tappez votre commande ");
		
		
		do {
			commande = sc.nextLine();
			// int nbc = sc.nextInt();
			// String typeC = sc.nextLine();
			if(commande.equals(exit)) {
				dbManager.finish();
			}
			else {
				
				dbManager.processCommande(commande);
			}
			
		}while(!commande.equals(exit));		
		
		System.out.println("sortie");
		
	}

}
