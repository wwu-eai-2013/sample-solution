using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Activation;
using System.ServiceModel.Web;
using System.Text;
using Pharmacy.BusinessLayer.Data;
using Pharmacy.BusinessLayer.Logic;

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
    public class DrugStats
    {

        private static Int32 DATE_OFFSET = -30;

        /// <summary>
        /// Returns a list of DrugStatistic-Objects
        /// </summary>
        [WebGet(UriTemplate = "", ResponseFormat = WebMessageFormat.Json)]
        public ICollection<DrugStatistic> GetCollection()
        {
            DateTime to = DateTime.Now;
            DateTime from = to.AddDays(DATE_OFFSET);
            return DrugStatisticsService.GetAllStatistics(from, to);
        }

        /// <summary>
        /// Returns single DrugStatistic-Object
        /// </summary>
        [WebGet(UriTemplate = "{pznAsString}", ResponseFormat = WebMessageFormat.Json)]
        public DrugStatistic GetSingle(String pznAsString)
        {
            Int32 pzn = Int32.Parse(pznAsString);

            ValidateDrugExists(pzn);

            DateTime to = DateTime.Now;
            DateTime from = to.AddDays(DATE_OFFSET);

            return DrugStatisticsService.GetStatistic(pzn, from, to);
        }

        private static void ValidateDrugExists(Int32 pzn)
        {
            try
            {
                DrugService.GetDrug(pzn);
            }
            catch
            {
                throw new WebFaultException(System.Net.HttpStatusCode.NotFound);
            }
        }

    }
}
