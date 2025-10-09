package com.williamhsieh.fruitandessence.model;

import java.time.LocalDateTime;

public class Invoice {

    private Integer invoiceId;
    private Integer orderId;
    private String invoiceNumber; // 發票號碼
    private String invoiceCarrier; // 載具號碼
    private String invoiceDonationCode; // 捐贈碼
    private String companyTaxId; // 統一編號
    private Boolean issued; // 是否已開立
    private LocalDateTime issuedDate; // 開立日期
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
