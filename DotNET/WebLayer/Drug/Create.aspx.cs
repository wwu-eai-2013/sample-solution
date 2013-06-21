using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using Pharmacy.BusinessLayer.Logic;
using Pharmacy.BusinessLayer.Data;

namespace WebLayer.Drug
{
    public partial class Create : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {

        }

        protected void SubmitBtn_Click(object sender, EventArgs e)
        {
            // because validation still invoke this method
            if (!Page.IsValid)
                return;
            try
            {
                // pzn is validated in form
                int pzn = Int32.Parse(PZNBox.Text);

                Pharmacy.BusinessLayer.Data.Drug result =
                    Pharmacy.BusinessLayer.Logic.DrugService.CreateDrug(pzn, NameBox.Text, DescriptionBox.Text);
                ResultLabel.Text = String.Format("Drug '{0}' created.", result.PZN);
                ResultLabel.CssClass = "success";
                PZNBox.Text = "";
                NameBox.Text = "";
                DescriptionBox.Text = "";
            }
            catch (ArgumentException ex)
            {
                ResultLabel.Text = String.Format("Drug not created: {0}", ex.Message);
                ResultLabel.CssClass = "error";
            }
        }

        protected void prefillButton_Click(object sender, EventArgs e)
        {
            int parseResult;
            if (!Int32.TryParse(PZNBox.Text, out parseResult))
            {
                return;
            }

            try
            {
                DrugInfo drugInfo = DrugInfoService.GetInfo(Int32.Parse(PZNBox.Text));
                NameBox.Text = drugInfo.Name;
                DescriptionBox.Text = drugInfo.Description;
                // to make sure errors are not shown by accident
                Validate();
                ResultLabel.Text = "Prefilled data for drug.";
                ResultLabel.CssClass = "success";
            }
            catch (ArgumentException ex)
            {
                ResultLabel.Text = ex.Message;
                ResultLabel.CssClass = "error";
            }


        }

    }
}