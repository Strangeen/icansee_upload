package online.dinghuiye.icanseeupload.impl.process;

public class ProcessBar {

	private long total;
	private long process;
	
	public ProcessBar(long process, long total) {
		super();
		this.process = process;
		this.total = total;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getProcess() {
		return process;
	}

	public void setProcess(long process) {
		this.process = process;
	}
}
