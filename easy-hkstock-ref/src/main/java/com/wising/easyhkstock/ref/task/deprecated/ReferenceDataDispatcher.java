package com.wising.easyhkstock.ref.task.deprecated;

import java.util.List;

public interface ReferenceDataDispatcher<T> {

	public void dispatch(List<T> data);
}
