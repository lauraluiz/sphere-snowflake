package controllers;

import controllers.actions.Ajax;
import controllers.actions.SaveContext;
import forms.addressForm.ListAddress;
import forms.productForm.AddReview;
import forms.productForm.ListReview;
import io.sphere.client.exceptions.SphereBackendException;
import io.sphere.client.model.QueryResult;
import io.sphere.client.model.VersionedId;
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

    public static Result getReview(String productId) {
        if (!sphere().isLoggedIn()) return noContent();
        QueryResult<Review> queryReview = sphere().currentCustomer().reviewsForProduct(productId).fetch();
        Review review = null;
        if (queryReview.getCount() > 0) {
            review = queryReview.getResults().get(0);
        }
        return ok(ListReview.getJson(review));
    }

    @With(Ajax.class)
    public static Result addReview(String productId) {
        if (!sphere().isLoggedIn()) {
            return unauthorized("Login required");
        }
        // Case invalid product
        Product product = sphere().products().byId(productId).fetch().orNull();
        if (product == null) {
            return notFound("Product not found");
        }
        // Case valid select product
        Variant variant = product.getMasterVariant();
        Category category = getDefaultCategory(product);
        List<Comment> comments = sphere().comments().byProductId(product.getId()).fetch().getResults();
        Form<AddReview> form = addReviewForm.bindFromRequest();
        // Case missing or invalid form data
        if (form.hasErrors()) {
            displayErrors("add-review", form);
            System.out.println(form.errorsAsJson());
            return badRequest(products.render(product, variant, category, comments, form));
        }
        // Case valid customer update
        AddReview addReview = form.get();
        String author = sphere().currentCustomer().fetch().getName().getFirstName();
        Review review;
        try {
            System.out.println("Creating review");
            System.out.println("RATIIIIING" + addReview.rating);
            review = sphere().currentCustomer().createReview(productId, author, product.getName(), addReview.getComment(), addReview.getScore());
        } catch (SphereBackendException sbe) {
            System.out.println("Editing review");
            VersionedId reviewId = VersionedId.create(addReview.id, addReview.getVersion());
            ReviewUpdate update = new ReviewUpdate()
                    .setScore(addReview.getScore())
                    .setText(addReview.comment);
            review = sphere().reviews().updateReview(reviewId, update);
        }
        addReview.displaySuccessMessage(review);
        return ok(products.render(product, variant, category, comments, addReviewForm));
    }
}
