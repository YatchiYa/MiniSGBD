package sgbd;

import java.util.ArrayList;

import Schema.RelSchemaDef;
import rel.RelDef;

public class FileManager {
	
	private static final FileManager INSTANCE = new FileManager();

	public static FileManager getInstance(){
	      return INSTANCE;
	}

	private static ArrayList<HeapFile> listeHeapFile;
	
	
	public static void init(DBDef db) {
		
		listeHeapFile = new ArrayList<HeapFile>(0);
		for(RelDef r : db.getListRelDef()) {
			listeHeapFile.add(new HeapFile(r));
		}
		
	}

	
	
	
	public static ArrayList<HeapFile> getListeHeapFile() {
		return listeHeapFile;
	}


	public static void setListeHeapFile(ArrayList<HeapFile> listeHeapFile) {
		FileManager.listeHeapFile = listeHeapFile;
	}
		
	
	
}
