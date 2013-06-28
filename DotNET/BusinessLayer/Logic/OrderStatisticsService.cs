using System;
using System.Collections.Generic;
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
            return new List<OrderStatistic>();
        }


        public static OrderStatistic GetStatistic(int orderId)
        {
            return new OrderStatistic
            {
                orderId = orderId,
                deviation = 42
            };
        }
    }
}
