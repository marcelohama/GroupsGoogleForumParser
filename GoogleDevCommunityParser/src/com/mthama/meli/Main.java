package com.mthama.meli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		
		// comment out to turn off annoying htmlunit warnings
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
        
		WebClient webClient = new WebClient();
        String url = "https://groups.google.com/forum/#!topicsearchin/mercadopago-developers-brasil/woocommerce";
        HtmlPage page = webClient.getPage(url);
        
        // will wait JavaScript to execute up to 30s
        webClient.waitForBackgroundJavaScript(30 * 1000);
        
        //System.setOut(new PrintStream(new File("output-file.txt")));
        //System.out.println(page.asXml());
		
		//String url = "https://groups.google.com/forum/#!topicsearchin/mercadopago-developers-brasil/woocommerce";
        
		//try {
		Document document = Jsoup.parse(page.asXml());
	
	        //class IVILX2C-p-w
        Elements threadsList = document.select("tbody[class=IVILX2C-p-w]");
		
        System.setOut(new PrintStream(new File("output-file.txt")));
        System.out.println(threadsList.get(0).html());
	        //for (Element thread : threadsList) {
	              //String title;
	              //Elements titles = thread.select("a[class^=IVILX2C-p-Q]");
	              //title = titles.get(0).html();
	              //System.out.println(thread.html());
	        //}
	        /*String text = document.select("div").first().text();
	        System.out.println(text);
	
	        Elements links = document.select("a");
	        for (Element link : links) {
	            System.out.println(link.attr("href"));
	        }*/
		//} catch (Exception e) {
			//System.out.println(e.getMessage());
		//}
		
	}

}
