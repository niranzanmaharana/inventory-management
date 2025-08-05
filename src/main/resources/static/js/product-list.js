$(document).ready(function () {
    const MAX_SIZE = 100 * 1024; // 100KB
    const DEFAULT_IMAGE_PATH = '/images/product-default-image.png';

    function setHeader(xhr) {
        const token = $('meta[name="_csrf"]').attr('content');
        const header = $('meta[name="_csrf_header"]').attr('content');
        xhr.setRequestHeader(header, token);
    }

    function retrieveImage(productId, imageType) {
        const formData = new FormData();
        formData.append('id', productId);
        formData.append('imageType', imageType);

        $.ajax({
            url: '/product/image-path',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            beforeSend: function (xhr) {
                setHeader(xhr);
            },
            success: function (response) {
                let imagePath = response && response.trim() !== "" ? response : DEFAULT_IMAGE_PATH;
                $('#previewImage').attr('src', imagePath).removeClass('d-none');
                $('#uploadImageModal').modal('show');
            },
            error: function (error) {
                console.log(error);
                showStatusMessage("Could not load product image.", "error");
                $('#previewImage').attr('src', DEFAULT_IMAGE_PATH).removeClass('d-none');
                $('#uploadImageModal').modal('show');
            }
        });
    }

    function showStatusMessage(message, type) {
        const statusDiv = $('#uploadStatusMessage');
        statusDiv
            .removeClass('text-success text-danger')
            .addClass(type === 'success' ? 'text-success' : 'text-danger')
            .text(message)
            .removeClass('d-none');
    }

    function sendAjaxAction(action, orderId) {
        if (!confirm(`Are you sure you want to ${action} this order?`)) return;

        $.ajax({
            url: `/purchase-order/${action}/${orderId}`,
            type: 'POST',
            success: function () {
                alert(`Order ${action}d successfully`);
                location.reload(); // Or use AJAX to update just the affected row
            },
            error: function (xhr) {
                const message = xhr.responseJSON?.message || `Failed to ${action} order`;
                alert(message);
            }
        });
    }

    $('.image-action-link').on('click', function (e) {
        $('#uploadStatusMessage').addClass('d-none').text('');
        e.preventDefault();
        const productId = $(this).data('product-id');
        const imageType = $(this).data('image-type');
        $('#uploadProductId').val(productId);
        $('#uploadImageType').val(imageType);
        $('#productImage').val('');
        $('#previewImage').addClass('d-none').attr('src', '#');
        retrieveImage(productId, imageType);
    });

    $('#productImage').on('change', function () {
        const file = this.files[0];
        showStatusMessage('', '');
        if (file.size > MAX_SIZE) {
            showStatusMessage(`File "${file.name}" exceeds 100KB.`);
            return;
        }
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $('#previewImage').attr('src', e.target.result).removeClass('d-none');
            }
            reader.readAsDataURL(file);
        }
    });

    $('#uploadImageForm').on('submit', function (e) {
        e.preventDefault();
        const fileInput = $('#productImage')[0];

        if (!fileInput.files || fileInput.files.length === 0) {
            showStatusMessage("Please select an image to upload.", "error");
            return;
        }

        const formData = new FormData(this);

        $.ajax({
            url: '/product/upload-image',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            beforeSend: function (xhr) {
                setHeader(xhr);
            },
            success: function (response) {
                console.log(response);
                showStatusMessage("Image uploaded successfully.", "success");
                $('#productImagePath').val(response);
            },
            error: function (error) {
                console.log(error);
                showStatusMessage("An error occurred during image upload.", "error");
            }
        });
    });
});