package com.osh.rvs.mapper.partial;

import java.math.BigDecimal;
import java.util.List;

import com.osh.rvs.bean.partial.PartialOrderRecordEntity;

public interface PartialOrderRecordMapper {

	List<PartialOrderRecordEntity> searchPartials(PartialOrderRecordEntity entity);
	List<PartialOrderRecordEntity> searchEchelons(PartialOrderRecordEntity entity);
	List<PartialOrderRecordEntity> searchLevelModels(PartialOrderRecordEntity entity);
	PartialOrderRecordEntity getPeriodEdges(PartialOrderRecordEntity entity);

	List<PartialOrderRecordEntity> searchPartialsOnForeboard(PartialOrderRecordEntity entity);
	List<PartialOrderRecordEntity> searchLevelModelsOnForecast(PartialOrderRecordEntity entity);

	PartialOrderRecordEntity getTurnroundRateOfPartialInuse(PartialOrderRecordEntity entity);
	PartialOrderRecordEntity getTurnroundRateOfPartialSupply(PartialOrderRecordEntity condition);

	BigDecimal getAgreeHistoryML(PartialOrderRecordEntity lModel);
	String getOrderHillML(PartialOrderRecordEntity levelModel);
	Integer getNoGoodTimesML(PartialOrderRecordEntity levelModel);

	BigDecimal getAgreeHistoryP(PartialOrderRecordEntity partial);
	String getOrderHillP(PartialOrderRecordEntity partial);
	Integer getNoGoodTimesP(PartialOrderRecordEntity partial);
	List<PartialOrderRecordEntity> searchPartialsOnForeboardTopStick(PartialOrderRecordEntity entity);
}
