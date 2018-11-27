package sgbd;
import java.io.IOException;
import java.util.ArrayList;

import Schema.PageId;
import rel.Record;
import rel.RelDef;

/**
 * a revoir tous le fichier !! !! ! ! ! ! ! !
 * 
 * 
 * a checker et a pense :: 
 * 
 * faire la recuperation du header buffer manager dans une classe tous seul 
 * ou même toute les clase en relation avec les heap file dans une classe ( le traitement ) 
 *
 */

public class HeapFile {
	private RelDef relation;


	public HeapFile(RelDef relation) {
		this.relation = relation;
	}
	
	public RelDef getRelation() {
		return relation;
	}

	public void setRelation(RelDef relation) {
		this.relation = relation;
	}

	
	
	/**
	 * 
	 * @throws IOException
	 */
	public void createNewOnDisk () throws IOException {
		DiskManager.CreateFile(relation.getFileIdx());	

		// ajouter de la page
		DiskManager.AddPage(relation.getFileIdx());    // revenir revoir plus tard 
		
		//recuperer le buffer de la Header Page vis le BufferManger
		// new pageId
		PageId hp= new PageId(relation.getFileIdx(),0);		
		byte[] bufferHeaderPage = BufferManager.getPage(hp);
		
		HeaderPageInfo headerPageInfo = new HeaderPageInfo(0);
		
		headerPageInfo.writeToBuffer(bufferHeaderPage, headerPageInfo);  // revoir 
		
		//liberer le buffer aupres de BufferManager
		BufferManager.freePageId(hp, 1);   // 1 => modifier      0 => pas modif 
		
	}
	
	
	
	public void getFreePageId(PageId oPageId) throws IOException {

		int FileId = relation.getFileIdx();
		
		// recuperer le header Buffer		
		PageId hp= new PageId(relation.getFileIdx(),0);		
		byte[] bufferHeaderPage = BufferManager.getPage(hp);
		
		//cree une new instance of headerPageInfo
		HeaderPageInfo headerPageInfo = new HeaderPageInfo(); 
		// remplissage du headerPageInfo
		
		// recuperer l'indice et les slot disponible afin de cree notre objet
		ArrayList<Integer> indiceX = headerPageInfo.getPageIdx();
		ArrayList<Integer> slotLibre = headerPageInfo.getFreeSlot();
		
		for(int i = 0;i<indiceX.size();i++) {
			int indice = indiceX.get(i).intValue();
			int nbSlot = slotLibre.get(i).intValue();
			
			
			if(nbSlot > 0) {
				// PageId newPage = DiskManager.addPage(relation.getFileId());
				PageId newPage=  new PageId(FileId, indice); 	
				
				oPageId.setFileIdx(newPage.getFileIdx());
				
				System.out.println("pas de modif");
				BufferManager.freePageId(hp, 0);
								
			}else {
				DiskManager.AddPage(oPageId.getPageIdx());  //pkoi ? je sais pas 
				
				
				headerPageInfo.addSlot(relation.getSlotCount()); // recuperer le nombre de slot 
				headerPageInfo.addPageIdx(oPageId.getPageIdx()); // recuperer le id de l'ajout
				headerPageInfo.incrDataPageCount(); // on incremente le nombre de page dispo

				headerPageInfo.writeToBuffer(bufferHeaderPage, headerPageInfo);
				
				System.out.println(" modif");
				BufferManager.freePageId(hp, 1);
			}
		}
		
	}
	 
	/**
	 * 
	 * @param iPageId
	 * @throws IOException
	 */
	public void updateHeaderWithTakenSlot(PageId iPageId) throws IOException{
		
		//on recupére l'indice du fichier HeapFIle
		int FileIdxHP = relation.getFileIdx();
		//on crée une instance de headerPage
		PageId hp=  new PageId(FileIdxHP,0);
		byte[ ] bufferHeaderPage = BufferManager.getPage(hp);
		HeaderPageInfo headerPageInfo = new HeaderPageInfo(0);
	}

	public void insertRecord(Record r) {
		// TODO Auto-generated method stub
		
	}
	
	
}
