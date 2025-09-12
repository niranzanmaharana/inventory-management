package com.niranzan.inventory.management.service;

import com.lowagie.text.DocumentException;
import com.niranzan.inventory.management.view.response.SalesTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {
    private final TemplateEngine templateEngine;

    public byte[] generateInvoicePdf(SalesTransactionResponse transaction) throws DocumentException, IOException {
        String html = null;
        try {
            // Generate simple HTML without Thymeleaf processing
            html = generateSimpleHtml(transaction);
            log.info("Generated HTML for PDF: {}", html.substring(0, Math.min(200, html.length())));

            // Convert HTML to PDF using Flying Saucer
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();

            // Set document from string
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);

            log.info("PDF generated successfully, size: {} bytes", outputStream.size());
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF: ", e);
            if (html != null) {
                log.error("HTML content that caused error: {}", html);
            }
            throw new DocumentException("Failed to generate PDF: " + e.getMessage());
        }
    }

    private String generateSimpleHtml(SalesTransactionResponse transaction) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'/>");
        html.append("<title>Invoice</title>");
        html.append("<style type='text/css'>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; font-size: 12px; }");
        html.append("h1 { font-size: 24px; margin-bottom: 20px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append("th, td { border: 1px solid #333; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f5f5f5; font-weight: bold; }");
        html.append(".total-row { font-weight: bold; background-color: #f9f9f9; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        html.append("<h1>INVOICE</h1>");

        // Invoice details
        html.append("<table>");
        html.append("<tr><td><strong>Invoice #:</strong></td><td>").append(escapeXml(transaction.getInvoiceNumber())).append("</td></tr>");
        html.append("<tr><td><strong>Order #:</strong></td><td>").append(escapeXml(transaction.getOrderNumber())).append("</td></tr>");
        html.append("<tr><td><strong>Date:</strong></td><td>").append(escapeXml(transaction.getFormattedTxDate())).append("</td></tr>");
        html.append("<tr><td><strong>Payment Method:</strong></td><td>").append(escapeXml(transaction.getPaymentMethod())).append("</td></tr>");
        html.append("</table>");

        // Customer details
        if (transaction.getCustomer() != null) {
            html.append("<h3>Bill To:</h3>");
            html.append("<p><strong>").append(escapeXml(transaction.getCustomer().getCustomerName())).append("</strong></p>");
            html.append("<p>Address: ").append(escapeXml(transaction.getCustomer().getAddress())).append("</p>");
            html.append("<p>Phone: ").append(escapeXml(transaction.getCustomer().getMobile())).append("</p>");
            html.append("<p>Email: ").append(escapeXml(transaction.getCustomer().getEmail())).append("</p>");
        }

        // Items table
        html.append("<h3>Items:</h3>");
        html.append("<table>");
        html.append("<tr><th>#</th><th>Product</th><th>Unit Price</th><th>Qty</th><th>Discount</th><th>Tax</th><th>Amount</th></tr>");

        if (transaction.getItems() != null) {
            for (int i = 0; i < transaction.getItems().size(); i++) {
                var item = transaction.getItems().get(i);
                html.append("<tr>");
                html.append("<td>").append(i + 1).append("</td>");
                html.append("<td>").append(escapeXml(item.getProductName())).append("</td>");
                html.append("<td>₹").append(escapeXml(item.getAmount() != null ? item.getAmount().toString() : "0.00")).append("</td>");
                html.append("<td>").append(escapeXml(item.getQuantity() != null ? item.getQuantity().toString() : "0")).append("</td>");
                html.append("<td>₹").append(escapeXml(item.getDiscountAmount() != null ? item.getDiscountAmount().toString() : "0.00")).append("</td>");
                html.append("<td>₹").append(escapeXml(item.getTaxAmount() != null ? item.getTaxAmount().toString() : "0.00")).append("</td>");
                html.append("<td>₹").append(escapeXml(item.getSubtotal() != null ? item.getSubtotal().toString() : "0.00")).append("</td>");
                html.append("</tr>");
            }
        }

        // Totals
        html.append("<tr><td colspan='6'><strong>Discount</strong></td><td>₹").append(escapeXml(transaction.getTotalDiscount() != null ? transaction.getTotalDiscount().toString() : "0.00")).append("</td></tr>");
        html.append("<tr><td colspan='6'><strong>Tax</strong></td><td>₹").append(escapeXml(transaction.getTotalTax() != null ? transaction.getTotalTax().toString() : "0.00")).append("</td></tr>");
        html.append("<tr class='total-row'><td colspan='6'><strong>Grand Total</strong></td><td>₹").append(escapeXml(transaction.getGrandTotal() != null ? transaction.getGrandTotal().toString() : "0.00")).append("</td></tr>");

        html.append("</table>");
        html.append("<p style='text-align: center; margin-top: 30px; font-style: italic;'>Thank you for doing business. Please visit again!</p>");
        html.append("</body></html>");

        return html.toString();
    }

    private String escapeXml(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
