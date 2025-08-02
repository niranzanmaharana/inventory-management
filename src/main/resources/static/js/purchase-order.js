$(document).ready(function () {
    let itemIndex = $('#items-container .item-entry').length;

    function initializeDatepickers() {
        $(".datepicker, .expiryDate").datepicker({
            dateFormat: "dd-mm-yy",
            changeMonth: true,
            changeYear: true,
            yearRange: "1900:c",
            maxDate: 0,
            onClose: function () {
                $(this).trigger('change');
            }
        });
    }

    function loadSubCategories($categorySelect) {
        const categoryId = $categorySelect.val();
        const $itemEntry = $categorySelect.closest('.item-entry');
        const $subCategorySelect = $itemEntry.find('.sub-category-select');

        $.get(`/product-category/${categoryId}/subcategories`, function (subCategories) {
            $subCategorySelect.empty().append('<option disabled selected>Select Sub Category</option>');
            subCategories.forEach(sc => {
                $subCategorySelect.append($('<option>').val(sc.id).text(sc.categoryName));
            });
        });
    }

    function updateItemTitlesAndNames() {
        $('#items-container .item-entry').each(function (i) {
            $(this).find('.item-title').text('Item #' + (i + 1));
            $(this)
                .find('select, input')
                .each(function () {
                    const name = $(this).attr('name');
                    if (name) {
                        const newName = name.replace(/items\[\d+\]/, `items[${i}]`);
                        $(this).attr('name', newName);
                    }
                });
        });
    }

    initializeDatepickers();

    $('#items-container').on('change', '.category-select', function () {
        loadSubCategories($(this));
    });

    $('#add-item').on('click', function () {
        const template = $('#item-template').html().replace(/__index__/g, itemIndex);
        $('#items-container').append(template);
        initializeDatepickers(); // if you have any
        itemIndex++;
        updateItemTitlesAndNames();
    });

    $('#items-container').on('click', '.remove-item', function () {
        $(this).closest('.item-entry').remove();
        updateItemTitlesAndNames();
    });

    $('#items-container').on('change', '.sub-category-select', function () {
        var subCategoryId = $(this).val();
        var $itemEntry = $(this).closest('.item-entry');
        var $productSelect = $itemEntry.find('.product-select');

        // Clear existing options
        $productSelect.empty().append('<option value="" disabled selected>Select Product</option>');

        if (subCategoryId) {
            $.ajax({
                url: '/product/by-category/' + subCategoryId,
                type: 'GET',
                success: function (products) {
                    $.each(products, function (index, product) {
                        $productSelect.append($('<option>', {
                            value: product.id,
                            text: product.productName
                        }));
                    });
                },
                error: function () {
                    console.error('Failed to load products.');
                }
            });
        }
    });
});