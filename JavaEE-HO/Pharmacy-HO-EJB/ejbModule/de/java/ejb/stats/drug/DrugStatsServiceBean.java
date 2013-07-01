package de.java.ejb.stats.drug;

import static de.java.ejb.Subsidiaries.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

import org.jboss.resteasy.client.ClientResponseFailure;
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
    javaDrugStats = ProxyFactory.create(DrugStatsResource.class, JAVA_HOST);
    csharpeDrugStats = ProxyFactory.create(DrugStatsResource.class, CSHARPE_HOST);
  }

  public List<AggregatedDrugStatistic> getStats() {
    Map<Integer, WrappedDrugStatistic> statisticsFromJava = mapPznToStatistics(JAVA_DISPLAY_NAME, javaDrugStats.getAllStatistics());
    Map<Integer, WrappedDrugStatistic> statisticsFromCsharpe = mapPznToStatistics(CSHARPE_DISPLAY_NAME, csharpeDrugStats.getAllStatistics());

    @SuppressWarnings("unchecked")
    List<Integer> uniquePzns = new ArrayList<>(generateDistinctSetOf(statisticsFromJava, statisticsFromCsharpe));
    Collections.sort(uniquePzns);

    ArrayList<AggregatedDrugStatistic> result = new ArrayList<>();
    for (Integer pzn : uniquePzns) {
      Collection<WrappedDrugStatistic> statisticsForPzn = new ArrayList<>();

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

  private Map<Integer, WrappedDrugStatistic> mapPznToStatistics(
      String fromSubsidiary, Collection<DrugStatistic> allStatistics) {
    Map<Integer, WrappedDrugStatistic> mapped = new HashMap<>();
    for (DrugStatistic statistic : allStatistics) {
      mapped.put(statistic.getPzn(), new WrappedDrugStatistic(fromSubsidiary, statistic));
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
    Collection<WrappedDrugStatistic> statistics = new ArrayList<>();

    try {
      statistics.add(new WrappedDrugStatistic(JAVA_DISPLAY_NAME, javaDrugStats.getStatistic(pzn)));
    } catch (ClientResponseFailure e) { }

    try {
      statistics.add(new WrappedDrugStatistic(CSHARPE_DISPLAY_NAME, csharpeDrugStats.getStatistic(pzn)));
    } catch (ClientResponseFailure e) { }

    validateThatStatisticsAreAvailableFor(pzn, statistics);
    
    return new AggregatedDrugStatistic(statistics);
  }

  private void validateThatStatisticsAreAvailableFor(int pzn,
      Collection<WrappedDrugStatistic> statistics) {
    if (statistics.isEmpty()) {
      throw new EJBException(String.format("Drug with pzn %s not available at any subsidiary", pzn));
    }
  }

}
