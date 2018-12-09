package Schema;

import java.util.ArrayList;

public class bytemap {
	private ArrayList<Byte> slotIndice;
	
	public bytemap(ArrayList<Byte> slotIndice) {
		super();
		this.slotIndice = slotIndice;
	}

	public ArrayList<Byte> getSlotIndice() {
		return slotIndice;
	}

	public void setSlotIndice(ArrayList<Byte> slotIndice) {
		this.slotIndice = slotIndice;
	}

	public bytemap() {
		slotIndice = new ArrayList<Byte>(0);
		
	}
	
	public void addSlotIndice(Byte status) {
		slotIndice.add(status);
	}
	
	public void slotOccup(int indice) {
		slotIndice.set(indice, new Byte((byte)1));
	}
}
