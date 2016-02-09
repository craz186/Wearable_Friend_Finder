import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.photofall.rest.store.ScanStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;

public class TescoScraper {
    public static void main(String[] args) {
        try {
            new TescoScraper().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() throws IOException {
        ScanStore store = new ScanStore();
        for(int i=0; i<=1960; i+=20) {
            Document doc2 = Jsoup.connect("http://www.tesco.ie/groceries/product/browse/default.aspx?N=4294954027&Ne=4294954028&=&Nao="+i).get();
            Elements elements1 = doc2.getElementsByClass("price");
            Elements elements2 = doc2.getElementsByClass("desc");
            LinkedList<String> prices = new LinkedList<>();
            LinkedList<String> names = new LinkedList<>();
            for(Element e : elements1) {
                if(e.children().size()!=0)
                    prices.add(e.child(0).text());
            }
            for(Element e : elements2) {
                if(e.children().size()!=0)
                    names.add(e.child(0).text());
            }
            for(int j = 0; j < prices.size(); j++) {
                System.out.println(names.get(j+1)+ " " + prices.get(j));
                store.add(names.get(j+1), prices.get(j), "Tescos");

            }
            System.out.println();
        }


    }
}
