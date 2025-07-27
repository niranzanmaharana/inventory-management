$(document).ready(function () {
    // Toggle collapse and icon
    $('[id^="toggle-"]').on('click', function (e) {
        e.stopPropagation();

        const $icon = $(this);
        const targetSelector = $icon.attr('data-target');
        const $target = $(targetSelector);

        if ($target.length) {
            $target.collapse('toggle');
            const isOpen = $icon.text() === '−';
            $icon.text(isOpen ? '+' : '−');
        }
    });

    // Expand All
    $('#expandAllBtn').on('click', function () {
        $('[id^="toggle-"]').each(function () {
            const $icon = $(this);
            const targetSelector = $icon.attr('data-target');
            const $target = $(targetSelector);

            if (!$target.hasClass('show')) {
                $target.collapse('show');
                $icon.text('−');
            }
        });
    });

    // Collapse All
    $('#collapseAllBtn').on('click', function () {
        $('[id^="toggle-"]').each(function () {
            const $icon = $(this);
            const targetSelector = $icon.attr('data-target');
            const $target = $(targetSelector);

            if ($target.hasClass('show')) {
                $target.collapse('hide');
                $icon.text('+');
            }
        });
    });
});