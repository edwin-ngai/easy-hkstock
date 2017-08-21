package com.wising.easyhkstock.common.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//This class is not thread safe
public class DataTask<T> implements Runnable{

	private DataBuilder<T> builder;
	private List<DataDispatcher<T>> dispatchers = new ArrayList<DataDispatcher<T>>();
	
	public DataTask(DataBuilder<T> builder) {
		Objects.requireNonNull(builder);
		this.builder = builder;
	}
	
	public void run() {
		List<T> data = builder.build();
		if (!data.isEmpty()) {
//			dispatchers.forEach(dispatcher -> dispatcher.dispatch(data));
			dispatchers.forEach(
				dispatcher -> new Thread(()->
						dispatcher.dispatch(data)
				).run()
			);
		}
	}
	
	public void addDispatcher(DataDispatcher<T> dispatcher) {
		Objects.requireNonNull(dispatcher);
		this.dispatchers.add(dispatcher);
	}
	
	public void removeDispatcher(DataDispatcher<T> dispatcher) {
		Objects.requireNonNull(dispatcher);
		this.dispatchers.remove(dispatcher);
	}
}
