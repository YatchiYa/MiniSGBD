package sgbd;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
public class DiskManager {

	
	// joseph fix !!!!
	
	
	
	/**
	 * Création d'une instance unique pour DiskManager
	 */
	private static final DiskManager INSTANCE = new DiskManager();

	public static DiskManager getInstance(){
	      return INSTANCE;
	}
	public static final String PATH = constants.Constants.PATH;
	public static final long SIZEPAGE = constants.Constants.pageSize;
	/**
	 * méthode crée un fichier Data_iFileIdx.rf initialement vide 
	 * @param iFileIdx
	 * @throws IOException
	 */
	public static void  CreateFile(int iFileIdx)throws IOException{
		try {
			RandomAccessFile randomfile = new RandomAccessFile(PATH+iFileIdx+".rf","rw");	
			randomfile.close();	
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	/**
	 *  Cette méthode rajoute une page au fichier spécifié par iFileIdx et remplit l’argument oPageId
	 * @param iFileIdx
	 * @param oPageId
	 * @return 
	 * @throws IOException
	 */
	public static PageId AddPage(int iFileIdx) throws IOException{
		RandomAccessFile randomfile = new RandomAccessFile(PATH+iFileIdx+".rf","rw");
		long longueur =randomfile.length();
		int indice =(int)(longueur/SIZEPAGE);
		for(int i=0;i<SIZEPAGE; i++) {
			randomfile.writeByte(0);
		}
		randomfile.close();		
		
		System.out.println(" \n commentaire : "+ iFileIdx + " " + indice);
		return new PageId(iFileIdx,indice);
	}

	/**
	 *  remplit l’argument oBuffer par le contenu (disque) de la page identifiée par le iPageId
	 * @param iPageId
	 * @param oBuffer
	 * @throws IOException
	 */
	public static void ReadPage(PageId iPageId, byte[]oBuffer)throws IOException{
		RandomAccessFile randomfile = new RandomAccessFile(PATH+iPageId.getFileIdx()+".rf", "rw"); 
		randomfile.seek(iPageId.getPageIdx()*SIZEPAGE);
		randomfile.write(oBuffer);
		
		System.out.println("commentaire : "+ oBuffer);
		randomfile.close();
	}


	/**
	 * écrit le contenu de l’argument iBuffer dans le fichier et à la position indiqués par iPageId
	 * @param iPageId
	 * @param iBuffer
	 * @throws IOException
	 */
	public static void WritePage(PageId iPageId,byte[] iBuffer)throws IOException {
		RandomAccessFile randomfile = new RandomAccessFile(PATH + iPageId.getFileIdx()+".rf", "rw"); 
		randomfile.seek(iPageId.getPageIdx()*SIZEPAGE);
		randomfile.readFully(iBuffer);
		randomfile.close();
	}
}
