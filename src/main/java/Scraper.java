import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class Scraper {

    private static final String searchUrl = "https://vancouver.craigslist.org/search/cta?query=vw%20gti&sort=rel";

    public static void main(String[] args) {

        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);

        try {
            HtmlPage page = client.getPage(searchUrl);
            System.out.println(page.asXml());
            List<HtmlElement> items = (List<HtmlElement>) page.getByXPath("//li[@class='result-row']");
            if (items.isEmpty()){
                System.out.println("No items found");
            }else {

                for (HtmlElement htmlItem : items){
                    HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath("p[@class='result-info']/a"));
                    HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(".//a/span[@class='result-price']"));

                    String itemPrice = spanPrice == null ? "0.0": spanPrice.asText();

                    Item item = new Item();

                    item.setTitle(itemAnchor.asText());
                    item.setUrl(itemAnchor.getHrefAttribute());

                    item.setPrice(new BigDecimal(itemPrice.replace("$", "")));

                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = mapper.writeValueAsString(item);

                    System.out.println(jsonString);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
