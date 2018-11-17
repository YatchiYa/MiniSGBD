package sgbd;
import java.util.Arrays;

public class BufferTable {

	private static final long sizePage = 4096;
	
	private InfoPage inf;
	private byte[] buffer;

	
	public BufferTable(InfoPage inf, byte[] buffer) {
		super();
		this.inf = inf;
		this.buffer = buffer;
	}

	
	
	public InfoPage getInf() {
		return inf;
	}

	public void setInf(InfoPage inf) {
		this.inf = inf;
	}

	public static long getSizepage() {
		return sizePage;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] getBuffer() {
		return buffer;
	}

	/**
	 * 
	 * @param buffer
	 */
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}



	@Override
	public String toString() {
		return "BufferTable [inf=" + inf + ", buffer=" + Arrays.toString(buffer) + "]";
	}
	
	
	
	
	
}
