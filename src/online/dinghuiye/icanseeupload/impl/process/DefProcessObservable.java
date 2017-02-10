package online.dinghuiye.icanseeupload.impl.process;

import online.dinghuiye.icanseeupload.core.processobserve.ProcessObservable;

public class DefProcessObservable extends ProcessObservable {

	@Override
	public void change(int inCome) {
		setChanged();
		notifyObservers(inCome);
	}
	
}
