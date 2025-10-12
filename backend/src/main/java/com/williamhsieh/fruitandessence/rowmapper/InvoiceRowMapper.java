package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.model.Invoice;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InvoiceRowMapper implements RowMapper<Invoice> {

    @Override
    public Invoice mapRow(ResultSet resultSet, int i) throws SQLException {

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(resultSet.getInt("invoice_id"));
        invoice.setOrderId(resultSet.getInt("order_id"));
        invoice.setInvoiceNumber(resultSet.getString("invoice_number"));
        invoice.setInvoiceCarrier(resultSet.getString("invoice_carrier"));
        invoice.setInvoiceDonationCode(resultSet.getString("invoice_donation_code"));
        invoice.setCompanyTaxId(resultSet.getString("company_tax_id"));
        invoice.setIssued(resultSet.getBoolean("issued"));
        invoice.setIssuedDate(resultSet.getTimestamp("issued_date").toLocalDateTime());
        invoice.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        invoice.setLastModifiedDate(resultSet.getTimestamp("last_modified_date").toLocalDateTime());

        return invoice;
    }
}
