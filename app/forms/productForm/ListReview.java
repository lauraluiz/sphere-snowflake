package forms.productForm;

import io.sphere.client.shop.model.Review;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;

import java.util.List;

public class ListReview {

    public ListReview() {

    }

    public static ObjectNode getJson(List<Review> comments) {
        ObjectNode json = Json.newObject();
        ArrayNode list = json.putArray("review");
        for (Review comment : comments) {
            list.add(getJson(comment));
        }
        return json;
    }

    public static ObjectNode getJson(Review comment) {
        ObjectNode json = Json.newObject();
        if (comment == null) return json;
        json.put("id", comment.getIdAndVersion().getId());
        json.put("version", comment.getIdAndVersion().getVersion());
        json.put("author", comment.getAuthor());
        json.put("createdAt", comment.getCreatedAt().toString());
        json.put("comment", comment.getText());
        json.put("score", comment.getScore() * 5);
        return json;
    }

}
