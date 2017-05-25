package com.mthama.meli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {

	public static void main(String[] args) throws
		FailingHttpStatusCodeException, IOException {
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		String url = "https://groups.google.com/forum/#!topicsearchin/mercadopago-developers-brasil/woocommerce";
		
		//===
		
		// Load and wait JavaScript to execute up to 30s
		WebClient webClient = new WebClient();
        System.out.println("Loading page now: " + url);
        HtmlPage page = webClient.getPage(url);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        webClient.close();
        String pageAsXml = page.asXml();
        
        // JSOUP parser
        Document doc = Jsoup.parse(pageAsXml);
        doc.select("script,.hidden,style").remove();
        
        // At this point, we have a pure HTML loaded after JavaScript execution
        
		//===
		
        // Output to file
        System.setOut(new PrintStream(new File("output-file.html")));
        System.out.println(StringEscapeUtils.unescapeHtml3(doc.html()));
		
	}

}
