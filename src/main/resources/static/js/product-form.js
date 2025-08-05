$(function () {
    // Initialize datepicker
    $(".datepicker").datepicker({
        dateFormat: "dd-mm-yy",
        changeMonth: true,
        changeYear: true,
        yearRange: "1900:c",
        minDate: 0
    });

    // Enable input if checkbox is already checked (initial setup)
    $('.attribute-checkbox').each(function () {
        const attrId = $(this).data('attr-id');
        const input = $('#attrInput_' + attrId);
        if ($(this).is(':checked')) {
            input.prop('disabled', false);
            input.prop('required', true);
        }
    });

    // ✅ NEW: Restore attribute values from JSON if form reloaded with errors
    const attributeJsonVal = $('#attributeJson').val();
    if (attributeJsonVal) {
        try {
            const attributes = JSON.parse(attributeJsonVal);
            Object.keys(attributes).forEach(attrId => {
                const checkbox = $('#attrCheck_' + attrId);
                const input = $('#attrInput_' + attrId);
                const value = attributes[attrId];

                if (checkbox.length && input.length) {
                    checkbox.prop('checked', true);
                    input.val(value).prop('disabled', false).prop('required', true);
                }
            });
        } catch (e) {
            console.error("Failed to parse attribute JSON:", e);
        }
    }

    // Toggle input field on checkbox change
    $('.attribute-checkbox').change(function () {
        const attrId = $(this).data('attr-id');
        const input = $('#attrInput_' + attrId);
        if ($(this).is(':checked')) {
            input.prop('disabled', false).focus();
            input.prop('required', true);
        } else {
            input.prop('disabled', true).val('');
            input.prop('required', false);
        }
    });

    // Optional: clean up invalid class on input change
    $('.attribute-value-input').on('input', function () {
        if ($(this).val().trim()) {
            $(this).removeClass('is-invalid');
        }
    });

    // Prepare attribute JSON before form submission
    $('form').on('submit', function (e) {
        const form = this;
        const attributes = {};
        let isValid = true;

        $('.attribute-checkbox').each(function () {
            const attrId = $(this).data('attr-id');
            const input = $('#attrInput_' + attrId);
            const isChecked = $(this).is(':checked');
            const value = input.val().trim();

            if (isChecked) {
                if (!value) {
                    input.addClass('is-invalid');
                    isValid = false;
                } else {
                    input.removeClass('is-invalid');
                    attributes[attrId] = value;
                }
            } else {
                input.removeClass('is-invalid');
            }
        });

        if (!form.checkValidity() || !isValid) {
            e.preventDefault();
            $(form).addClass('was-validated');
            if (!isValid) {
                $('#attributeError').removeClass('d-none');
            }
            return false;
        }

        $('#attributeJson').val(JSON.stringify(attributes));
    });
});
