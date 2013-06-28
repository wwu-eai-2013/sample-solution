using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Pharmacy.BusinessLayer.Data;

namespace Pharmacy.BusinessLayer.Logic
{
    public static class DrugStatisticsService
    {
        public static ICollection<DrugStatistic> GetAllStatistics(DateTime fromDate, DateTime toDate)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                Dictionary<Int32, Int32> sumsFrom = GetSumOfQuantitiesPerPzn(fromDate, db);
                Dictionary<Int32, Int32> sumsTo = GetSumOfQuantitiesPerPzn(toDate, db);
                ICollection<Drug> allDrugs = DrugService.GetAllDrugs();

                ICollection<DrugStatistic> result = new List<DrugStatistic>(allDrugs.Count);
                foreach (Drug drug in allDrugs)
                {
                    result.Add(CreateDrugStatistic(drug.PZN, sumsFrom, sumsTo));
                }
                return result;
            }
        }

        private static Dictionary<Int32, Int32> GetSumOfQuantitiesPerPzn(DateTime before, PharmacyContainer db)
        {
            var sums = from d in db.DrugSet
                       let sumUpToBefore = d.Events.Where(e => e.DateOfAction <= before).Sum(e => e.Quantity)
                       let sum = sumUpToBefore == null ? 0 : sumUpToBefore
                       select new { Pzn = d.PZN, Sum = sum };
            Dictionary<Int32, Int32> result = new Dictionary<Int32, Int32>();
            foreach (var tuple in sums)
            {
                System.Diagnostics.Debug.WriteLine(String.Format("pzn: {0}, sum: {1}, date: {2}", tuple.Pzn, tuple.Sum, before));
                result.Add(tuple.Pzn, tuple.Sum);
            }
            return result;
        }

        private static DrugStatistic CreateDrugStatistic(Int32 pzn, Dictionary<Int32, Int32> sumsFrom, Dictionary<Int32, Int32> sumsTo)
        {
            long inventoryAtFrom = sumsFrom[pzn];
            long inventoryAtTo = sumsTo[pzn];
            double averageInventoryLevel = (inventoryAtFrom + inventoryAtTo) / 2.0;
            return new DrugStatistic { Pzn = pzn, AverageInventoryLevel = averageInventoryLevel };
        }

        public static DrugStatistic GetStatistic(Int32 pzn, DateTime from, DateTime to)
        {
            return new DrugStatistic
            {
                Pzn = pzn,
                AverageInventoryLevel = 42.5
            };
        }
    }
}
