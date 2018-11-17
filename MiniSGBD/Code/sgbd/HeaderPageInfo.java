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

public void setDataPageCount(int dataPageCount) {
	this.dataPageCount = dataPageCount;
}

public ArrayList<Integer> getPageIdx() {
	return pageIdx;
}

public void setPageIdx(ArrayList<Integer> pageIdx) {
	this.pageIdx = pageIdx;
}

public ArrayList<Integer> getFreeSlot() {
	return freeSlot;
}

public void setFreeSlot(ArrayList<Integer> freeSlot) {
	this.freeSlot = freeSlot;
}

public void readFromBuffer(byte[] headerPage, HeaderPageInfo hfi) throws IOException {
	
	ByteBuffer buffer = ByteBuffer.wrap(headerPage);
	
	
	}

public void writeToBuffer() throws IOException {
	
}




}
