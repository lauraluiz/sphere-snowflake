package forms.cartForm;

import io.sphere.client.shop.model.*;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;

import java.math.BigDecimal;

public class ListCart {

    public ListCart() {

    }

    public static ObjectNode getJson(String snapshot) {
        ObjectNode json = Json.newObject();
        json.put("snapshot", snapshot);
        return json;
    }

    public static ObjectNode getJson(Cart cart) {
        ObjectNode json = Json.newObject();
        if (cart.getTotalQuantity() < 1) return json;
        json.put("currency", cart.getCurrency().getCurrencyCode());
        if (cart.getShippingAddress() != null) {
            json.put("totalPrice", cart.getTaxedPrice().getTotalGross().format(2));
            json.put("totalNetPrice", cart.getTaxedPrice().getTotalNet().format(2));
            // TODO Use SDK shipping logic
            json.put("shippingPrice", "0.00");
            ArrayNode taxPortions = json.putArray("taxPortion");
            for (TaxPortion tax: cart.getTaxedPrice().getTaxPortions()) {
                ObjectNode taxPortion = Json.newObject();
                taxPortion.put("rate", String.valueOf(BigDecimal.valueOf(tax.getRate() * 100).stripTrailingZeros()));
                taxPortion.put("amount", tax.getAmount().format(2));
                taxPortion.put("currency", tax.getAmount().getCurrencyCode());
                taxPortions.add(taxPortion);
            }
        } else {
            json.put("totalPrice", cart.getTotalPrice().format(2));
        }
        ArrayNode list = json.putArray("item");
        for (LineItem item : cart.getLineItems()) {
            list.add(getJson(item));
        }
        return json;
    }

    public static ObjectNode getJson(LineItem item) {
        ObjectNode json = Json.newObject();
        json.put("itemId", item.getId());
        json.put("productId", item.getProductId());
        json.put("productName", item.getProductName());
        json.put("quantity", item.getQuantity());
        json.put("currency", item.getTotalPrice().getCurrencyCode());
        json.put("price", item.getPrice().getValue().format(2));
        json.put("totalPrice", item.getTotalPrice().format(2));

        ArrayNode attributes = json.putArray("attribute");
        for (Attribute attr : item.getVariant().getAttributes()) {
            ObjectNode attribute = Json.newObject();
            attribute.put("name", attr.getName());
            attribute.put("value", attr.getValue().toString());
            attributes.add(attribute);
        }

        ObjectNode images = Json.newObject();
        images.put("thumbnail", item.getVariant().getFeaturedImage().getSize(ImageSize.THUMBNAIL).getUrl());
        images.put("small", item.getVariant().getFeaturedImage().getSize(ImageSize.SMALL).getUrl());
        images.put("medium", item.getVariant().getFeaturedImage().getSize(ImageSize.MEDIUM).getUrl());
        images.put("large", item.getVariant().getFeaturedImage().getSize(ImageSize.LARGE).getUrl());
        images.put("original", item.getVariant().getFeaturedImage().getSize(ImageSize.ORIGINAL).getUrl());
        json.put("image", images);

        return json;
    }

}
