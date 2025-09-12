$(document).ready(function() {
    let salesTrendChart = null;
    let paymentMethodChart = null;
    let currentData = {
        sales: [],
        analytics: {}
    };

    // Initialize date pickers
    initializeDatePickers();

    // Event handlers
    $('#filterBtn').on('click', function() {
        loadSalesReport();
    });

    $('#resetBtn').on('click', function() {
        resetFilters();
    });

    $('#exportExcel').on('click', function() {
        exportToExcel();
    });

    $('#printReport').on('click', function() {
        printReport();
    });

    // Load initial data
    loadSalesReport();

    function initializeDatePickers() {
        // Set default date range (last 30 days)
        const endDate = new Date();
        const startDate = new Date();
        startDate.setDate(startDate.getDate() - 30);

        $('#startDate').val(formatDate(startDate));
        $('#endDate').val(formatDate(endDate));
    }

    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    function loadSalesReport() {
        showLoading();
        
        const formData = {
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val(),
            customerId: $('#customer').val()
        };

        $.ajax({
            url: '/report/sales-report/filter',
            type: 'GET',
            data: formData,
            success: function(response) {
                currentData = response;
                updateAnalyticsCards(response.analytics);
                updateSalesTable(response.sales);
                updateCharts(response.sales);
                hideLoading();
            },
            error: function(xhr, status, error) {
                console.error('Error loading sales report:', error);
                showError('Failed to load sales report. Please try again.');
                hideLoading();
            }
        });
    }

    function updateAnalyticsCards(analytics) {
        $('#totalRevenue').text('₹' + formatCurrency(analytics.totalRevenue || 0));
        $('#totalTransactions').text(analytics.totalTransactions || 0);
        $('#averageOrderValue').text('₹' + formatCurrency(analytics.averageOrderValue || 0));
        $('#totalTax').text('₹' + formatCurrency(analytics.totalTax || 0));
    }

    function updateSalesTable(sales) {
        const tbody = $('#salesTableBody');
        tbody.empty();

        if (sales.length === 0) {
            tbody.append(`
                <tr>
                    <td colspan="11" class="text-center empty-state">
                        <i class="bi bi-inbox"></i>
                        <h5>No sales found</h5>
                        <p>No sales transactions found for the selected filters.</p>
                    </td>
                </tr>
            `);
            updateTableTotals({});
            return;
        }

        let subtotalSum = 0;
        let taxSum = 0;
        let discountSum = 0;
        let grandTotalSum = 0;

        sales.forEach((sale, index) => {
            const subtotal = parseFloat(sale.grandTotal || 0) - parseFloat(sale.totalTax || 0) + parseFloat(sale.totalDiscount || 0);
            const tax = parseFloat(sale.totalTax || 0);
            const discount = parseFloat(sale.totalDiscount || 0);
            const grandTotal = parseFloat(sale.grandTotal || 0);

            subtotalSum += subtotal;
            taxSum += tax;
            discountSum += discount;
            grandTotalSum += grandTotal;

            const row = `
                <tr>
                    <td>${index + 1}</td>
                    <td><strong>${sale.invoiceNumber || 'N/A'}</strong></td>
                    <td>${sale.orderNumber || 'N/A'}</td>
                    <td>${sale.customer ? sale.customer.customerName : 'N/A'}</td>
                    <td>${formatDateString(sale.formattedTxDate)}</td>
                    <td><span class="badge bg-primary">${sale.paymentMethod || 'N/A'}</span></td>
                    <td class="currency">₹${formatCurrency(subtotal)}</td>
                    <td class="currency">₹${formatCurrency(tax)}</td>
                    <td class="currency currency-negative">₹${formatCurrency(discount)}</td>
                    <td class="currency currency-positive"><strong>₹${formatCurrency(grandTotal)}</strong></td>
                    <td>
                        <div class="action-buttons">
                            <a href="/invoice/view/${sale.invoiceNumber}" target="_blank" 
                               class="btn btn-sm btn-outline-primary btn-action" title="View Invoice">
                                <i class="bi bi-eye"></i>
                            </a>
                            <a href="/invoice/download/${sale.invoiceNumber}" 
                               class="btn btn-sm btn-outline-success btn-action" title="Download PDF">
                                <i class="bi bi-download"></i>
                            </a>
                        </div>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });

        updateTableTotals({
            subtotal: subtotalSum,
            tax: taxSum,
            discount: discountSum,
            grandTotal: grandTotalSum
        });
    }

    function updateTableTotals(totals) {
        $('#subtotalTotal').text('₹' + formatCurrency(totals.subtotal || 0));
        $('#taxTotal').text('₹' + formatCurrency(totals.tax || 0));
        $('#discountTotal').text('₹' + formatCurrency(totals.discount || 0));
        $('#grandTotal').text('₹' + formatCurrency(totals.grandTotal || 0));
    }

    function updateCharts(sales) {
        updateSalesTrendChart(sales);
        updatePaymentMethodChart(sales);
    }

    function updateSalesTrendChart(sales) {
        const ctx = document.getElementById('salesTrendChart').getContext('2d');
        
        if (salesTrendChart) {
            salesTrendChart.destroy();
        }

        // Group sales by date
        const salesByDate = {};
        sales.forEach(sale => {
            const date = new Date(sale.transactionDate).toLocaleDateString();
            if (!salesByDate[date]) {
                salesByDate[date] = 0;
            }
            salesByDate[date] += parseFloat(sale.grandTotal || 0);
        });

        const dates = Object.keys(salesByDate).sort();
        const amounts = dates.map(date => salesByDate[date]);

        salesTrendChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: dates,
                datasets: [{
                    label: 'Sales Amount (₹)',
                    data: amounts,
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    tension: 0.1,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return '₹' + formatCurrency(value);
                            }
                        }
                    }
                }
            }
        });
    }

    function updatePaymentMethodChart(sales) {
        const ctx = document.getElementById('paymentMethodChart').getContext('2d');
        
        if (paymentMethodChart) {
            paymentMethodChart.destroy();
        }

        // Group sales by payment method
        const salesByPaymentMethod = {};
        sales.forEach(sale => {
            const method = sale.paymentMethod || 'Unknown';
            if (!salesByPaymentMethod[method]) {
                salesByPaymentMethod[method] = 0;
            }
            salesByPaymentMethod[method] += parseFloat(sale.grandTotal || 0);
        });

        const methods = Object.keys(salesByPaymentMethod);
        const amounts = methods.map(method => salesByPaymentMethod[method]);
        const colors = ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF'];

        paymentMethodChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: methods,
                datasets: [{
                    data: amounts,
                    backgroundColor: colors.slice(0, methods.length),
                    borderWidth: 2,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed / total) * 100).toFixed(1);
                                return context.label + ': ₹' + formatCurrency(context.parsed) + ' (' + percentage + '%)';
                            }
                        }
                    }
                }
            }
        });
    }

    function resetFilters() {
        $('#salesReportFilter')[0].reset();
        initializeDatePickers();
        loadSalesReport();
    }

    function exportToExcel() {
        const formData = $('#salesReportFilter').serialize();
        window.location.href = '/report/sales-report/export?' + formData;
    }

    function printReport() {
        window.print();
    }

    function showLoading() {
        $('.container').addClass('loading');
    }

    function hideLoading() {
        $('.container').removeClass('loading');
    }

    function showError(message) {
        // You can implement a toast notification or alert here
        alert(message);
    }

    function formatCurrency(amount) {
        return parseFloat(amount || 0).toFixed(2);
    }

    function formatDateString(dateString) {
        if (!dateString) return 'N/A';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString();
        } catch (e) {
            return dateString;
        }
    }
});
