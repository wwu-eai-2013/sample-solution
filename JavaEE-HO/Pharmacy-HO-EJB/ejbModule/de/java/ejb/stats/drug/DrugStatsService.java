package de.java.ejb.stats.drug;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface DrugStatsService {

  List<AggregatedDrugStatistic> getStats();

  AggregatedDrugStatistic getStatistic(int pzn);

}
