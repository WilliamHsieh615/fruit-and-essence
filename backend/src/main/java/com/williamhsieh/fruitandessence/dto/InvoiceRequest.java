package com.williamhsieh.fruitandessence.dto;

public class InvoiceRequest {

    private String invoiceCarrier; // 載具號碼
    private String invoiceDonationCode; // 捐贈碼
    private String companyTaxId; // 統一編號

    public String getInvoiceCarrier() {
        return invoiceCarrier;
    }

    public void setInvoiceCarrier(String invoiceCarrier) {
        this.invoiceCarrier = invoiceCarrier;
    }

    public String getInvoiceDonationCode() {
        return invoiceDonationCode;
    }

    public void setInvoiceDonationCode(String invoiceDonationCode) {
        this.invoiceDonationCode = invoiceDonationCode;
    }

    public String getCompanyTaxId() {
        return companyTaxId;
    }

    public void setCompanyTaxId(String companyTaxId) {
        this.companyTaxId = companyTaxId;
    }
}
