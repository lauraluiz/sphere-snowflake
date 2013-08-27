package forms.productForm;

import org.codehaus.jackson.node.ObjectNode;
import play.data.validation.Constraints;
import play.libs.Json;

import static utils.ControllerHelper.saveFlash;
import static utils.ControllerHelper.saveJson;

public class AddReview {

    @Constraints.Min(0)
    @Constraints.Max(5)
    @Constraints.Required(message = "Score required")
    public Integer rating;

    public String comment;


    public AddReview() {

    }

    public boolean hasScore() {
        return rating != null;
    }

    public boolean hasComment() {
        return comment != null;
    }

    public double getScore() {
        if (rating == null) return 0.0;
        return rating.doubleValue() / 5.0;
    }

    public String getComment() {
        if (comment == null) return "";
        return comment;
    }

    public void displaySuccessMessage() {
        String message = "Your review has been submitted!";
        saveFlash("add-review-success", message);

        ObjectNode json = Json.newObject();
        json.put("success", message);
        //json.put("customer-firstName", customer.getName().getFirstName());
        saveJson(json);
    }

}
