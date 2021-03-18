package labs.pm.data;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class ProductManager {
    private Map<Product, List<Review>> products = new HashMap<>();
    private Locale locale;
    private ResourceBundle resourceBundle;
    private DateTimeFormatter dateTimeFormatter;
    private NumberFormat numberFormat;

    public ProductManager(Locale locale) {
        this.locale = locale;
        resourceBundle = ResourceBundle.getBundle("resources", locale);
        dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
        numberFormat = NumberFormat.getCurrencyInstance(locale);
    }


    public void printProductReport(int id){
        printProductReport(findProduct(id));
    }
    public void printProductReport(Product product){
            StringBuilder txt = new StringBuilder();
            txt.append(
                MessageFormat.format(
                    resourceBundle.getString("product"),
                    product.getName(),
                    numberFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateTimeFormatter.format(product.getBestBefore())
                    ));
            txt.append("\n");
            List<Review> reviews = products.get(product);
            Collections.sort(reviews);
            for( Review review: reviews){
                    txt.append(
                            MessageFormat.format(
                                    resourceBundle.getString("review"),
                                    review.getRating().getStars(),
                                    review.getComments()
                            ));
                    txt.append("\n");
            }
            if(reviews.isEmpty()){
                txt.append(resourceBundle.getString("no.reviews"));
                txt.append("\n");
            }

            System.out.println(txt);
    }
    public  Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore){
        Product product = new Food(id, name, price, rating, bestBefore);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }
    public  Product createProduct(int id, String name, BigDecimal price, Rating rating ){
        Product product = new Drink(id, name, price, rating);
        products.putIfAbsent(product, new ArrayList<>());
        return product;
    }

    public Product reviewProduct(int id, Rating rating, String comments){
        return  reviewProduct(findProduct(id), rating, comments);
    }

    public Product reviewProduct(Product oldProduct, Rating rating, String comments){
        List<Review> reviews = products.get(oldProduct);
        products.remove(oldProduct);
        reviews.add(new Review(rating, comments));
        int sum = 0;
        for(Review review : reviews){
            sum += review.getRating().ordinal();
        }
        Product product = oldProduct.applyRating(Rateable.convert(Math.round((float)sum/reviews.size())));
        products.put(product, reviews);
        return product;
    }
    public Product findProduct(int id){
        Product result = null;
        for (Product product :
                products.keySet()) {
            if(product.getId() == id) {
                result = product;
                break;
            };
        }
        return result;
    }
}
