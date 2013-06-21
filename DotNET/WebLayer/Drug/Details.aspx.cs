using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using Pharmacy.BusinessLayer.Data;
using Pharmacy.BusinessLayer.Logic;

namespace WebLayer.Drug
{
    public partial class Details : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (DateOfActionBox.Text == "" && !IsPostBack)
            {
                DateOfActionBox.Text = String.Format("{0:dd.MM.yyyy HH:mm}", DateTime.Now);
            }
            UpdateSuggestion();
        }

        private void Page_Error(object sender, EventArgs e)
        {
            Session["LastError"] = Server.GetLastError();
            Server.ClearError();
            Server.Transfer("~/Error.aspx");
        }

        private void UpdateSuggestion()
        {
            if (DrugService.RequiresReplenishment(GetPzn()))
            {
                SuggestionLabel.Visible = true;
                int replenishmentSuggestion = DrugService.GetReplenishmentSuggestion(GetPzn());
                SuggestionLabel.Text = String.Format("Requires replenishment of {0} items", replenishmentSuggestion);
            }
            else
            {
                SuggestionLabel.Visible = false;
            }
        }

        protected void WithdrawButton_Command(object sender, CommandEventArgs e)
        {
            if (!Page.IsValid)
            {
                return;
            }
            DrugService.Withdraw(GetPzn(), GetQuantity(), GetDateOfAction());
            UpdateData();
        }

        private Int32 GetPzn()
        {
            return Int32.Parse(Request.Params["pzn"]);
        }

        private Int32 GetQuantity()
        {
            return Int32.Parse(QuantityBox.Text);
        }

        private DateTime GetDateOfAction()
        {
            return parseDateTime(DateOfActionBox.Text);
        }

        private static DateTime parseDateTime(string dateAsString)
        {
            return DateTime.ParseExact(dateAsString, "dd.MM.yyyy HH:mm", CultureInfo.InvariantCulture);
        }

        private void UpdateData()
        {
            // update data
            DrugDetailsView.DataBind();
            PendingOrdersGridView.DataBind();
            UpdateSuggestion();
        }

        protected void RestockButton_Command(object sender, CommandEventArgs e)
        {
            if (!Page.IsValid)
            {
                return;
            }
            DrugService.Restock(GetPzn(), GetQuantity(), GetDateOfAction());
            UpdateData();
        }

        protected void ReplenishButton_Command(object sender, CommandEventArgs e)
        {
            if (!Page.IsValid)
            {
                return;
            }
            OrderService.InitiateReplenishment(GetPzn(), GetQuantity());
            UpdateData();
        }
    }
}