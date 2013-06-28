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
                return GetAllStatistics(fromDate, toDate, db).ToList();
            }
        }

        private static IQueryable<DrugStatistic> GetAllStatistics(DateTime fromDate, DateTime toDate, PharmacyContainer db)
        {
            return from d in db.DrugSet
                    let tmpSumFrom = d.Events.Where(e => e.DateOfAction <= fromDate).Sum(e => e.Quantity)
                    let sumFrom = tmpSumFrom == null ? 0 : tmpSumFrom
                    let tmpSumTo = d.Events.Where(e => e.DateOfAction <= toDate).Sum(e => e.Quantity)
                    let sumTo = tmpSumTo == null ? 0 : tmpSumTo
                    select new DrugStatistic { Pzn = d.PZN, AverageInventoryLevel = (sumFrom + sumTo) / 2.0 };
        }

        public static DrugStatistic GetStatistic(Int32 pzn, DateTime fromDate, DateTime toDate)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                return GetAllStatistics(fromDate, toDate, db).Where(d => d.Pzn == pzn).First();
            }
        }
    }
}
