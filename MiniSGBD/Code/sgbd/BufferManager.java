package sgbd;
/**
 * ce qu'il faut faire !!! 
 * ->  le systeme de LRU
 * 
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import Schema.BufferTable;
import Schema.PageId;
import constants.Constants;


//  commentaire !!! 
public class BufferManager {
	
	private static BufferTable[] bufferPool;

	
	/**
	 * 
	 * @param page
	 * @return
	 * @throws IOException
	 */
	public static byte[] getPage(PageId page) throws IOException {
		
		for(int j = 0; j<Constants.frameCount ; j++) {
			
			/** on demande une page deja presente dns le bufferPool
			 * si c'est le cas =>  frameCount++
			 */
			
			if(page.equals(bufferPool[j].getframe().getPage())) {
				bufferPool[j].getframe().incrementPinCount();
				return bufferPool[j].getBuffer();
			}
			
			
			//Frame vide = pas remplie
			if(bufferPool[j].getframe().getPage() == null) {
				
				byte[] forPage = new byte[(int)Constants.pageSize];  // create a new buffer for the pages with byte encoding
				// dabord faire le diskManager
				// DiskManager.readPage(page, forPage);
				bufferPool[j].setBuffer(forPage);
				bufferPool[j].getframe().incrementPinCount();
				bufferPool[j].getframe().setPage(page);
				return forPage;
			}
		}
		
		
			ArrayList<BufferTable> CountFrame_zero = new ArrayList<BufferTable>(0);
			for(int i = 0; i<Constants.frameCount; i++) {
				
				// frame avec 0 pincount et on les ajoute a notre bufferpool
				if(bufferPool[i].getframe().getPinCount() == 0) {
					CountFrame_zero.add(bufferPool[i]);
				}
			}
				
				// check if pincount different de 0 ==> l'arrayList n'est pas vide 
			if(CountFrame_zero.size() != 0) {
				
				try {

					BufferTable toChange = CountFrame_zero.get(0);  // frame a changer
					
					
					//  LRUUUUUUU to check after
					for(int i = 1; i<CountFrame_zero.size(); i++) {
						
						// il verifie la trame, son pinCountTime a l instant t
						Date currentTime = CountFrame_zero.get(i).getframe().getInitialTimeFrame();
						Date lruTime = toChange.getframe().getInitialTimeFrame();
						
						// si la trame courante est avant la trame select
						if(currentTime.before(lruTime)) {
							toChange = CountFrame_zero.get(i);
						}
					}
					
					
					
					
					
					// check if dirty Flag is 0, if true, then write it in DiskManger
					if(toChange.getframe().getDirtyFlag() == 1) {
						DiskManager.WritePage(toChange.getframe().getPage(), toChange.getBuffer());
					}
				
					
					// maintenant on procede au remplacement.
					/**
					 * on recupere la frame, et on met la page dans cette frame
					 */
					toChange.getframe().setPage(page);
					// pinCount++
					toChange.getframe().incrementPinCount();
					
					// on cree un buffer et on l'ajoute au disk manager
					byte[] contenuPage = new byte[(int)Constants.pageSize];
					DiskManager.ReadPage(page, contenuPage);
					toChange.setBuffer(contenuPage);
					
					return contenuPage;
					
				}catch(Exception e) {
					System.out.println("error " + e);
					return null;
				}
			}
			else {
				return null;
			}
				
	}


	
	/**
	 * 
	 * @param page
	 * @param isDirty
	 */
	public static void freePageId(PageId page,int isDirty) {

		for(int i = 0;i< Constants.frameCount;i++) {
			
			if(page.equals(bufferPool[i].getframe().getPage())) {
				
				bufferPool[i].getframe().decrementPinCount();
				if(bufferPool[i].getframe().getPinCount() == 0) {
					bufferPool[i].getframe().rezTime();
				}
				if(isDirty == 1) {
					bufferPool[i].getframe().setDirtyFlag(isDirty);
				}
			}
		}

	}
	
	
	
	public static void flushAll() {
		boolean razPinCount = true;
		
		// if(frame pincount == 0 )
		// si la condition precedente n'est pas realiser on quitte le programe
		
		for(int i = 0; i< Constants.frameCount; i++) {
			if(bufferPool[i].getframe().getPinCount() != 0) {
				razPinCount = false;
			}
		}
		
		// si c'est false on quitte le programe
		if(razPinCount) {
			for(int i = 0; i< Constants.frameCount; i++) {
				
				if(bufferPool[i].getframe().getDirtyFlag() == 1) {
					
					try{
						// on ecrit sur le diskmanager
						
						DiskManager.WritePage(bufferPool[i].getframe().getPage(), bufferPool[i].getBuffer());
					}catch(IOException e) {
						System.out.println("error : " + e.getMessage());
					}
				}
				//on marque les frames comme "inoccupées"
				bufferPool[i] = new BufferTable(null, null);
			}
		}
		else {
			System.out.println(" page occupé");
			System.exit(-99999);   // pour sortir du programe
		}
		
	}




	public static BufferTable[] getBufferPool() {
		return bufferPool;
	}



	public static void setBufferPool(BufferTable[] bufferPool) {
		BufferManager.bufferPool = bufferPool;
	}

	
}
