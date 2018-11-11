package sgbd;

/**
 * Cette classe contiendra un entier FileId et un entier Idx.
 *
 */
public class PageId {
	/**
	 * Attribut fileId qui correspond à l'identifiant du fichier où est située la page
	 */
	private int fileId;
	/**
	 * Attribut idX qui correspond à l'identifiant de la page
	 */
	private int idX;
	
	/**
	 * Constructeur de la classe PageId
	 * @param fileId
	 * @param idX
	 */
	public PageId(int fileId, int idX) {
		this.fileId = fileId;
		this.idX = idX;
	}
	
	/**
	 * @return fileId l'identifiant du fichier où est contenu la page
	 */
	public int getFileId() {
		return fileId;
	}
	
	/**
	 * @return idX l'identifiant de la page
	 */
	public int getIdX() {
		return idX;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageId other = (PageId) obj;
		if (fileId != other.fileId)
			return false;
		if (idX != other.idX)
			return false;
		return true;
	}
}