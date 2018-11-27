package Schema;
import java.util.Arrays;

public class BufferTable {

	private static final long sizePage = 4096;
	
	private Frame frame;
	private byte[] buffer;

	
	public BufferTable(Frame frame, byte[] buffer) {
		super();
		this.frame = new Frame();
		this.buffer = new byte[(int)constants.Constants.pageSize];
	}

	
	
	public Frame getframe() {
		return frame;
	}

	public void setframe(Frame frame) {
		this.frame = frame;
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
		return "BufferTable [frame=" + frame + ", buffer=" + Arrays.toString(buffer) + "]";
	}
	
	
	
	
	
}
