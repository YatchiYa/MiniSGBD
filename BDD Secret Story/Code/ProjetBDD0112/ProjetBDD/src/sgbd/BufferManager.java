package sgbd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class BufferManager {
	/**
	 * le bufferpool contient F frames
	 */
	private static final int F = 2;
	private static final long K = 4096;
	
	private static FrameDuBufferPool[] bufferPool;
	
	//initialisation du bufferpool
	static {
		bufferPool = new FrameDuBufferPool[F];
		
		for(int i = 0; i<F ; i++) {
			bufferPool[i] = new FrameDuBufferPool();
		}
	}
	
	/**
	 * Cette méthode doit incrémenter le pin_count et aussi s’occuper 
	 * du remplacement d’une frame si besoin.
	 * @param page la page demandée
	 * @return le contenu de la page demandée
	 * @throws IOException
	 */
	public static byte[] getPage(PageId page) throws IOException{
		
		for(int j = 0; j<F ; j++) {
			//Demande d'une page deja presente dans le bufferpool => incrementer le pin 
			if(page.equals(bufferPool[j].getTi().getPage())) {
				bufferPool[j].getTi().incrementPinCount();
				return bufferPool[j].getBuffer();
			}
			//Frame vide = pas remplie
			if(bufferPool[j].getTi().getPage() == null) {
				byte[] bufferPage = new byte[(int)K];
				DiskManager.readPage(page, bufferPage);
				bufferPool[j].setBuffer(bufferPage);
				bufferPool[j].getTi().incrementPinCount();
				bufferPool[j].getTi().setPage(page);
				return bufferPage;
			}
		}
		
		//Ici le bufferpool est rempli => demande de page inexistante => politique de remplacement
	
		//Tout d'abord, on recupere toutes les frames qui ont un pincount à 0
		ArrayList<FrameDuBufferPool> framePinCountZero = new ArrayList<FrameDuBufferPool>(0);
		for(int i = 0; i<F; i++) {
			//on garde toutes les frames qui ont un pincount à 0
			if(bufferPool[i].getTi().getPinCount() == 0) {
				framePinCountZero.add(bufferPool[i]);
			}
		}

		//on vérifie que la liste contenant les frames qui ont un pincount à 0 n'est pas vide
		if(framePinCountZero.size() != 0) {
			//la liste n'est pas vide
			//On doit trouver la frame à remplacer => celle qui a "le plus ancien temps de unpin"
			//On initialise la frame à remplacer par la premiere frame de la liste contenant les frames qui ont un pincount à 0
			FrameDuBufferPool frameAReplacer = framePinCountZero.get(0);
			
			//On cherche la frame qui a été le moins récemment utilisée => "le plus ancien temps de unpin"
			//On commence la boucle à 1 car on a déjà la première valeur dans frameReplace
			for(int i = 1; i<framePinCountZero.size(); i++) {
				//les temps de unpin
				Date timePinCountZeroCourant = framePinCountZero.get(i).getTi().getTimePinCountAtZero();
				Date timeLru = frameAReplacer.getTi().getTimePinCountAtZero();
				
				//si le temps de la frame courante est avant celui de la frame 
				if(timePinCountZeroCourant.before(timeLru)) {
					frameAReplacer = framePinCountZero.get(i);
				}
			}
			
			//Dirty flag = https://en.wikipedia.org/wiki/Dirty_bit
			//Avant son remplacement, si la frame/page est marquée comme “dirty”,
			//écrire son contenu sur le disque
			if(frameAReplacer.getTi().getDirtyFlag() == 1) {
				DiskManager.writePage(frameAReplacer.getTi().getPage(),frameAReplacer.getBuffer());
			}
			//remplacement
			frameAReplacer.getTi().setPage(page);
			frameAReplacer.getTi().incrementPinCount();
			byte[] contenuPage = new byte[(int)K];
			DiskManager.readPage(page, contenuPage);
			frameAReplacer.setBuffer(contenuPage);
			
			return contenuPage;
		}
		//sinon la liste est vide => aucune frame à un pincount à 0 => toutes les frames sont en cours d'utilisation
		else {
			System.out.println("*** Les pages sont en cours d'utilisation ! ***");
			return null;
		}
	}
	
	/**
	 * Cette méthode libère une page et spécifie si elle a été modifiée.<br>
	 * Cette méthode décrémente le pin_count et actualise le flag dirty de la page.
	 * @param page
	 * @param isDirty prend la valeur 0 ou 1 (pas modifiée ou modifiée)
	 */
	public static void freePage(PageId page,int isDirty) {
		for(int i = 0;i<F;i++) {
			if(page.equals(bufferPool[i].getTi().getPage())) {
				bufferPool[i].getTi().decrementPinCount();
				if(bufferPool[i].getTi().getPinCount() == 0) {
					bufferPool[i].getTi().setTimePinCountAtZero(new Date());
				}
				if(isDirty == 1) {
					bufferPool[i].getTi().setDirtyFlag(isDirty);
				}
			}
		}
	}
	
	/**
	 * Cette méthode écrit tous les contenus (buffers) des cases avec le flag dirty = 1 sur le disque.
	 * 
	 * Elle «vide» toutes les frames (elle s’occupe non seulement d’écrire le contenu des frames dirty sur disque,<br> 
	 * mais aussi d’«enlever» tous les buffers de toutes les frames et <br>marquer les frames comme «inoccupées» = pas de page dedans, pin_count 0, dirty 0...)
	 * Pour être rigoureux, il faudrait vérifier que le pin_count est déjà 0 pour toutes les cases; si ce n’est pas le cas:<br> 
	 * il faudrait que votre appli quitte avec un message d’erreur.<br>  
	 * 
	 */
	public static void flushAll() {
	
		boolean pinCountZero = true;
		//on vérifie que toutes les frames ont leur pincount==0
		//Si une des frames à son pin count != de 0, on quitte l'appli
		for(int i = 0; i<F; i++) {
			if(bufferPool[i].getTi().getPinCount() != 0) {
				pinCountZero = false;
			}
		}
		
		if(pinCountZero) {
			for(int i = 0; i<F; i++) {
				if(bufferPool[i].getTi().getDirtyFlag() == 1) {
					try{
						DiskManager.writePage(bufferPool[i].getTi().getPage(), bufferPool[i].getBuffer());
					}catch(IOException e) {
						System.out.println("Une erreur s'est produite lors de l'écriture sur disque !");
						System.out.println("Détails : " + e.getMessage());
					}
				}
				//on marque les frames comme "inoccupées"
				bufferPool[i] = new FrameDuBufferPool();
			}
		}
		else {
			System.out.println("*** Erreur ! Les pages sont en cours d'utilisation ! ");
			System.exit(-1);
		}
	}
	
	/**
	 * Retourne la seule et unique instance du BufferPool
	 * @return le bufferPool
	 */
	public static FrameDuBufferPool[] getBufferPool() {
		return bufferPool;
	}
}
