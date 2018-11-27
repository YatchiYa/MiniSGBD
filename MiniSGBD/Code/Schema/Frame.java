package Schema;

import java.util.Date;


public class Frame {
	
	private PageId page;
	private int pinCount;
	private int dirtyFlag;
	private Date initialTimeFrame;
	
	
	public Frame(PageId page, int pinCount, int dirtyFlag) {
		super();
		this.page = page;
		this.pinCount = 0;
		this.dirtyFlag = 0;
		this.initialTimeFrame = null;
	}
	
	public Frame() {
	}

	
	public Date getInitialTimeFrame() {
		return initialTimeFrame;
	}
	public void setInitialTimeFrame(Date initialTimeFrame) {
		this.initialTimeFrame = initialTimeFrame;
	}
	
	public void rezTime() {
		this.initialTimeFrame= new Date();
	}

	public PageId getPage() {
		return page;
	}


	public void setPage(PageId page) {
		this.page = page;
	}


	public int getPinCount() {
		return pinCount;
	}


	public void setPinCount(int pinCount) {
		this.pinCount = pinCount;
	}


	public int getDirtyFlag() {
		return dirtyFlag;
	}


	public void setDirtyFlag(int dirtyFlag) {
		this.dirtyFlag = dirtyFlag;
	}
	
	

	/**
	 * décrémente le pin count de 1
	 */
	public void decrementPinCount() {
		this.pinCount--;
	}
	
	/**
	 * incrémente le pin count de 1
	 */
	public void incrementPinCount() {
		this.pinCount++;
	}
	
	
	public String toString() {
		return("<page=" + this.page + ",pin=" + this.pinCount + ",dirty=" + this.dirtyFlag + ">\n");
	}
}
