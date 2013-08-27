$ ->
    html = $("#add-review-template").html()
    template = Handlebars.compile $.trim(html) if html?

    addReview = new Form $('#form-add-review')

    # Bind add review 'save' submit event to 'add review' functionality
    addReview.form.submit( ->
        # Remove alert messages
        addReview.removeAllMessages()

        # Validate form client side
        return false unless addReview.validateRequired()

        # Send new data to server
        addReview.startSubmit()
        url = addReview.form.attr("action")
        method = addReview.form.attr("method")
        data = addReview.form.serialize()
        xhr = addReview.submit(url, method, data)
        xhr.done (res) -> addReview.doneSubmit(res)
        xhr.fail (res) -> addReview.failSubmit(res)
        xhr.always -> addReview.stopSubmit()

        return addReview.allowSubmit
    )

    # Bind click on 'size variant' to 'select size' functionality
    $('.product-info-variant-size a').click( ->

        # Update 'add to cart' button with new variant
        variantId = $(this).data("variant")
        $('#form-add-to-cart [name=variantId]').val(variantId)

        # Disable 'active' on previously selected size
        $('.product-info-variant-size li.active').removeClass("active")

        # Enable 'active' on selected size
        $(this).parent().addClass("active")

        # Disable link
        return false
    )


