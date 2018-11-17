package app;
import java.util.Scanner;

import sgbd.DBManager;


public class Main {

	public static void main(String[] args) {
		
		DBManager dbManager = new DBManager();
		dbManager.init();

		Scanner sc = new Scanner(System.in);
		String commande;
		String exit = "exit";
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
