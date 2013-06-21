<%@ Page Title="C.Sharpe - Create Drug" Language="C#" MasterPageFile="~/Site.Master" AutoEventWireup="true" 
        CodeBehind="Create.aspx.cs" Inherits="WebLayer.Drug.Create" %>
    <asp:Content ID="Content1" ContentPlaceHolderID="ContentHolder" runat="server">
    <form id="DrugCreateForm" runat="server">
        <p class="label">PZN</p>
        <p class="value">
            <asp:TextBox ID="PZNBox" TextMode="Number" runat="server" />
            <asp:Button ID="prefillButton" Text="Prefill" runat="server" OnClick="prefillButton_Click" />
            <asp:RequiredFieldValidator ID="PZNBoxValidator" runat="server" 
                ControlToValidate="PZNBox" ErrorMessage="RequiredFieldValidator"
                EnableClientScript="false">
                <span class="error">PZN required</span>
            </asp:RequiredFieldValidator>
            <asp:RangeValidator ID="PZNNumberValidator" runat="server"
                ControlToValidate="PZNBox" ErrorMessage="RangeValidator"
                Type="Integer" MinimumValue="1" MaximumValue="99999999"
                EnableClientScript="false">
                <span class="error">Enter valid PZN (8 digits)</span>
            </asp:RangeValidator>
        </p>
        <p class="label">Name</p>
        <p class="value">
            <asp:TextBox ID="NameBox" runat="server"></asp:TextBox>
            <asp:RequiredFieldValidator ID="NameBoxValidator" runat="server" 
                ControlToValidate="NameBox" ErrorMessage="RequiredFieldValidator"
                EnableClientScript="false">
                <span class="error">Name required</span>
            </asp:RequiredFieldValidator>
        </p>
        <p class="label">Description</p>
        <p class="value">
            <asp:TextBox ID="DescriptionBox" runat="server" TextMode="MultiLine"></asp:TextBox>
        </p>
        <p class="form-footer">
            <asp:Button ID="SubmitBtn" runat="server" Text="Submit"
                onclick="SubmitBtn_Click" />
        </p>
    </form>
    <div class="result">
        <asp:Label ID="ResultLabel" runat="server"></asp:Label>
    </div>
</asp:Content>