package com.wising.easyhkstock.ccass.task;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import com.wising.easyhkstock.ccass.domain.SnapshotDetail;
import com.wising.easyhkstock.ccass.domain.SnapshotSummary;
import com.wising.easyhkstock.ccass.domain.repository.SnapshotDetailRepository;
import com.wising.easyhkstock.ccass.domain.repository.SnapshotSummaryRepository;
import com.wising.easyhkstock.common.task.DataDispatcher;

public class MongoDispatcher implements DataDispatcher<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> {

	private SnapshotDetailRepository detailRepository;
	private SnapshotSummaryRepository summaryRepository;
	
	public MongoDispatcher(SnapshotSummaryRepository summaryRepository, SnapshotDetailRepository detailRepository) {
		
		Validate.notNull(summaryRepository);
		Validate.notNull(detailRepository);
		this.summaryRepository = summaryRepository;
		this.detailRepository = detailRepository;
	}
	
	
	@Override
	public void dispatch(List<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> data) {
		Validate.notNull(data);
		List<SnapshotSummary> summary = new ArrayList<SnapshotSummary>(data.size());
		List<SnapshotDetail> detail = new ArrayList<SnapshotDetail>(data.size());
		data.forEach(item -> {
			summary.add(item.getKey());
			detail.add(item.getValue());
		});
		summaryRepository.save(summary);
		detailRepository.save(detail);
	}
}
