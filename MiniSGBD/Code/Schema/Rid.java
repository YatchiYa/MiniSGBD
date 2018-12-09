package Schema;

public class Rid {
	
	private PageId pageId;
	private int slotIdx;
	
	public Rid() {
		super();
	}
	public Rid(PageId idP, int s) {
		super();
		this.pageId = idP;
		this.slotIdx = s;
	}
	
	public PageId getPageId() {
		return pageId;
	}
	
	
	public void setPageId(PageId pageId) {
		this.pageId = pageId;
	}
	public int getSlotIdx() {
		return slotIdx;
	}
	public void setSlotIdx(int slotIdx) {
		this.slotIdx = slotIdx;
	}
	
	
	
}
