package rel;
import java.util.List;

import Schema.RelSchemaDef;

public class RelDef {
		
	private RelSchemaDef relDef;
	private int recordSize,slotCount;
	private int fileIdx;
	
	/**
	 * 
	 * @param relDef
	 */
	public RelDef(RelSchemaDef relDef) {
	
		this.relDef = relDef;
	}
	
	
	public int getFileIdx() {
		return fileIdx;
	}

	public void setFileIdx(int fileIdx) {
		this.fileIdx = fileIdx;
	}

	
	public int getRecordSize() {
		return recordSize;
	}

	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}

	public int getSlotCount() {
		return slotCount;
	}

	public void setSlotCount(int slotCount) {
		this.slotCount = slotCount;
	}

	/**
	 * 
	 * @return
	 */
	public RelSchemaDef getRelDef() {
		return relDef;
	}

	/**
	 * 
	 * @param relDef
	 */
	public void setRelDef(RelSchemaDef relDef) {
		this.relDef = relDef;
	}



	
}
