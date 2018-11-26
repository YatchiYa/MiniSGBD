package sgbd;
import java.io.IOException;

import rel.Record;
import rel.RelDef;

public class HeapFile {
	private RelDef relation;

	public RelDef getRelation() {
		return relation;
	}

	public void setRelation(RelDef relation) {
		this.relation = relation;
	}

	public HeapFile(RelDef relation) {
		this.relation = relation;
		}
	
	/**
	 * La HeaderPage aura donc la structure suivante :<br>
	 * - NbPagesDeDonnées : int<br>
	 * - NbPageDeDonnées entrées de la forme : (IdxPage : int ; NbSlotsRestantDisponibles: int)
	 * @throws IOException
	 */
	public void createNewOnDisk () throws IOException {
		DiskManager.CreateFile(relation.getFileIdx());	
		//on crée une instance de la Header Page
		PageId hp= new PageId(relation.getFileIdx(),0);
		// ajout de la HeaderPage je suis pas sur !!!!
		DiskManager.AddPage(relation.getFileIdx(), 0);    // revenir revoir plus tard 
		
		
		byte[] bufferHeaderPage = BufferManager.getPage(hp);
		// construire une HeaderPageInfo toute fraîche (avec donc le dataPageCount=0)
		HeaderPageInfo Hpi = new HeaderPageInfo(0);
		//ecrire la HeaderPageInfo dans le buffer 
		Hpi.writeToBuffer(bufferHeaderPage, Hpi);  // revoir 
		BufferManager.freePageId(hp, 1); // libreer le buffer en signalant u'il a été modifié 
		
	}
	
	
	public void getFreePageId(PageId oPageId) throws IOException {
		byte[ ] bufferHeaderPage = BufferManager.getPage(oPageId);
	}
	 
	/**
	* Cette méthode devra actualiser les informations dans la Header
	*  Page suite à l’occupation d’une des cases disponible sur une page
	*/
	public void updateHeaderWithTakenSlot(PageId iPageId) throws IOException{
		//on recupére l'indice du fichier HeapFIle
		int FileIdxHP = relation.getFileIdx();
		//on crée une instance de headerPage
		PageId hp=  new PageId(FileIdxHP,0);
		byte[ ] bufferHeaderPage = BufferManager.getPage(hp);
		HeaderPageInfo hpi = new HeaderPageInfo(0);
	}

	public void insertRecord(Record r) {
		// TODO Auto-generated method stub
		
	}
	
	
}
