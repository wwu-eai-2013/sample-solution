using System;
using System.ServiceModel.Activation;
using System.Web;
using System.Web.Routing;
using Pharmacy.BusinessLayer.Data;

namespace Pharmacy.RestLayer
{
    public class Global : HttpApplication
    {
        void Application_Start(object sender, EventArgs e)
        {
            RegisterRoutes();
        }

        /// <summary>
        /// Register the URIs as Routes at the .NET-Application
        /// </summary>
        private void RegisterRoutes()
        {
            // This is the route for the statistics
            RouteTable.Routes.Add(new ServiceRoute("drugstats", new WebServiceHostFactory(), typeof(DrugStats)));
            RouteTable.Routes.Add(new ServiceRoute("orderstats", new WebServiceHostFactory(), typeof(OrderStats)));
        }
    }
}
