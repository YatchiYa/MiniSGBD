package sgbd;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
public class DiskManager {

	
	// joseph fix !!!!
	
	
	
	/**
	 * Cr�ation d'une instance unique pour DiskManager
	 */
	private static final DiskManager INSTANCE = new DiskManager();

	public static DiskManager getInstance(){
	      return INSTANCE;
	}
	public static final String PATH ="."+File.separatorChar+"DB"+File.separatorChar+"Data_";
	public static final long SIZEPAGE =4096;
	/**
	 * m�thode cr�e un fichier Data_iFileIdx.rf initialement vide 
	 * @param iFileIdx
	 * @throws IOException
	 */
	public static void  CreateFile(int iFileIdx)throws IOException{
		RandomAccessFile rwF = new RandomAccessFile(PATH+iFileIdx+".rf","rw");	
		rwF.close();
	}
	/**
	 *  Cette m�thode rajoute une page au fichier sp�cifi� par iFileIdx et remplit l�argument oPageId
	 * @param iFileIdx
	 * @param oPageId
	 * @return 
	 * @throws IOException
	 */
	public static PageId AddPage(int iFileIdx) throws IOException{
		RandomAccessFile rwF = new RandomAccessFile(PATH+iFileIdx+".rf","rw");
		long longueur =rwF.length();
		int indice =(int)(longueur/SIZEPAGE);
		for(int i=0;i<SIZEPAGE; i++) {
			rwF.writeByte(0);
		}
		rwF.close();		
		return new PageId(iFileIdx,indice);
	}

	/**
	 *  remplit l�argument oBuffer par le contenu (disque) de la page identifi�e par le iPageId
	 * @param iPageId
	 * @param oBuffer
	 * @throws IOException
	 */
	public static void ReadPage(PageId iPageId, byte[]oBuffer)throws IOException{
		RandomAccessFile rwF = new RandomAccessFile(PATH+iPageId.getFileIdx()+".rf", "rw"); 
		rwF.seek(iPageId.getPageIdx()*SIZEPAGE);
		rwF.write(oBuffer);
		rwF.close();
	}


	/**
	 * �crit le contenu de l�argument iBuffer dans le fichier et � la position indiqu�s par iPageId
	 * @param iPageId
	 * @param iBuffer
	 * @throws IOException
	 */
	public static void WritePage(PageId iPageId,byte[] iBuffer)throws IOException {
		RandomAccessFile rwF = new RandomAccessFile(PATH+iPageId.getFileIdx()+".rf", "rw"); 
		rwF.seek(iPageId.getPageIdx()*SIZEPAGE);
		rwF.readFully(iBuffer);
		rwF.close();
	}
}
