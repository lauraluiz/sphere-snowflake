package controllers;

import controllers.actions.Ajax;
import controllers.actions.SaveContext;
import forms.productForm.AddReview;
import io.sphere.client.exceptions.SphereBackendException;
import io.sphere.client.shop.model.*;
import play.data.Form;
import play.mvc.Result;
import play.mvc.With;
import sphere.ShopController;
import java.util.List;
import views.html.products;

import static play.data.Form.form;
import static utils.ControllerHelper.displayErrors;
import static utils.ControllerHelper.getDefaultCategory;

@With(SaveContext.class)
public class Products extends ShopController {

    final static Form<AddReview> addReviewForm = form(AddReview.class);

    public static Result select(String productSlug, int variantId) {
        // Case invalid product
        Product product = sphere().products().bySlug(productSlug).fetch().orNull();
        if (product == null) {
            return notFound("Product not found: " + productSlug);
        }
        // Case valid select product
        Variant variant = product.getVariants().byId(variantId).or(product.getMasterVariant());
        Category category = getDefaultCategory(product);
        List<Comment> comments = sphere().comments().byProductId(product.getId()).fetch().getResults();
        return ok(products.render(product, variant, category, comments, addReviewForm));
    }

    @With(Ajax.class)
    public static Result review(String productSlug, int variantId) {
        if (!sphere().isLoggedIn()) {
            return unauthorized("Login required");
        }
        // Case invalid product
        Product product = sphere().products().bySlug(productSlug).fetch().orNull();
        if (product == null) {
            return notFound("Product not found: " + productSlug);
        }
        // Case valid select product
        Variant variant = product.getVariants().byId(variantId).or(product.getMasterVariant());
        Category category = getDefaultCategory(product);
        List<Comment> comments = sphere().comments().byProductId(product.getId()).fetch().getResults();
        Form<AddReview> form = addReviewForm.bindFromRequest();
        // Case missing or invalid form data
        if (form.hasErrors()) {
            displayErrors("add-review", form);
            return badRequest(products.render(product, variant, category, comments, form));
        }
        // Case valid customer update
        AddReview addReview = form.get();
        String author = sphere().currentCustomer().fetch().getName().getFirstName();
        /*if (addReview.hasComment()) {
            sphere().currentCustomer().createComment(product.getId(), author, product.getName(), addReview.getComment());
        } */
        try {
            if (addReview.hasScore()) {
                sphere().currentCustomer().createReview(product.getId(), author, product.getName(), addReview.getComment(), addReview.getScore());
            }
        } catch (SphereBackendException sbe) {
            flash("error", "You already voted this product");
            return badRequest(products.render(product, variant, category, comments, form));
        }
        addReview.displaySuccessMessage();
        return ok(products.render(product, variant, category, comments, addReviewForm));
    }
}
