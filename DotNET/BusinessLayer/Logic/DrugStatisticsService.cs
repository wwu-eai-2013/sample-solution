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
        public static ICollection<DrugStatistic> GetAllStatistics(DateTime from, DateTime to)
        {
            return new List<DrugStatistic>();
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
