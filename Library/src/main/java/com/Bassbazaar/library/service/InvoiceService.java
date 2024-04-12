package com.Bassbazaar.library.service;

public interface InvoiceService
{
    byte[] generateInvoice(Long orderId);
}
