
public class PageId {

	private int FileIdx ; 
	private int PageIdx;
	
	
	

	/**
	 * Constructeur de la classe PageId
	 * @param fileIdx
	 * @param pageIdx
	 */
	public PageId(int fileIdx, int pageIdx) {
		super();
		FileIdx = fileIdx;
		PageIdx = pageIdx;
	}

	/**
	 * 
	 * @return FileIndx l'identifiant du fichier ou est contenu la page 
	 */
	public int getFileIdx() {
		return FileIdx;
	}

	/**
	 * 
	 * @param fileIdx
	 */
	public void setFileIdx(int fileIdx) {
		FileIdx = fileIdx;
	}

	/**
	 * 
	 * @return PageIdx l'identifiant de la page
	 */
	public int getPageIdx() {
		return PageIdx;
	}

	
	public void setPageIdx(int pageIdx) {
		PageIdx = pageIdx;
	}
}
