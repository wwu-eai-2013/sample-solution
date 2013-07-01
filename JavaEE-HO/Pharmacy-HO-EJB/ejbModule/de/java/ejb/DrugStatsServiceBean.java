package de.java.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import de.java.web.stats.drug.DrugStatistic;
import de.java.web.stats.drug.DrugStatsResource;

@Stateless
public class DrugStatsServiceBean implements DrugStatsService {

  private DrugStatsResource javaDrugStats;
  private DrugStatsResource csharpeDrugStats;

  @PostConstruct
  public void initialise() {
    RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    javaDrugStats = ProxyFactory.create(DrugStatsResource.class, Subsidiaries.JAVA_HOST);
    csharpeDrugStats = ProxyFactory.create(DrugStatsResource.class, Subsidiaries.CSHARPE_HOST);
  }

  public List<AggregatedDrugStatistic> getStats() {
    Map<Integer, DrugStatistic> statisticsFromJava = mapPznToStatistics(javaDrugStats.getAllStatistics());
    Map<Integer, DrugStatistic> statisticsFromCsharpe = mapPznToStatistics(csharpeDrugStats.getAllStatistics());

    @SuppressWarnings("unchecked")
    List<Integer> uniquePzns = new ArrayList<>(generateDistinctSetOf(statisticsFromJava, statisticsFromCsharpe));
    Collections.sort(uniquePzns);

    ArrayList<AggregatedDrugStatistic> result = new ArrayList<>();
    for (Integer pzn : uniquePzns) {
      Collection<DrugStatistic> statisticsForPzn = new ArrayList<>();

      if (statisticsFromJava.containsKey(pzn)) {
        statisticsForPzn.add(statisticsFromJava.get(pzn));
      }
      if (statisticsFromCsharpe.containsKey(pzn)) {
        statisticsForPzn.add(statisticsFromCsharpe.get(pzn));
      }

      result.add(new AggregatedDrugStatistic(statisticsForPzn));
    }
    return result;
  }

  private Map<Integer, DrugStatistic> mapPznToStatistics(
      Collection<DrugStatistic> allStatistics) {
    Map<Integer, DrugStatistic> mapped = new HashMap<>();
    for (DrugStatistic statistic : allStatistics) {
      mapped.put(statistic.getPzn(), statistic);
    }
    return mapped;
  }

  private Set<Integer> generateDistinctSetOf(
      @SuppressWarnings("unchecked") Map<Integer, ?>... integerIndexedMaps) {
    Set<Integer> distinctIntegerKeys = new HashSet<>();
    for (Map<Integer, ?> map : integerIndexedMaps) {
      distinctIntegerKeys.addAll(map.keySet());
    }
    return distinctIntegerKeys;
  }

  @Override
  public AggregatedDrugStatistic getStatistic(int pzn) {
    return null;
  }

}
