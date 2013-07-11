<%@ Page Title="C.Sharpe - Replenishment Order List" Language="C#" MasterPageFile="~/Site.Master" AutoEventWireup="true" CodeBehind="Details.aspx.cs" Inherits="WebLayer.ReplenishmentOrder.Details" %>
<%@ Import Namespace="Pharmacy.BusinessLayer.Data" %>
<asp:Content ID="Content1" ContentPlaceHolderID="ContentHolder" runat="server">
<form id="OrderDetailsForm" runat="server">
    <asp:ObjectDataSource ID="OrderDataSource" runat="server"
        SelectMethod="GetOrder"
        TypeName="Pharmacy.BusinessLayer.Logic.OrderService"
        DataObjectTypeName="Pharmacy.BusinessLayer.Data.ReplenishmentOrder">
        <SelectParameters>
            <asp:QueryStringParameter Name="ID" QueryStringField="id" Type="Int32" />
        </SelectParameters>
    </asp:ObjectDataSource>
    <asp:DetailsView ID="OrderDetailsView" runat="server" AutoGenerateRows="false"
        BackColor="White" BorderColor="#CCCCCC" BorderStyle="None" BorderWidth="1px" CellPadding="4"
        ForeColor="Black" GridLines="Horizontal" Height="50px" 
        DataSourceID="OrderDataSource">
        <Fields>
            <asp:BoundField DataField="Id" HeaderText="Replenishment Order" ReadOnly="true" SortExpression="Id" /> 
            <asp:BoundField DataField="State" HeaderText="State" ReadOnly="true" SortExpression="State" />
            <asp:TemplateField HeaderText="Expected delivery date">
                <ItemTemplate>
                    <asp:Label ID="ExpectedDeliveryLabel" runat="server"
                        Text='<%# Eval("ExpectedDelivery", "{0:dd.MM.yyyy HH:mm}") %>' 
                        Visible='<%# ((OrderState)Eval("State")) != OrderState.Posting %>' />
                </ItemTemplate>
            </asp:TemplateField>
            <asp:TemplateField HeaderText="Actual delivery date">
                <ItemTemplate>
                    <asp:Label ID="ActualDeliveryLabel" runat="server"
                        Text='<%# Eval("ActualDelivery", "{0:dd.MM.yyyy HH:mm}") %>' 
                        Visible='<%# ((OrderState)Eval("State")) != OrderState.Ordered %>' />
                    <asp:TextBox ID="ActualDeliveryBox"
                        TextMode="DateTime"
                        Text='<%# String.Format("{0:dd.MM.yyyy HH:mm}", DateTime.Now) %>'
                        Visible='<%# ((OrderState)Eval("State")) == OrderState.Ordered %>'
                        runat="server" />
                    <asp:RequiredFieldValidator ID="RequiredActualDelivery" ControlToValidate="ActualDeliveryBox"
                        EnableClientScript="false" runat="server">
                        Actual delivery date required
                    </asp:RequiredFieldValidator>
                    <asp:RegularExpressionValidator ID="ValidActualDelivery" ControlToValidate="ActualDeliveryBox"
                        EnableClientScript="false" runat="server"
                        ValidationExpression="\d{2}.\d{2}.\d{4} \d{2}:\d{2}">
                        Invalid date (e.g. 24.12.2013 18:32)
                    </asp:RegularExpressionValidator>
                </ItemTemplate>
            </asp:TemplateField>
            <asp:TemplateField>
                <ItemTemplate>
                    <asp:Button ID="NextStateButton" runat="server" OnCommand="NextStateButton_Command"
                        Text='<%# "Proceed to " + ((OrderState)Eval("State")).Next() %>'
                        Enabled='<%# ((OrderState)Eval("State")) == OrderState.Ordered %>'
                        Visible='<%# ((OrderState)Eval("State")) == OrderState.Ordered %>' />
                </ItemTemplate>
            </asp:TemplateField>
        </Fields>
    </asp:DetailsView>
       <asp:ObjectDataSource ID="PositionDataSource" runat="server"
        SelectMethod="GetPositionsForOrder"
        TypeName="Pharmacy.BusinessLayer.Logic.OrderService"
        DataObjectTypeName="Pharmacy.BusinessLayer.Data.Position">
        <SelectParameters>
            <asp:QueryStringParameter Name="ID" QueryStringField="id" Type="Int32" />
        </SelectParameters>
    </asp:ObjectDataSource>
    <asp:GridView ID="DrugGridView" runat="server" AutoGenerateColumns="False" DataSourceID="PositionDataSource">
        <Columns>
            <asp:BoundField DataField="Drug.PZN" HeaderText="PZN" ReadOnly="True" SortExpression="Drug.PZN" />
            <asp:BoundField DataField="Drug.Name" HeaderText="Name" SortExpression="Drug.Name" />
            <asp:BoundField DataField="Quantity" HeaderText="Quantity" SortExpression="Quantity" />
            <asp:TemplateField>
                <ItemTemplate>
                    <asp:HyperLink ID="DrugDetailsLink" runat="server" Text="Details"
                        NavigateUrl='<%# String.Format("~/Drug/Details.aspx?pzn={0}", Eval("Drug.PZN")) %>' />
                </ItemTemplate>
            </asp:TemplateField>
        </Columns>    
    </asp:GridView>
</form>
</asp:Content>
