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
});