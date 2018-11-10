import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PrintlnClass {

    public static void main(String[] args) throws IOException {

        printlnFileDownloadLink();

    }

    private static void printlnFileDownloadLink() throws IOException {
        String uri = "https://github.com/littleostar-pdf/__WEB_PDF";

        uri = uri.concat("/tree/fix-master/files");
        System.out.println("uri:\n" + uri);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
            System.out.println();

            HttpEntity httpEntity = response.getEntity();

            InputStream inputStream = httpEntity.getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String tmp;
            StringBuilder stringBuffer = new StringBuilder();
            while ((tmp = bufferedReader.readLine()) != null) {
                stringBuffer.append(tmp);
                stringBuffer.append("\n");
            }
            String html = stringBuffer.toString();
//            System.out.println(html);

            Document document = Jsoup.parse(html);
            // #js-repo-pjax-container >
            // div.container.new-discussion-timeline.experiment-repo-nav >
            // div.repository-content >
            // div.file-wrap >
            // table >
            // tbody:nth-child(2) >
            // tr:nth-child(2) >
            // td.content >
            // span
            Elements contentTds = document.select(
                    "#js-repo-pjax-container > " +
                            "div.container.new-discussion-timeline.experiment-repo-nav > " +
                            "div.repository-content > " +
                            "div.file-wrap > " +
                            "table > " +
                            "tbody:eq(1) > " +
                            "tr > " +
                            "td:eq(1) > " +
                            "span > " +
                            "a");
            if (contentTds != null) {
                Element element0 = contentTds.get(0);
                String tempLink = getTempLink(element0);

                System.out.println("####" + element0.text().substring(0, element0.text().indexOf(".")));
                System.out.println("```");
                for (Element element : contentTds) {
                    String currentLink = element.attr("href").replace("blob", "raw");

                    if (!currentLink.contains(tempLink)) {
                        System.out.println("```");
                        System.out.println("---");
                        System.out.println();
                        System.out.println("####" + element.text().substring(0, element.text().indexOf(".")));
                        System.out.println("```");

                    }
                    tempLink = getTempLink(element);

                    int firstPointIndex = currentLink.indexOf(".");
                    int secondPointIndex = currentLink.lastIndexOf(".");
                    String link = "https://github.com" + currentLink;

                    String linkText;
                    if (firstPointIndex == secondPointIndex){
                        linkText = "rar";
                    }else {
                        linkText = currentLink.substring(firstPointIndex+1, secondPointIndex);
                    }
//                    System.out.println(linkText);

                    System.out.println("["+linkText+"]("+link+")");
                }
                System.out.println();
                System.out.println("```");

                System.out.println();
                System.out.println("contentTds.size()==" + contentTds.size());

                EntityUtils.consume(httpEntity);

            }
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    private static String getTempLink(Element element) {
        String tempLink = element.attr("href").replace("blob", "raw");
        String k1 = ".part";
        String k2 = ".rar";
        if (tempLink.contains(k1)) {
            tempLink = tempLink.substring(0, tempLink.indexOf(k1));
        } else {
            tempLink = tempLink.substring(0, tempLink.indexOf(k2));
        }
        return tempLink;
    }

}
