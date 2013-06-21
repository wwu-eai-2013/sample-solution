using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Web.Script.Serialization;
using Pharmacy.BusinessLayer.Data;

namespace Pharmacy.BusinessLayer.Logic
{
    public static class DrugInfoService
    {
        public static DrugInfo GetInfo(Int32 pzn)
        {
            String pznServiceHost = ConfigurationManager.AppSettings["pznServiceHost"];
            using (var client = new WebClient())
            {
                try
                {
                    var json = client.DownloadString(String.Format("http://{0}/drug/{1}", pznServiceHost, pzn));
                    var serializer = new JavaScriptSerializer();
                    return serializer.Deserialize<DrugInfo>(json);
                }
                catch (WebException)
                {
                    throw new ArgumentException("Could not retrieve info for drug with PZN: " + pzn);
                }

            }
        }
    }
}
