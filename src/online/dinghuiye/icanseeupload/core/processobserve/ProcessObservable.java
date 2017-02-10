package online.dinghuiye.icanseeupload.core.processobserve;

import java.util.Observable;

public abstract class ProcessObservable extends Observable {
	
	public abstract void change(int inCome);
	
}
