package forms.productForm;

import io.sphere.client.shop.model.Review;
import org.codehaus.jackson.node.ObjectNode;
import play.data.validation.Constraints;
import play.libs.Json;

import static utils.ControllerHelper.saveFlash;
import static utils.ControllerHelper.saveJson;

public class AddReview extends ListReview {

    public String id;
    public String version;

    @Constraints.Min(0)
    @Constraints.Max(5)
    @Constraints.Required(message = "Score required")
    public Integer rating;

    public String comment;


    public AddReview() {

    }

    public double getScore() {
        if (rating == null) return 0.0;
        System.out.println(rating);
        return rating.doubleValue() / 5.0;
    }

    public int getVersion() {
        if (version == null) return 0;
        return Integer.valueOf(version);
    }

    public String getComment() {
        if (comment == null) return "";
        return comment;
    }

    public void displaySuccessMessage(Review comment) {
        String message = "Your review has been submitted!";
        saveFlash("add-review-success", message);

        ObjectNode json = Json.newObject();
        json.put("success", message);
        json.putAll(getJson(comment));
        saveJson(json);
    }

}
