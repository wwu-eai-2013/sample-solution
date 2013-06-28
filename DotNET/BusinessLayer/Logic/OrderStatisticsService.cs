using System;
using System.Collections.Generic;
using System.Data.Objects.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Pharmacy.BusinessLayer.Data;

namespace Pharmacy.BusinessLayer.Logic
{
    public static class OrderStatisticsService
    {
        public static ICollection<OrderStatistic> GetAllStatistics()
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                return GetAllStatistics(db).ToList();
            }
        }

        private static IQueryable<OrderStatistic> GetAllStatistics(PharmacyContainer db)
        {
            return from o in db.ReplenishmentOrderSet
                   where o.State == OrderState.Finished
                   select new OrderStatistic { orderId = o.Id, deviation = SqlFunctions.DateDiff("ss", o.ExpectedDelivery, o.ActualDelivery) };
        }

        public static OrderStatistic GetStatistic(int orderId)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                return GetAllStatistics(db).Where(o => o.orderId == orderId).First();
            }
        }
    }
}
