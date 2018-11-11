package sgbd;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import relation.Record;
import relation.RelDef;
import relation.RelSchema;

public class HeapFile {
	
	private RelDef relation;
	
	public HeapFile(RelDef relation) {
		this.relation = relation;
	}
	
	/**
	 * La HeaderPage aura donc la structure suivante :<br>
	 * - NbPagesDeDonnées : int<br>
	 * - NbPageDeDonnées entrées de la forme : (IdxPage : int ; NbSlotsRestantDisponibles: int)
	 * @throws IOException
	 */
	public void createHeader() throws IOException {
		DiskManager.addPage(relation.getFileId());
	}
	
	/**
	 * Cette méthode lit le contenu du page directory qui se trouve dans le buffer et "copier"
	 * ce contenu dans les membres de la HeaderPageInfo hfi.<br>
	 * Ce sera pour "désérialiser la header page".
	 * @param headerPage header page sous la forme d'un buffer
	 * @param hfi headerpageinfo à remplir
	 * @throws IOException
	 */
	public void readHeaderPageInfo(byte[] headerPage, HeaderPageInfo hfi) throws IOException {
		//"désérialisation" : flux octet(headerPage) buffer -> un object de type HeaderPageInfo		
		ByteBuffer buffer = ByteBuffer.wrap(headerPage);
		
		//on récupère le nombre de page (4 bytes)
		int nbPage = buffer.getInt();
		hfi.setNbPagesDeDonnees(nbPage);
		
		//lecture successive du couple idx/nbslotrestant (8 bytes = 4 bytes + 4 bytes)
		for(int i = 0; i<nbPage; i++) {
			Integer idx = new Integer(buffer.getInt());
			Integer nbSlot = new Integer(buffer.getInt());
			
			hfi.addIdxPage(idx);
			hfi.addNbSlotDispo(nbSlot);
		}
	}
	
	/**
	 * Cette méthode écrit le contenu de la HeaderPageInfo dans le buffer
	 * Ce sera pour "sérialiser la header page".
	 * @param headerPage
	 * @param hfi
	 * @throws IOException
	 */
	public void writeHeaderPageInfo(byte[] headerPage, HeaderPageInfo hfi) throws IOException {
		//"sérialisation" : flux octet(headerPage) buffer <- un objet du type headerpageinfo
		ByteBuffer buffer = ByteBuffer.wrap(headerPage);
	
		//ecriture du nombre page (4 bytes)
		buffer.putInt(hfi.getNbPagesDeDonnees());
		
		ArrayList<Integer> idxTab = hfi.getIdxPageTab();
		ArrayList<Integer> nbSlotTab = hfi.getNbSlotsRestantDisponibles();
		
		//ecriture successive du couple idx/nbslotrestant (8 bytes = 4 bytes + 4 bytes)
		for(int i = 0; i<idxTab.size(); i++) {
			buffer.putInt(idxTab.get(i).intValue());
			buffer.putInt(nbSlotTab.get(i).intValue());
		}
	}
	
	/**
	 * Cette méthode remplit le contenu de hpi avec les informations de page directory dans la header page du heapfile courant.
	 * Pour ce faire elle récupére, via le Buffer Manager, le buffer de la Header Page (rappel : l’indice
	 * de cette page est 0, donc elle construit le PageId pour faire le bon appel au BufferManager) ;<br>
	 * puis appeler readHeaderPageInfo ;<br> 
	 * puis libérer auprès du BufferManager le buffer de la Header Page (avec un flag dirty à 0 car on l’a juste lue).<br>
	 * Attention : l’instance de HeaderPageInfo à remplir doit être créée en dehors de cette méthode.
	 * @param HeaderPageInfo hpi
	 * @throws IOException
	 */
	public void getHeaderPageInfo(HeaderPageInfo hpi) throws IOException {
		//on recupere la header page du heapfile
		int fileIdHP = relation.getFileId();
		//on construit son PageId pour faire le bon appel au BufferManager
		PageId headerPage = new PageId(fileIdHP, 0);
		
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);
		
		//on remplit hpi avec le contenu du buffer de la header page
		readHeaderPageInfo(bufferHeaderPage, hpi);
		
		BufferManager.freePage(headerPage, 0);
	}
	
	/**
	 * Cette méthode actualise les informations de page directory dans la header page du heapfile courant
	 * suite à l'ajout d'une nouvelle page de données newpid.
	 * @param newpid
	 * @throws IOException
	 */
	public void updateHeaderNewDataPage(PageId newpid) throws IOException {
		//on recupere la header page du heapfile
		int fileIdHP = relation.getFileId();
		//on construit son PageId pour faire le bon appel au BufferManager
		PageId headerPage = new PageId(fileIdHP, 0);
		
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		
		readHeaderPageInfo(bufferHeaderPage, hpi);
		//on rajoute dans la liste de la headerpageinfo l'indice de la nouvelle page
		Integer idx = new Integer(newpid.getIdX());
		hpi.addIdxPage(idx);
		//le nombre de cases dispos est égal au slotCount
		hpi.addNbSlotDispo(relation.getSlotCount());
		//on incremente le nb de page total
		hpi.incrementNbPage();
		
		writeHeaderPageInfo(bufferHeaderPage, hpi);
		//on libere la header page qui vient d'etre modifiée
		BufferManager.freePage(headerPage, 1);
	}
	
	/**
	 * Cette méthode actualise les infos du page directory suite à l'occupation d'une des cases disponible sur une page
	 * @param pid
	 * @throws IOException
	 */
	public void updateHeaderTakenSlot(PageId pid) throws IOException {
		//on recupere la header page du heapfile
		int fileIdHP = relation.getFileId();
		//on construit son PageId pour faire le bon appel au BufferManager
		PageId headerPage = new PageId(fileIdHP, 0);
				
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);
		
		HeaderPageInfo hpi = new HeaderPageInfo();
				
		readHeaderPageInfo(bufferHeaderPage, hpi);
		
		//chercher l'id de la PageId dans la liste idxPageTab pour obtenir l'indice
		//et le faire correspondre dans la liste nbSlotsRestantDisponibles
		
		Integer idChercher = new Integer(pid.getIdX());
		boolean find = hpi.decrementNbSlotDispo(idChercher);
		if(!find) {
			System.out.println("*** Erreur ! Cette page n'est pas présente ! ***\n");
			BufferManager.freePage(headerPage, 0);
		}
		else {
			writeHeaderPageInfo(bufferHeaderPage, hpi);
			//on libere la header page qui vient d'etre modifiée
			BufferManager.freePage(headerPage, 1);
		}
	}
	
	/**
	 * Cette méthode remplit la liste dans la pageBitmapinfo bmpi avec les informations dans la bitmap du buffer bufferPage
	 * Ce sera pour "désérialiser la bitmap".
	 * @param bufferPage
	 * @param bmpi
	 * @throws IOException
	 */
	public void readPageBitmapInfo(byte[] bufferPage, PageBitmapInfo bmpi) throws IOException {
		//"désérialisation" : flux octet(bufferPage) buffer -> un objet du type PageBitmapInfo
		//Dans chaque page de données "en forme buffer" il y aura donc :
		//AU DEBUT, la bitmap avec un octet par case
		
		//nbSlot = nbSlot octets = nbSlot bytes
		//car on a la bitmap avec un octet par case
		int nbSlot = relation.getSlotCount();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);
		
		//lecture de la bitmap (nbSlot bytes)
		for(int i = 0; i<nbSlot; i++) {
			Byte status = new Byte(buffer.get());
			bmpi.addSlotStatus(status);
		}
	}
	
	/**
	 * Cette méthode écrit au début du buffer bufferPage la bitmap (état des cases) donnée par la liste d'octets de la pageBitmapInfo bmpi
	 * Ce sera pour "sérialiser la bitmap".
	 * @param bufferPage
	 * @param bmpi
	 * @throws IOException
	 */
	public void writePageBitmapInfo(byte[] bufferPage, PageBitmapInfo bmpi) throws IOException {
		//"sérialisation" : flux octet(bufferPage) buffer <- un objet du type PageBitmapInfo
		
		//nbSlot = nbSlot octets = nbSlot bytes
		//car on a la bitmap avec un octet par case
		int nbSlot = relation.getSlotCount();
		
		ArrayList<Byte> bitMap = bmpi.getSlotStatus();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);
		
		//modification du buffer de la header page passée en parametre
		for(int i = 0; i<nbSlot; i++){
			buffer.put(bitMap.get(i).byteValue());
		}
	}
	
	/**
	 * Cette méthode écrit un à un les champs du record r à l'offset indiqué par l'argument offset
	 * @param r
	 * @param bufferPage
	 * @param offset
	 */
	public void writeRecordInBuffer(Record r, byte[] bufferPage,int offset) {
		//on recupere la relschema de la reldef du heapfile courant
		RelSchema schema = relation.getrS();
		
		//la liste des types des différentes colonnes de la relation
		ArrayList<String> typeCol = schema.getType_col();
		//la liste des valeurs du record
		ArrayList<String> listVal = r.getListValues();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);
		
		//on se positionne dans le buffer à l'offset indiqué pour pouvoir écrire le record
		buffer.position(offset);
		
		//lors de l'écriture du record, on convertit les valeurs des champs int ou float, qui sont
		//au format String dans le Record. Pour les types, on se base sur les types de la relschema
		//Une fois la conversion faite, il faudra écrire "les octets de la valeur"
		for(int i = 0; i<typeCol.size(); i++) {
			String type = typeCol.get(i);
			String val = listVal.get(i);
			
			int valToInt;
			float valtoFloat;
			String valToString;
			
			switch(type.toLowerCase()) {
				case "int" : 
					valToInt = Integer.parseInt(val);
					buffer.putInt(valToInt);
					break;
				case "float" : 
					valtoFloat = Float.parseFloat(val);
					buffer.putFloat(valtoFloat);
					break;
				default : 
					int longueurString = Integer.parseInt(type.substring(6));
					valToString = val;
					for(int j = 0; j<longueurString; j++) {
						buffer.putChar(valToString.charAt(j));
					}
			}
		}
	}
	
	/**
	 * En utilisant les éléments précédemment codés, cette méthode devra lire, un à un, les champs<br>
	 * "sérialisés" du record situé à l'offset indiqué par l'argument, et remplir votre instance de<br>
	 * Record r passée en argument avec ces valeurs
	 * @param r
	 * @param bufferPage
	 * @param offset
	 */
	public void readRecordFromBuffer(Record r, byte[] bufferPage,int offset) {
		RelSchema schema = relation.getrS();
		
		ArrayList<String> typeCol = schema.getType_col();
		ArrayList<String> listVal = new ArrayList<String>();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);
		
		buffer.position(offset);
		
		for(int i = 0; i<typeCol.size(); i++) {
			String type = typeCol.get(i);
			
			int valInt;
			float valFloat;
			StringBuffer valString = new StringBuffer();
			
			switch(type.toLowerCase()) {
				case "int" : 
					valInt = buffer.getInt();
					listVal.add(String.valueOf(valInt));
					break;
				case "float" : 
					valFloat = buffer.getFloat();
					listVal.add(String.valueOf(valFloat));
					break;
				default : 
					int longueurString = Integer.parseInt(type.substring(6));
					//lorsque la longueur de la string est < longueurString il y a des caracteres
					//qui sont vides
					for(int j = 0; j<longueurString; j++) {
						valString.append(buffer.getChar());
					}
					listVal.add(valString.toString());
			}
		}
		r.setValue(listVal);
	}
	
	public PageId addDataPage() throws IOException {
		//on rajoute une data page au heapfile
		PageId newPage = DiskManager.addPage(relation.getFileId());
		
		updateHeaderNewDataPage(newPage);
		
		return newPage;
	}
	
	/**
	 * - créer une instance de de HeaderPageInfo et appeler getHeaderPageInfo pour remplir cette instance<br>
	 * - regarder le contenu de la HeaderPageInfo : si on trouve une page qui a tjrs des cases restant disponibles, <br>
	 * il faut retourner un PageId construit à partir de l’indice de la page<br>
	 * - sinon, on appelle addDataPage (ci-dessus) et on retourne le PageId donné par cet appel<br>
	 * @return PageId
	 * @throws IOException
	 */
	public PageId getFreePageId() throws IOException {
	
		int idFile = relation.getFileId();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
		ArrayList<Integer> idxList = hpi.getIdxPageTab();
		ArrayList<Integer> slotDispoList = hpi.getNbSlotsRestantDisponibles();
		
		for(int i = 0;i<idxList.size();i++) {
			int idx = idxList.get(i).intValue();
			int nbSlot = slotDispoList.get(i).intValue();
			
			if(nbSlot > 0) {
				return new PageId(idFile, idx); 
			}
		}
		return addDataPage();
	}
	
	/**
	 * - écrit le record dans la page comme suit :<br>
	 * - d’abord, le buffer de la page est obtenu via le BufferManager.<br>
	 * - ensuite, nous créons une PageBitmapInfo et la remplissons par readPageBitmapInfo<br>
	 * - ensuite, nous cherchons, avec les infos de la PageBitmapInfo, une case disponible ; soit IDX
	 * l’indice de la case<br>
	 * - nous écrivons le Record à la position slotCount+IDX*recordSize, en utilisant writeRecordInBuffer<br>
	 * - nous actualisons la PageBitmapInfo pour marquer la case IDX comme « occupée » (octet de valeur 1)<br>
	 * - nous recopions les information actualisées depuis la PageBitmapInfo vers le buffer via writePageBitmapInfo<br>
	 * - nous libérons enfin le buffer de la page auprès du BufferManager (quel flag?)<br>
	 * @param Record r
	 * @param PageId page
	 * @throws IOException
	 */
	public void insertRecordInPage(Record r, PageId page) throws IOException {
		byte[] bufferPage = BufferManager.getPage(page);
		
		PageBitmapInfo bitMap = new PageBitmapInfo();
		
		readPageBitmapInfo(bufferPage, bitMap);
		
		ArrayList<Byte> slotStatus = bitMap.getSlotStatus();
		
		//Retourne l'indice de la premiere occurence de l'element spécifié dans la list,
		//ou -1 si cette liste ne contient pas l'element
		int caseIdX = slotStatus.indexOf(new Byte((byte)0));
		
		if(caseIdX == -1) {
			BufferManager.freePage(page, 0);
		}
		else {
			int slotCount = relation.getSlotCount();
			int recordSize = relation.getRecordSize();
			
			//slotCount + idCaseFreeBitMap*recordSize
			//le 1er "slotCount" correspond aux nb byte pour la bitmap
			//le 2e membre "idCase*recordSize" correspond au 1er byte de la idCase ieme slot
			writeRecordInBuffer(r, bufferPage, slotCount + caseIdX*recordSize);
			
			//nous actualisons la PageBitMapInfo pour marquer la case caseIdX comme "occupée"
			//(octet de valeur de 1)
			bitMap.setStatusOccup(caseIdX);
			
			writePageBitmapInfo(bufferPage, bitMap);
			
			BufferManager.freePage(page, 1);
		}
	}
	
	public void insertRecord(Record r) throws IOException {
		PageId pageWhereRecordSaved = getFreePageId();
		insertRecordInPage(r, pageWhereRecordSaved);
		
		updateHeaderTakenSlot(pageWhereRecordSaved);
	}
	
	/**
	 * Affiche tous les records dans le heapfile suivis par la phrase (sur une ligne)<br>
	 * "Total records : x", où x est le nombre de records affichés.<br>
	 * On utilise getHeaderPageInfo pour les ids de toutes les pages, le BufferManager<br>
	 * pour accéder à une page de données spécifique, readPageBitmapInfo pour avoir le<br>
	 * statut des cases dans une page, et readRecordFromBuffer pour lire un record dans une case<br>
	 * @throws IOException 
	 */
	public void printAllRecords() throws IOException {
		//on recupere la header page du heapfile
		int fileIdHP = relation.getFileId();
		int slotCount = relation.getSlotCount();
		int recordSize = relation.getRecordSize();
		int totalRecordPrinted = 0;
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
		ArrayList<Integer> listIdxPage = hpi.getIdxPageTab();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int idxPageCourante = listIdxPage.get(i).intValue();
			PageId pageCourante = new PageId(fileIdHP, idxPageCourante); 
			
			//on recupere le contenu de la page courante
			byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
			
			PageBitmapInfo bitmapPageCourante = new PageBitmapInfo();
			readPageBitmapInfo(bufferPageCourante, bitmapPageCourante);
			
			ArrayList<Byte> slotStatusPageCourante = bitmapPageCourante.getSlotStatus();
			
			for(int j = 0; j<slotStatusPageCourante.size(); j++) {
				if(slotStatusPageCourante.get(j).byteValue() == (byte)1) {
					int caseIdX = j;
					//slotCount + idCaseBitMap*recordSize
					//le 1er "slotCount" correspond aux nb byte pour la bitmap
					//le 2e membre "idCase*recordSize" correspond au 1er byte de la idCase ieme slot
					Record recordToPrint = new Record();
					readRecordFromBuffer(recordToPrint, bufferPageCourante, slotCount + caseIdX*recordSize);
					System.out.println(recordToPrint.toString());
					totalRecordPrinted++;
				}
			}
			//on libere la page courante et on ne la pas modifier
			BufferManager.freePage(pageCourante, 0);
		}
		System.out.println("Total records : " + totalRecordPrinted);
	}
	
	/**
	 * Affiche tous les records qui ont la valeur v sur la colonne d’indice indice_colonne  
	 * (rappel TD1 : les colonnes sont numérotées de 1 à N, avec N le nombre de colonnes) suivis 
	 * par la phrase  (sur une nouvelle ligne) « Total records : x », où x est le nombre de 
	 * records affichés
	 * 
	 * @param indiceColonne
	 * @param condition
	 * @throws IOException 
	 */
	public void printAllRecordsWithFilter(int indiceColonne, String condition) throws IOException {
		//on recupere la header page du heapfile
		int fileIdHP = relation.getFileId();
		int slotCount = relation.getSlotCount();
		int recordSize = relation.getRecordSize();
		
		int totalRecordPrinted = 0;
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
		ArrayList<Integer> listIdxPage = hpi.getIdxPageTab();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int idxPageCourante = listIdxPage.get(i).intValue();
			PageId pageCourante = new PageId(fileIdHP, idxPageCourante); 
			
			//on recupere le contenu de la page courante
			byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
			
			PageBitmapInfo bitmapPageCourante = new PageBitmapInfo();
			readPageBitmapInfo(bufferPageCourante, bitmapPageCourante);
			
			ArrayList<Byte> slotStatusPageCourante = bitmapPageCourante.getSlotStatus();
			
			for(int j = 0; j<slotStatusPageCourante.size(); j++) {
				if(slotStatusPageCourante.get(j).byteValue() == (byte)1) {
					int caseIdX = j;
					//slotCount + idCaseBitMap*recordSize
					//le 1er "slotCount" correspond aux nb byte pour la bitmap
					//le 2e membre "idCase*recordSize" correspond au 1er byte de la idCase ieme slot
					Record recordToPrint = new Record();
					readRecordFromBuffer(recordToPrint, bufferPageCourante, slotCount + caseIdX*recordSize);
					//probleme lorsqu'on cherche une valeur avec string car si on a tapé 
					//un string avec une longueur de chaine < a la taille de la colonne string x
					//il y a des espaces manquants
					if(recordToPrint.getListValues().get(indiceColonne-1).equals(condition)) {
						System.out.println(recordToPrint.toString());
						totalRecordPrinted++;
					}
				}
			}
			//on libere la page courante et on ne la pas modifier
			BufferManager.freePage(pageCourante, 0);
		}
		System.out.println("Total records : " + totalRecordPrinted);
	}
	
	/**
	 * 
	 * @return retourne la liste de tous les records d'une relation
	 * @throws IOException 
	 */
	public ArrayList<Record> getAllRecords() throws IOException {
		ArrayList<Record> listRecords = new ArrayList<Record>(0);
	
		//on recupere la header page du heapfile
		int fileIdHP = relation.getFileId();
		int slotCount = relation.getSlotCount();
		int recordSize = relation.getRecordSize();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
		ArrayList<Integer> listIdxPage = hpi.getIdxPageTab();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int idxPageCourante = listIdxPage.get(i).intValue();
			PageId pageCourante = new PageId(fileIdHP, idxPageCourante); 
			
			//on recupere le contenu de la page courante
			byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
			
			PageBitmapInfo bitmapPageCourante = new PageBitmapInfo();
			readPageBitmapInfo(bufferPageCourante, bitmapPageCourante);
			
			ArrayList<Byte> slotStatusPageCourante = bitmapPageCourante.getSlotStatus();
			
			for(int j = 0; j<slotStatusPageCourante.size(); j++) {
				if(slotStatusPageCourante.get(j).byteValue() == (byte)1) {
					int caseIdX = j;
					//slotCount + idCaseBitMap*recordSize
					//le 1er "slotCount" correspond aux nb byte pour la bitmap
					//le 2e membre "idCase*recordSize" correspond au 1er byte de la idCase ieme slot
					Record recordToAdd = new Record(pageCourante,slotCount + caseIdX*recordSize);
					readRecordFromBuffer(recordToAdd, bufferPageCourante, slotCount + caseIdX*recordSize);
					listRecords.add(recordToAdd);
				}
			}
			//on libere la page courante et on ne la pas modifier
			BufferManager.freePage(pageCourante, 0);
		}
		return listRecords;
	}
	
	/**
	 * 
	 * @return retourne la liste de tous les records d'une page
	 * @throws IOException 
	 */
	public ArrayList<Record> getAllRecordsPage(PageId page) throws IOException {
		ArrayList<Record> listRecords = new ArrayList<Record>(0);
	
		int slotCount = relation.getSlotCount();
		int recordSize = relation.getRecordSize();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
		//on recupere le contenu de la page passé en parametre
		byte[] bufferPage = BufferManager.getPage(page);
			
		PageBitmapInfo bitmapPage = new PageBitmapInfo();
		readPageBitmapInfo(bufferPage, bitmapPage);
			
		ArrayList<Byte> slotStatusPage = bitmapPage.getSlotStatus();
			
		for(int j = 0; j<slotStatusPage.size(); j++) {
			if(slotStatusPage.get(j).byteValue() == (byte)1) {
				int caseIdX = j;
				//slotCount + idCaseBitMap*recordSize
				//le 1er "slotCount" correspond aux nb byte pour la bitmap
				//le 2e membre "idCase*recordSize" correspond au 1er byte de la idCase ieme slot
				Record recordToAdd = new Record(page,slotCount + caseIdX*recordSize);
				readRecordFromBuffer(recordToAdd, bufferPage, slotCount + caseIdX*recordSize);
				listRecords.add(recordToAdd);
			}
		}
		
		//on libere la page et on ne la pas modifier
		BufferManager.freePage(page, 0);

		return listRecords;
	}
	
	
	public RelDef getRel() {
		return relation;
	}

	public void join(HeapFile relFind2, int col1, int col2) throws IOException {
		//on recupere les fileid des heapfiles
		int fileIdHP1 = relation.getFileId();
		int fileIdHP2 = relFind2.getRel().getFileId();
		
		int totalRecordPrinted = 0;
		
		//on recupere la header page des heapfiles
		HeaderPageInfo hpi1 = new HeaderPageInfo();
		getHeaderPageInfo(hpi1);
		HeaderPageInfo hpi2 = new HeaderPageInfo();
		relFind2.getHeaderPageInfo(hpi2);
		
		ArrayList<Integer> listIdxPage1 = hpi1.getIdxPageTab();
		ArrayList<Integer> listIdxPage2 = hpi2.getIdxPageTab();
		
		//boucle de la relation1
		for(int i=0; i<listIdxPage1.size(); i++) {
			
			int idxPageCourante1 = listIdxPage1.get(i).intValue();
			PageId pageCourante1 = new PageId(fileIdHP1, idxPageCourante1); 
			
			ArrayList<Record> listRecords1 = getAllRecordsPage(pageCourante1);
			
			//boucle de la relation2
			for(int j=0; j<listIdxPage2.size(); j++) {
				
				int idxPageCourante2 = listIdxPage2.get(j).intValue();
				PageId pageCourante2 = new PageId(fileIdHP2, idxPageCourante2); 
				
				ArrayList<Record> listRecords2 = relFind2.getAllRecordsPage(pageCourante2);
				
				for(int h=0; h<listRecords1.size();h++) {
					for(int k=0; k<listRecords2.size();k++) {
						Record r1 = listRecords1.get(h);
						Record r2 = listRecords2.get(k);
						
						String val1 = r1.getListValues().get(col1-1);
						String val2 = r2.getListValues().get(col2-1);
						
						if(val1.equals(val2)) {
							System.out.println(r1.toString() + r2.toString());
							totalRecordPrinted++;
						}	
					}
				}
				
			}
		}
						
		System.out.println("Total records : " + totalRecordPrinted);
	
	}
}
