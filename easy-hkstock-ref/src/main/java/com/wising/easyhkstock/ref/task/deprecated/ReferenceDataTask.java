package com.wising.easyhkstock.ref.task.deprecated;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.wising.easyhkstock.ref.task.ReferenceDataBuilder;

//This class is not thread safe
public class ReferenceDataTask<T> implements Runnable {

	private ReferenceDataBuilder<T> builder;
	private List<ReferenceDataDispatcher<T>> dispatchers = new ArrayList<ReferenceDataDispatcher<T>>();
	
	public ReferenceDataTask(ReferenceDataBuilder<T> builder) {
		Validate.notNull(builder);
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
	
	public void addDispatcher(ReferenceDataDispatcher<T> dispatcher) {
		Validate.notNull(dispatcher);
		this.dispatchers.add(dispatcher);
	}
	
	public void removeDispatcher(ReferenceDataDispatcher<T> dispatcher) {
		Validate.notNull(dispatcher);
		this.dispatchers.remove(dispatcher);
	}
}
