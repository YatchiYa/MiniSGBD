import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;



public class BufferManager {
	
	// constant a deplacer au file constante 
	public static final int frameCount = 2;
	public static final int sizePage = 4096;

	private static BufferTable[] bufferPool;

	
	
	public static byte[] getPage(PageId page) throws IOException {
		
		for(int j = 0; j<frameCount ; j++) {
			//Demande d'une page deja presente dans le bufferpool => incrementer le pin 
			
			if(page.equals(bufferPool[j].getInf().getPage())) {
				bufferPool[j].getInf().incrementPinCount();
				return bufferPool[j].getBuffer();
			}
			
			//Frame vide = pas remplie
			if(bufferPool[j].getInf().getPage() == null) {
				
				byte[] bufferPage = new byte[(int)sizePage];
				// dabord faire le diskManager
				// DiskManager.readPage(page, bufferPage);
				bufferPool[j].setBuffer(bufferPage);
				bufferPool[j].getInf().incrementPinCount();
				bufferPool[j].getInf().setPage(page);
				return bufferPage;
			}
		}
		
		
			ArrayList<BufferTable> framePinCountZero = new ArrayList<BufferTable>(0);
			for(int i = 0; i<frameCount; i++) {
				//on garde toutes les frames qui ont un pincount à 0
				if(bufferPool[i].getInf().getPinCount() == 0) {
					framePinCountZero.add(bufferPool[i]);
				}
			}
				
			
			//on vérifie que la liste contenant les frames qui ont un pincount à 0 n'est pas vide
			if(framePinCountZero.size() != 0) {
				//la liste n'est pas vide
				//On doit trouver la frame à remplacer => celle qui a "le plus ancien temps de unpin"
				//On initialise la frame à remplacer par la premiere frame de la liste contenant les frames qui ont un pincount à 0
				
				BufferTable frameAReplacer = framePinCountZero.get(0);
				
				//On cherche la frame qui a été le moins récemment utilisée => "le plus ancien temps de unpin"
				//On commence la boucle à 1 car on a déjà la première valeur dans frameReplace
				for(int i = 1; i<framePinCountZero.size(); i++) {
					
					
					
				}
			
				byte[] contenuPage = new byte[(int)sizePage];
				// DiskManager.readPage(page, contenuPage);
				frameAReplacer.setBuffer(contenuPage);
				
				return contenuPage;
			}
			else {
				System.out.println("*** Les pages sont en cours d'utilisation ! ***");
				return null;
			}
				
	}


	
	/**
	 * 
	 * @param page
	 * @param isDirty
	 */
	public static void freePageId(PageId page,int isDirty) {

		for(int i = 0;i< frameCount;i++) {
			
			if(page.equals(bufferPool[i].getInf().getPage())) {
				
				bufferPool[i].getInf().decrementPinCount();
				if(bufferPool[i].getInf().getPinCount() == 0) {
					
				}
				if(isDirty == 1) {
					bufferPool[i].getInf().setDirtyFlag(isDirty);
				}
			}
		}

	}
	
	
	
	public static void flushAll() {
		boolean pinCountZero = true;
		
		//on vérifie que toutes les frames ont leur pincount==0
		//Si une des frames à son pin count != de 0, on quitte l'appli
		
		for(int i = 0; i< frameCount; i++) {
			if(bufferPool[i].getInf().getPinCount() != 0) {
				pinCountZero = false;
			}
		}
		
		if(pinCountZero) {
			for(int i = 0; i< frameCount; i++) {
				
				if(bufferPool[i].getInf().getDirtyFlag() == 1) {
					
					try{
						// diskmanager a cree dabord par Yanis
						
						// DiskManager.writePage(bufferPool[i].getInf().getPage(), bufferPool[i].getBuffer());
					}catch(IOException e) {
						System.out.println("Une erreur s'est produite lors de l'écriture sur disque !");
						System.out.println("Détails : " + e.getMessage());
					}
				}
				//on marque les frames comme "inoccupées"
				bufferPool[i] = new BufferTable(null, null);
			}
		}
		else {
			System.out.println("*** Erreur ! Les pages sont en cours d'utilisation ! ");
			System.exit(-1);
		}
		
	}




	public static BufferTable[] getBufferPool() {
		return bufferPool;
	}



	public static void setBufferPool(BufferTable[] bufferPool) {
		BufferManager.bufferPool = bufferPool;
	}

	
}
