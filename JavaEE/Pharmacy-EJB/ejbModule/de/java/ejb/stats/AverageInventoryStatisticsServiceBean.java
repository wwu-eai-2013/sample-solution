package de.java.ejb.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.java.domain.Drug;
import de.java.ejb.DrugService;
import de.java.web.api.DrugStatistic;

@Stateless
public class AverageInventoryStatisticsServiceBean implements
    AverageInventoryStatisticsService {

  @PersistenceContext
  private EntityManager em;

  @EJB
  private DrugService drugService;

  @Override
  public Collection<DrugStatistic> getAllStats(Date from, Date to) {
    Map<Integer, Long> sumsFrom = getSumOfQuantitiesPerPzn(from);
    Map<Integer, Long> sumsTo = getSumOfQuantitiesPerPzn(to);
    Collection<Drug> allDrugs = drugService.getAllDrugs();

    Collection<DrugStatistic> result = new ArrayList<>(allDrugs.size());
    for (Drug drug : allDrugs) {
      result.add(createDrugStatistic(drug, sumsFrom, sumsTo));
    }
    return result;
  }

  /**
   * @param before
   * @return map of pzn mapped to sum for all drugs that had events before
   */
  private Map<Integer, Long> getSumOfQuantitiesPerPzn(Date before) {
    List<Object[]> resultList = em
        .createQuery(
            "select d.pzn as pzn, sum(e.quantity) as inventoryLevel\n"
                + "from Drug d\n"
                + "join d.events e\n"
                + "where e.dateOfAction <= :upToDate\n"
                + "group by d.pzn\n")
        .setParameter("upToDate", before)
        .getResultList();
    Map<Integer, Long> results = new HashMap<>();
    for (Object[] result : resultList) {
      results.put((Integer) result[0], (Long) result[1]);
    }
    return results;
  }

  private DrugStatistic createDrugStatistic(Drug drug,
      Map<Integer, Long> sumsFrom, Map<Integer, Long> sumsTo) {
    int pzn = drug.getPzn();
    long inventoryAtFrom = sumOrZero(sumsFrom, pzn);
    long inventoryAtTo = sumOrZero(sumsTo, pzn);
    long averageInventoryLevel = (inventoryAtFrom + inventoryAtTo) / 2;
    DrugStatistic drugStatistic = new DrugStatistic(pzn, averageInventoryLevel);
    return drugStatistic;
  }

  private long sumOrZero(Map<Integer, Long> sums, int pzn) {
    return sums.containsKey(pzn) ? sums.get(pzn) : 0;
  }

}
