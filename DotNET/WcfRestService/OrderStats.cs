using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Activation;
using System.ServiceModel.Web;
using System.Text;
using Pharmacy.BusinessLayer.Logic;
using Pharmacy.BusinessLayer.Data;
using System.Web.Script.Serialization;
using System.IO;

namespace Pharmacy.RestLayer
{
    // Start the service and browse to http://<machine_name>:<port>/customers/help to view the service's generated help page
   	// Note: the Service will only answer with JSON, if JSON is inserted into the accept header.
    //       Otherwise the service will answer with in XML format
    //
    [ServiceContract]
    [AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.PerCall)]
    // NOTE: If the service is renamed, remember to update the global.asax.cs file
    public class OrderStats
    {

        /// <summary>
        /// Returns a list of order statistics
        /// </summary>
        /// <returns></returns>
        [WebGet(UriTemplate = "", ResponseFormat = System.ServiceModel.Web.WebMessageFormat.Json)]
        public ICollection<OrderStatistic> GetCollection()
        {
            return OrderStatisticsService.GetAllStatistics();
        }

        /// <summary>
        /// Returns a single order statistic
        /// </summary>
        /// <returns></returns>
        [WebGet(UriTemplate = "{orderIdAsString}", ResponseFormat = System.ServiceModel.Web.WebMessageFormat.Json)]
        public OrderStatistic GetSingle(String orderIdAsString)
        {
            Int32 orderId = Int32.Parse(orderIdAsString);

            ValidateOrderExists(orderId);

            return OrderStatisticsService.GetStatistic(orderId);
        }

        private static void ValidateOrderExists(Int32 orderId)
        {
            try
            {
                ReplenishmentOrder order = OrderService.GetOrder(orderId);
                if (order.State != OrderState.Finished)
                    throw new WebFaultException(System.Net.HttpStatusCode.NotFound);
            }
            catch
            {
                throw new WebFaultException(System.Net.HttpStatusCode.NotFound);
            }
        }
    }
}
