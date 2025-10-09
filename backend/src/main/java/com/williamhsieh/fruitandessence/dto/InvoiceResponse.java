package com.williamhsieh.fruitandessence.dto;

import java.time.LocalDateTime;

public class InvoiceResponse {

    private Integer invoiceId;
    private String invoiceNumber;
    private String invoiceCarrier;
    private String invoiceDonationCode;
    private String companyTaxId;
    private Boolean issued;
    private LocalDateTime issuedDate;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

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

    public Boolean getIssued() {
        return issued;
    }

    public void setIssued(Boolean issued) {
        this.issued = issued;
    }

    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
