package controllers;

import controllers.actions.Ajax;
import controllers.actions.SaveContext;
import forms.productForm.AddReview;
import io.sphere.client.shop.model.*;
import play.data.Form;
import play.mvc.Result;
import play.mvc.With;
import sphere.ShopController;
import views.html.products;
import java.util.List;

import java.util.List;

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
        // Case invalid product
        Product product = sphere().products().bySlug(productSlug).fetch().orNull();
        if (product == null) {
            return notFound("Product not found: " + productSlug);
        }
        // Case valid select product
        Variant variant = product.getVariants().byId(variantId).or(product.getMasterVariant());
        Category category = getDefaultCategory(product);
        List<Comment> comments = sphere().comments().byProductId(product.getId()).fetch().getResults();
        Customer customer = sphere().currentCustomer().fetch();
        Form<AddReview> form = addReviewForm.bindFromRequest();
        // Case missing or invalid form data
        if (form.hasErrors()) {
            displayErrors("add-review", form);
            return badRequest(products.render(product, variant, category, comments, addReviewForm));
        }
        // Case valid customer update
        AddReview addReview = form.get();
        String author = sphere().isLoggedIn() ? customer.getName().getFirstName() : "Guest";
        if (addReview.comment != null) {
            sphere().currentCustomer().createComment(product.getId(), author, product.getName(), addReview.comment);
        }
        if (addReview.score != null) {
            sphere().currentCustomer().createReview(product.getId(), author, product.getName(), addReview.comment, addReview.score);
        }
        addReview.displaySuccessMessage();
        return ok(products.render(product, variant, category, comments, addReviewForm));
    }
}
