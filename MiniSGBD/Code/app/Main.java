package app;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import Schema.PageId;
import sgbd.DBManager;
import sgbd.DiskManager;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		
		DBManager dbManager = new DBManager();
		dbManager.init();

		Scanner sc = new Scanner(System.in);
		String commande;
		String exit = "exit";
		
		DiskManager d = new DiskManager();
		
		/** byte[] t = new byte[100];
		
		PageId i = new PageId(5,5);
		d.CreateFile(5);
		d.AddPage(5);
		d.ReadPage(i, t);
		d.WritePage(i, t);
		
		*/
		
		
		System.out.println("Feel welcome to mini_SGBD");
		System.out.println(" tappez votre commande ");
		System.out.println("(\\_/)");
		System.out.println("( •,•)");
		System.out.println("(\")_(\")");
		
		
		
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
