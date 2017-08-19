package com.wising.easyhkstock.common.task;

import java.util.List;

public interface DataDispatcher<T> {

	public void dispatch(List<T> data);
}
