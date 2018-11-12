
public class PageId {
	
	private int FileIdx;
	private int PageIdx;
	/**
	 * 
	 * @return
	 */
	
	
	public int getFileIdx() {
		return FileIdx;
	}
	public void setFileIdx(int fileIdx) {
		FileIdx = fileIdx;
	}
	public int getPageIdx() {
		return PageIdx;
	}
	public void setPageIdx(int pageIdx) {
		PageIdx = pageIdx;
	}
	public PageId(int fileIdx, int pageIdx) {
		super();
		FileIdx = fileIdx;
		PageIdx = pageIdx;
	}

}
