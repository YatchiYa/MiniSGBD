package sgbd;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Cette classe représente le gestionnaire de disque.<br>
 * Il permet de créer un fichier, d'ajouter une page à un fichier,<br>
 * de lire un page en remplissant un buffer, d'écrire sur une page le contenu d'un buffer.
 *
 */
public class DiskManager {
	
	private static final String LIEN = "."+File.separatorChar+"DB"+File.separatorChar+"Data_";
	private static final long K = 4096;
	
	/**
	 * Créé un fichier Data_FileId.rf en vue d’y stocker des records par la suite.
	 * @param fileId
	 * @throws IOException
	 */
	public static void createFile(int fileId) throws IOException {
		RandomAccessFile rAf = new RandomAccessFile(LIEN+fileId+".rf", "rw");	
		rAf.close();
	}
	
	/**
	 * Rajoute une page au fichier spécifié par le champ FileIdx et qui retourne un PageId 
	 * correspondant à l’id de la page nouvellement rajoutée
	 * @param fileId
	 * @return PageId correspondant à l’id de la page nouvellement rajoutée
	 * @throws IOException
	 */
	public static PageId addPage(int fileId) throws IOException {
		RandomAccessFile rAf = new RandomAccessFile(LIEN+fileId+".rf", "rw");
		long longueur = rAf.length();
		int id = (int)(longueur/K);
		rAf.seek((long)longueur);
		for (int i=0; i<K; i++){
			rAf.writeByte(0);
		}
		rAf.close();
		return new PageId(fileId,id);
	}
	
	/**
	 * Remplit l’argument buffer par le contenu (disque) de la page identifiée par le PageId
	 * @param page
	 * @param buffer
	 * @throws IOException
	 */
	public static void readPage(PageId page, byte[] buffer) throws IOException{
		RandomAccessFile rAf = new RandomAccessFile(LIEN+page.getFileId()+".rf", "rw");
		rAf.seek(page.getIdX()*K);
		rAf.readFully(buffer);
		rAf.close();
	}
	
	/**
	 * Ecrit le contenu de l’argument buffer dans le fichier et à l’offset indiqués par le PageId.
	 * @param page
	 * @param buffer
	 * @throws IOException
	 */
	public static void writePage(PageId page, byte[] buffer) throws IOException {
		RandomAccessFile rAf = new RandomAccessFile(LIEN+page.getFileId()+".rf", "rw");
		rAf.seek(page.getIdX()*K);
		rAf.write(buffer);
		rAf.close();
	}
}