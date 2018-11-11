package sgbd;

import java.util.ArrayList;

public class PageBitmapInfo {
	private ArrayList<Byte> slotStatus;
	
	public PageBitmapInfo() {
		slotStatus = new ArrayList<Byte>(0);
	}

	public ArrayList<Byte> getSlotStatus() {
		return slotStatus;
	}

	public void setSlotStatus(ArrayList<Byte> slotStatus) {
		this.slotStatus = slotStatus;
	}

	public void addSlotStatus(Byte status) {
		slotStatus.add(status);
	}
	
	public void setStatusOccup(int indice) {
		slotStatus.set(indice, new Byte((byte)1));
	}
	
	
}
