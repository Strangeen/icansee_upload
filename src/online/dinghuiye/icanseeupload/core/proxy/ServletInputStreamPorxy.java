package online.dinghuiye.icanseeupload.core.proxy;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

import online.dinghuiye.icanseeupload.core.processobserve.ProcessObservable;

public class ServletInputStreamPorxy extends ServletInputStream {

	private InputStream in;
	private ProcessObservable procsObsvb;
	
	public ServletInputStreamPorxy(InputStream in, ProcessObservable procsObsvb) {
		this.in = in;
		this.procsObsvb = procsObsvb;
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
        int bs = in.read(b, off, len);
        // modify process observable
        if (procsObsvb != null) procsObsvb.change(bs); 
        return bs;
    }

	@Override
	public int read() throws IOException {
		return in.read();
	}
}
