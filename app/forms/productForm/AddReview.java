package forms.productForm;

import org.codehaus.jackson.node.ObjectNode;
import play.data.validation.Constraints;
import play.libs.Json;

import static utils.ControllerHelper.saveFlash;
import static utils.ControllerHelper.saveJson;

public class AddReview {

    @Constraints.Pattern(value = "[0-5]", message = "Invalid value for score")
    public Double score;

    @Constraints.Pattern(value = ".+", message = "Invalid value for comment")
    public String comment;


    public AddReview() {

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
