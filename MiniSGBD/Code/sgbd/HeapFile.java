package sgbd;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import Schema.PageId;
import Schema.RelSchemaDef;
import Schema.bytemap;
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
		// on construit le buffer 
		byte[ ] bufferHeaderPage = BufferManager.getPage(hp);
		// on initialise le headerPage 
		HeaderPageInfo headerPageInfo = new HeaderPageInfo(0);
		
		// lecture du headerPage
		ByteBuffer buffer = ByteBuffer.wrap(bufferHeaderPage);
				
			// get nb de page
			int nbPage = buffer.getInt();
			headerPageInfo.setDataPageCount(nbPage);
			
			// read indiceX/ nb_slot dispo -> 8bits
			for(int i = 0; i<nbPage; i++) {
				Integer indiceX = new Integer(buffer.getInt());
				Integer nb_slot = new Integer(buffer.getInt());
				
				headerPageInfo.addPageIdx(indiceX);
				headerPageInfo.addSlot(nb_slot);
			}
		
			Integer iSearch = new Integer(iPageId.getPageIdx());
			boolean find = headerPageInfo.decrDataPageCount(iSearch);
			if(!find) {
				System.out.println("*** Erreur ! Cette page n'est pas présente ! ***\n");
				BufferManager.freePageId(hp, 0);
			}
			else {				
				buffer.putInt(headerPageInfo.getDataPageCount());
				
				ArrayList<Integer> indice_tab = headerPageInfo.getPageIdx();
				ArrayList<Integer> nb_slot = headerPageInfo.getFreeSlot();
				
				for(int i = 0; i<indice_tab.size(); i++) {
					buffer.putInt(indice_tab.get(i).intValue());
					buffer.putInt(nb_slot.get(i).intValue());
				}

				BufferManager.freePageId(hp, 1);
			}
		
	}

	
	public void writeRecordInBuffer(Record iRecord, byte[] ioBuffer, int  iSlotIdx) {
		//set the relation
		RelSchemaDef schemaRelation = relation.getRelDef();
		// get the different attributes
		ArrayList<String> type_checkCol = schemaRelation.getType_col();
		ArrayList<String> values = iRecord.getListValues();
		// set a buffer
		ByteBuffer buffer = ByteBuffer.wrap(ioBuffer);
		// get the position
		buffer.position(iSlotIdx);
		
		for(int i = 0; i<type_checkCol.size(); i++) {
			String type_check = type_checkCol.get(i);
			String val_check = values.get(i);
			
			int val_Int;
			float val_Float;
			String val_String;
			
			switch(type_check.toLowerCase()) {
				case "int" : 
					val_Int = Integer.parseInt(val_check);
					buffer.putInt(val_Int);
					break;
				case "float" : 
					val_Float = Float.parseFloat(val_check);
					buffer.putFloat(val_Float);
					break;
				default : 
					int longueurString = Integer.parseInt(type_check.substring(6));
					val_String = val_check;
					for(int j = 0; j<longueurString; j++) {
						buffer.putChar(val_String.charAt(j));
					}
			}
		}
		
		
		
	}
	
	public void insertRecordInPage(Record iRecord, PageId iPageId) throws IOException {
		byte[] b_page = BufferManager.getPage(iPageId);
		bytemap bytMap = new bytemap();
		
		int nbSlot = relation.getSlotCount();
		// reading the bytmap page
		ByteBuffer buffer = ByteBuffer.wrap(b_page);
		
		//lecture de la bitmap (nbSlot bytes)
		for(int i = 0; i<nbSlot; i++) {
			Byte indice = new Byte(buffer.get());
			bytMap.addSlotIndice(indice);
		}
		// verifiction que l'element est dans la liste
		ArrayList<Byte> slot_indice = bytMap.getSlotIndice();
		int checkIdx = slot_indice.indexOf(new Byte((byte)0));
		if(checkIdx == -1) {
			BufferManager.freePageId(iPageId, 0);
		}
		else {
			int slot_c = relation.getSlotCount();
			int record_s = relation.getRecordSize();
			
			writeRecordInBuffer(iRecord, b_page, slot_c + checkIdx*record_s);
								
			ArrayList<Byte> map = bytMap.getSlotIndice();			
			
			for(int i = 0; i<nbSlot; i++){
				buffer.put(map.get(i).byteValue());
			}
			BufferManager.freePageId(iPageId, 1);
		}
		
	}
	
	
	
	public void insertRecord(Record r) {
		
		
	}

	
	
}
