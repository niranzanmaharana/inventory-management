$(document).ready(function () {
    $(".btn-search").addClass("d-none");
    // Show relevant input based on filter type
    $("#filterType").on("change", function () {
        $(".filter-input").addClass("d-none");
        const selected = $(this).val();

        if (selected === "invoice-number") {
            $("#invoiceNumberInput").removeClass("d-none");
        } else if (selected === "order-number") {
            $("#orderNumberInput").removeClass("d-none");
        } else if (selected === "customer-id") {
            $("#customerInput").removeClass("d-none");
        } else if (selected === "date-range") {
            $("#dateRangeInput").removeClass("d-none");
            $("#endDateInput").removeClass("d-none");
        }

        if (selected) {
            $(".btn-search").removeClass("d-none");
        }
    });

    // Customer search
    $('#customerName').on('input', function () {
        let query = $(this).val().trim();
        if (query.length < 2) {
            $('#customerResults').empty();
            return;
        }

        $.get('/customer/search', { keyword: query }, function (data) {
            let results = $('#customerResults').empty();
            if (data.length > 0) {
                data.forEach(c => {
                    results.append(`<a href="#" class="list-group-item list-group-item-action" data-id="${c.id}">${c.firstName} ${c.lastName || ''} - ${c.mobile}</a>`);
                });
            } else {
                results.append(`<a href="#" class="list-group-item list-group-item-action text-danger" id="createCustomerBtn">No customer found. Click to create.</a>`);
            }
        });
    });

    // Select customer
    $(document).on('click', '#customerResults a', function (e) {
        $('#customer-id').val($(this).data('id'));
        $('#customerName').val($(this).text());
        $('#customerResults').empty();
    });

    // Handle form submission
    $("#invoiceFilterForm").on("submit", function (e) {
        e.preventDefault();
        $("#invoiceTable tbody").empty();
        const filterBy = $("#filterType").val();
        
        let url, data;
        
        if (filterBy === "date-range") {
            const startDate = $("#startDate").val();
            const endDate = $("#endDate").val();
            
            if (!startDate || !endDate) {
                alert("Please select both start and end dates.");
                return;
            }
            
            if (new Date(startDate) > new Date(endDate)) {
                alert("Start date cannot be after end date.");
                return;
            }
            
            url = `/invoice/date-range?startDate=${startDate}&endDate=${endDate}`;
            data = null;
        } else {
            const searchKeyword = $(`#${filterBy}`).val();
            url = `/invoice/${filterBy}/${searchKeyword}`;
            data = null;
        }
        
        $.ajax({
            url: url,
            method: "GET",
            data: data,
            success: function (response) {
                const data = response.data;
                let rows = "";
                data.forEach(function (invoice) {
                    rows += `
                        <tr>
                            <td>${invoice.invoiceNumber}</td>
                            <td>${invoice.orderNumber}</td>
                            <td>${invoice.customer.customerName}</td>
                            <td>${invoice.formattedTxDate}</td>
                            <td>${invoice.grandTotal}</td>
                            <td>${invoice.paymentMethod}</td>
                            <td>
                                <a href="/invoice/view/${invoice.invoiceNumber}" target="_blank" class="btn btn-sm btn-primary me-1" title="View Invoice">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a href="/invoice/download/${invoice.invoiceNumber}" class="btn btn-sm btn-success" title="Download PDF">
                                    <i class="bi bi-download"></i>
                                </a>
                            </td>
                        </tr>
                    `;
                });
                $("#invoiceTable tbody").html(rows);
            },
            error: function (xhr) {
                if (xhr.status && typeof xhr.responseJSON === 'object') {
                    alert(xhr.responseJSON.message);
                    return;
                }
                console.error(xhr.responseText);
                alert('Unable to retrieve the invoice! Please try again after some time.');
            }
        });
    });
});