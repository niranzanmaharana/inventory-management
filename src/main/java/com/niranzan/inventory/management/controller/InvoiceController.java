package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.config.AppMessage;
import com.niranzan.inventory.management.enums.AppMessageParameter;
import com.niranzan.inventory.management.enums.AppPages;
import com.niranzan.inventory.management.service.InvoiceService;
import com.niranzan.inventory.management.service.PdfService;
import com.niranzan.inventory.management.utils.MessageFormatUtil;
import com.niranzan.inventory.management.view.response.ApiResponse;
import com.niranzan.inventory.management.view.response.SalesTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final PdfService pdfService;

    @GetMapping("/invoice-list")
    public String dashboard(Model model) {
        return AppPages.INVOICE_LIST_PATH.getPath();
    }

    @GetMapping("/view/{invoiceNumber}")
    public String viewInvoice(@PathVariable String invoiceNumber, Model model) {
        try {
            SalesTransactionResponse salesTransaction = invoiceService.findByInvoiceNumber(invoiceNumber);
            model.addAttribute("transaction", salesTransaction);
        } catch (Exception e) {
            model.addAttribute(AppMessageParameter.ERROR_PARAM_NM.getName(), MessageFormatUtil.format(AppMessage.MSG_INVOICE_NOT_FOUND_WITH_INVOICE_NUMBER.getMessage(), invoiceNumber));
        }
        return AppPages.INVOICE_PATH.getPath();
    }

    @GetMapping("/invoice-number/{invoiceNumber}")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<SalesTransactionResponse>>> getInvoiceByInvoiceNumber(@PathVariable String invoiceNumber) {
        try {
            SalesTransactionResponse salesTransaction = invoiceService.findByInvoiceNumber(invoiceNumber);
            return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved invoice successfully", List.of(salesTransaction)));
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error while retrieving the invoice: " + exception.getMessage(), null));
        }
    }

    @GetMapping("/order-number/{orderNumber}")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<SalesTransactionResponse>>> getInvoiceByOrderNumber(@PathVariable String orderNumber) {
        try {
            SalesTransactionResponse salesTransaction = invoiceService.findByOrderNumber(orderNumber);
            return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved invoice successfully", List.of(salesTransaction)));
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error while retrieving the invoice: " + exception.getMessage(), null));
        }
    }

    @GetMapping("/customer-id/{customerId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<SalesTransactionResponse>>> getInvoiceByInvoiceNumber(@PathVariable Long customerId) {
        try {
            List<SalesTransactionResponse> salesTransactions = invoiceService.findByCustomerId(customerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved invoice successfully", salesTransactions));
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error while retrieving the invoice: " + exception.getMessage(), null));
        }
    }

    @GetMapping("/date-range")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<SalesTransactionResponse>>> getInvoicesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            List<SalesTransactionResponse> salesTransactions = invoiceService.findByDateRange(start, end);
            return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved invoices successfully", salesTransactions));
        } catch (Exception exception) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Error while retrieving invoices: " + exception.getMessage(), null));
        }
    }

    @GetMapping("/download/{invoiceNumber}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable String invoiceNumber) {
        try {
            SalesTransactionResponse transaction = invoiceService.findByInvoiceNumber(invoiceNumber);
            byte[] pdfBytes = pdfService.generateInvoicePdf(transaction);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice-" + invoiceNumber + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error generating PDF for invoice " + invoiceNumber + ": " + e.getMessage());
            e.printStackTrace();

            // Return a more specific error response
            return ResponseEntity.status(500)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/test-pdf")
    @ResponseBody
    public ResponseEntity<byte[]> testPdf() {
        try {
            // Create a simple test PDF
            String html = "<html><body><h1>Test PDF</h1><p>This is a test PDF generation.</p></body></html>";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(("Test PDF Error: " + e.getMessage()).getBytes());
        }
    }
}
