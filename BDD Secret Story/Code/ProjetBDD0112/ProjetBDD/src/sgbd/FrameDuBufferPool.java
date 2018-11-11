package sgbd;

public class FrameDuBufferPool {
	private static final long K = 4096;
	
	private TableInformation ti;
	private byte[] buffer;
	
	public FrameDuBufferPool() {
		this.ti = new TableInformation();
		this.buffer = new byte[(int)K];
	}

	public TableInformation getTi() {
		return ti;
	}

	public void setTi(TableInformation ti) {
		this.ti = ti;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	
	public String toString() {
		return(ti.toString());
	}
}
