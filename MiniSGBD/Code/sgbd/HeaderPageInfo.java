package sgbd;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class HeaderPageInfo {
private int  dataPageCount;
private ArrayList<Integer> pageIdx;
private ArrayList<Integer> freeSlot;

public HeaderPageInfo(int dataPageCount) {
	
	this.dataPageCount =0;
	this.pageIdx =new ArrayList<Integer>(dataPageCount);;
	this.freeSlot = new ArrayList<Integer>(dataPageCount);;
}

public int getDataPageCount() {
	return dataPageCount;
}


/**
 * 
 * @param dataPageCount
 */
public void setDataPageCount(int dataPageCount) {
	this.dataPageCount = dataPageCount;
}


/**
 * 
 * @return
 */
public ArrayList<Integer> getPageIdx() {
	return pageIdx;
}


/**
 * 
 * @param pageIdx
 */
public void setPageIdx(ArrayList<Integer> pageIdx) {
	this.pageIdx = pageIdx;
}


/**
 * 
 * @return
 */

public ArrayList<Integer> getFreeSlot() {
	return freeSlot;
}



/**
 * 
 * @param freeSlot
 */
public void setFreeSlot(ArrayList<Integer> freeSlot) {
	this.freeSlot = freeSlot;
}



/**
 * 
 * @param headerPage
 * @param headerF
 * @throws IOException
 */
public void writeToBuffer(byte[] headerPage, HeaderPageInfo headerF) throws IOException {
    int i=0;
	ByteBuffer buffer = ByteBuffer.wrap(headerPage);
	buffer.putInt(headerF.getDataPageCount());
	ArrayList<Integer> idxTab = headerF.getPageIdx();
	ArrayList<Integer> freeSlotTab = headerF.getFreeSlot();
	
	while(i<idxTab.size()) {
		buffer.putInt(idxTab.get(i).intValue());
		buffer.putInt(freeSlotTab.get(i).intValue());	
		i++;
	}
			
	}


/**
 * 
 * @param headerPage
 * @param headerF
 * @throws IOException
 */
public void readFromBuffer(byte[] headerPage, HeaderPageInfo headerF ) throws IOException {
	int i=0;
	ByteBuffer buffer = ByteBuffer.wrap(headerPage);
	int nbPage = buffer.getInt();
	headerF.setDataPageCount(nbPage);

	while(i<nbPage) {
		Integer idx = new Integer(buffer.getInt());
		Integer nbSlot = new Integer(buffer.getInt());
		pageIdx.add(idx);
		freeSlot.add(nbSlot);
		i++;
	}	
}




}
