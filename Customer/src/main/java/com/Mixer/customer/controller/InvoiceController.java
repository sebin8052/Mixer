package com.Mixer.customer.controller;

import com.Mixer.library.service.InvoiceService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/downloadInvoice")
public class InvoiceController
{


private InvoiceService  invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Long orderId) {
        byte[] pdfContent = invoiceService.generateInvoice(orderId);
        ByteArrayResource resource = new ByteArrayResource(pdfContent);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfContent.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

}
