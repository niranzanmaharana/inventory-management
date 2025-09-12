$(document).ready(function () {
    let selectedProducts = [];

    // Customer search
    $('#customerSearch').on('input', function () {
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
        e.preventDefault();
        if ($(this).attr('id') === 'createCustomerBtn') {
            openCustomerForm();
            return;
        } else {
            $('#customerId').val($(this).data('id'));
            $('#customerSearch').val($(this).text());
            $('#customerResults').empty();
        }
    });

    function clearCustomerForm() {
        $('#customerSearch').val('');
        $('#customerId').val('');
        $('#customerResults').empty();
    }

    function openCustomerForm() {
        clearCustomerForm();
        $('#createCustomerModal').modal('show');
    }

    $('#clearCustomerBtn').on('click', function () {
        $('#customerSearch').val('');
        $('#customerId').val('');
        $('#customerResults').empty();
    });

    $('#addCustomerBtn').on('click', function () {
        openCustomerForm();
    });

    function setHeader(xhr) {
        const token = $('meta[name="_csrf"]').attr('content');
        const header = $('meta[name="_csrf_header"]').attr('content');
        xhr.setRequestHeader(header, token);
    }

    // Create customer submit
    $('#createCustomerForm').submit(function (e) {
        e.preventDefault();

        let formData = {
            firstName: $("#customer-firstName").val(),
            lastName: $("#customer-lastName").val(),
            mobile: $("#customer-mobile").val(),
            email: $("#customer-email").val(),
            address: $("#customer-address").val(),
            postalCode: $('#customer-postalCode').val()
        };

        $.ajax({
            url: '/customer/save',
            type: 'POST',
            data: JSON.stringify(formData),
            beforeSend: function (xhr) {
                setHeader(xhr);
            },
            contentType: 'application/json',
            success: function (response) {
                let customer = response.data;
                $('#customerId').val(customer.id);
                $('#customerSearch').val(`${customer.firstName} ${customer.lastName || ''} - ${customer.mobile}`);
                $('#createCustomerModal').modal('hide');
            },
            error: function (error) {
                console.log(error);
                if (error.status) {
                    let response = JSON.parse(error.responseText);
                    alert(`Unable to save customer data! Error: ${response.message}`);
                    return;
                }
                alert('Unable to save the customer data. Please try again after some time.');
            }
        });
    });

    // Product search
    $('#productSearch').on('input', function () {
        let query = $(this).val().trim();
        if (query.length < 2) {
            $('#productResults').empty();
            return;
        }

        $.get('/product/search', { keyword: query }, function (data) {
            let results = $('#productResults').empty();
            data.forEach(p => {
                results.append(`
                    <a href="#" class="list-group-item list-group-item-action product-item"
                        data-product-id="${p.productId}" data-product-name="${p.productName}" data-product-code="${p.productCode}" data-batch-code="${p.batchCode}" data-inv-id="${p.inventoryId}"
                        data-expiry="${p.expiryDate}" data-available="${p.availableQuantity}" data-amount="${p.amount}">
                        <b>${p.productName}</b> (<b>${p.productCode}</b>) - <b>Batch: ${p.batchCode}</b>, Avail: <b>${p.availableQuantity}</b>, Received On: <b>${p.receivedDate}</b>, Expiry: <b>${p.expiryDate}</b>
                    </a>
                `);
            });
        });
    });

    function findProduct(product) {
        let uid = product.batchCode + '-' + product.productId;
        return selectedProducts.find(item =>
            uid === item.batchCode + '-' + item.productId
        );
    }

    function findProductByUid(uid) {
        return selectedProducts.find(item =>
            uid === item.batchCode + '-' + item.productId
        );
    }

    // Select product
    $(document).on('click', '.product-item', function (e) {
        e.preventDefault();
        let product = {
            uid: $(this).data('batch-code') + '-' + $(this).data('product-id'),
            productId: $(this).data('product-id'),
            productCode: $(this).data('product-code'),
            batchCode: $(this).data('batch-code'),
            inventoryId: $(this).data('inv-id'),
            productName: $(this).data('product-name'),
            expiryDate: $(this).data('expiry'),
            available: $(this).data('available'),
            quantity: 1,
            amount: $(this).data('amount')
        };

        let existingItem = findProduct(product);
        let index = selectedProducts.indexOf(existingItem);

        if (existingItem) {
            if (confirm(`Same product ${product.productName} is already in the cart with quantity ${product.quantity}. Do you want to increase the quantity?`)) {
                existingItem.quantity += 1;
                $(`input[name="products[${index}].quantity"]`).val(existingItem.quantity);
            } else {
                alert("No changes made to the cart.");
            }
        } else {
            selectedProducts.push(product);
        }

        updateProductTable();
        $('#productResults').empty();
        $('#productSearch').val('');
    });

    function updateProductTable() {
        let tbody = $('#selectedProductsTable tbody').empty();

        let totalItems = 0;
        let totalPrice = 0;

        selectedProducts.forEach((p, index) => {
            totalItems += p.quantity;
            let lineTotal = (p.amount || 0) * p.quantity;
            totalPrice += lineTotal;
            let rowTitle = 'Product Code: ' + p.productCode + ', Batch Code: ' + p.batchCode + ', Expiry Date: ' + p.expiryDate;
            tbody.append(`
                <tr id="cart-item-${p.uid}">
                    <td title="${rowTitle}">${p.productName}</td>
                    <td>
                        <button type="button" class="qty-btn minus" data-uid="${p.uid}">−</button>
                        <span class="selectedQuntity" name="products[${index}].quantity">${p.quantity}</span>
                        <button type="button" class="qty-btn plus" data-uid="${p.uid}">+</button>
                    </td>
                    <td>₹ <span class="amount">${p.amount}</span></td>
                    <td><button type="button" class="btn btn-danger btn-sm remove-product" data-index="${index}">Remove</button></td>
                    <input type="hidden" name="products[${index}].productId" value="${p.productId}">
                    <input type="hidden" name="products[${index}].inventoryId" value="${p.inventoryId}">
                    <input type="hidden" name="products[${index}].batchCode" value="${p.batchCode}">
                    <input type="hidden" name="products[${index}].amount" value="${p.amount || 0}">
                    <input type="hidden" name="products[${index}].quantity" value="${p.quantity}">
                </tr>
            `);
        });

        // Update summary panel
        $('#totalItems').text(totalItems);
        $('#grandTotal').text(totalPrice.toFixed(2));
    }

    // Remove product
    $(document).on('click', '.remove-product', function () {
        let index = $(this).data('index');
        selectedProducts.splice(index, 1);
        updateProductTable();
    });

    function updateCartSummary() {
        let totalItems = 0;
        let grandTotal = 0;

        selectedProducts.forEach((product, index) => {
            let quantity = parseInt(product.quantity);
            let amount = parseFloat(product.amount);
            totalItems += quantity;
            let lineTotal = quantity * amount;
            grandTotal += lineTotal;
        });

        $("#totalItems").text(totalItems);
        $("#grandTotal").text(grandTotal.toFixed(2));
    }

    $(document).on('click', '.qty-btn.plus', function () {
        let uid = $(this).data("uid");
        let existingItem = findProductByUid(uid);
        let index = selectedProducts.indexOf(existingItem);

        let qtyElem = $(this).siblings(".selectedQuntity");


        let currentQty = parseInt(qtyElem.text());
        let newQuantity = currentQty + 1;

        if (newQuantity > existingItem.available) {
            alert(`There are only ${existingItem.available} in the stock`);
            return;
        }
        qtyElem.text(newQuantity);
        $(`input[name="products[${index}].quantity"]`).val(newQuantity);
        existingItem.quantity = newQuantity;
        updateCartSummary();
    });

    $(document).on('click', '.qty-btn.minus', function () {
        let uid = $(this).data("uid");
        let existingItem = findProductByUid(uid);
        let index = selectedProducts.indexOf(existingItem);
        let qtyElem = $(this).siblings(".selectedQuntity");
        let currentQty = parseInt(qtyElem.text());

        let newQuantity = currentQty - 1;
        if (newQuantity === 0) {
            alert('You have only one item added to the cart. If you want you can delete the item from the cart');
            return;
        }

        qtyElem.text(newQuantity);
        $(`input[name="products[${index}].quantity"]`).val(newQuantity);
        existingItem.quantity = newQuantity;
        updateCartSummary();
    });

    $("#sellingForm").on("submit", function (e) {
        e.preventDefault();
        let customerId = $('#customerId').val();
        if (!customerId) {
            alert('Please select the customer for billing!');
            return;
        }
        if (selectedProducts && selectedProducts.length == 0) {
            alert('Your cart is still empty! Please add some items to proceed.');
            return;
        }

        $("#summaryCustomerName").text($("#customerSearch").val());

        let totalItems = 0;
        let grandTotal = 0;
        let summaryBody = $("#order-summary-table tbody");
        summaryBody.empty();

        selectedProducts.forEach((product, index) => {
            let productName = product.productName;
            let quantity = parseInt(product.quantity);
            let amount = parseFloat(product.amount);
            let lineTotal = quantity * amount;

            summaryBody.append(`
                <tr>
                    <td>${index}</td>
                    <td>${productName}</td>
                    <td>${quantity}</td>
                    <td>₹ ${amount.toFixed(2)}</td>
                    <td>₹ ${lineTotal.toFixed(2)}</td>
                </tr>
            `);

            totalItems += quantity;
            grandTotal += lineTotal;
        });

        $("#summaryTotalItems").text(totalItems);
        $("#summaryGrandTotal").text(grandTotal.toFixed(2));

        // Show modal
        $("#orderSummaryModal").modal("show");
    });

    // On confirm, submit the form normally
    $("#confirmCheckout").on("click", function () {
        $("#orderSummaryModal").modal("hide");
        let saleItems = [];
        selectedProducts.forEach((product, index) => {
            saleItems.push({
                productId: product.productId,
                inventoryId: product.inventoryId,
                quantity: product.quantity,
                discountPercent: 0.0,
                taxPercent: 0.0
            });
        });
        let formData = {
            customerId: $('#customerId').val(),
            paymentMethod: $('#paymentMethod').val(),
            totalDiscount: 0.0,
            totalTax: 0.0,
            items: saleItems
        };

        $.ajax({
            url: "/sales/save",
            type: 'POST',
            beforeSend: function (xhr) {
                setHeader(xhr);
            },
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                window.location.href = '/sales/confirmation/' + response.data;
            },
            error: function (xhr) {
                console.error(xhr.responseText);
                if (xhr.status) {
                    alert(`Error placing the order: ${xhr.message}`);
                    return;
                }
                alert('Unable to place the order at this moment! Please try again after some time.');
            }
        });
    });
});
