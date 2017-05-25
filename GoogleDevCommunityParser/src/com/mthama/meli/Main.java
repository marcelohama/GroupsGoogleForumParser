package com.mthama.meli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {
	
	private class ThreadRegister {
		public String title;
		public String lastPostDate;
		public String lastPostWriter;
	}
	
	private static String[] MercadoLibreTeam = {
			"Henrique Goncalves Leite"
	};

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, IOException {
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		//System.setOut(new PrintStream(new File("output-file.html")));
		
		//===

		/*
		// This fetch the main list. TODO: we need to deal with dynamic pagination.
		String listUrl = "https://groups.google.com/forum/#!topicsearchin/mercadopago-developers/prestashop";
		HtmlPage listPage = loadPage(listUrl, 10);
		// This is the actual first post of the thread.
		String firstPostUrl = getFirstPostLink(listPage);
		HtmlPage postPage = loadPage(firstPostUrl, 10);
		outputPage(postPage);
		*/
		
		// A manual approach, to cope with dynamic pagination for now.
		List<String> links = getPostLinksFromDoc(createDocFromPageFromFile("prestashop.htm"));
		
		for (int i=0; i<links.size(); i++) {
			HtmlPage porPage = loadPage(links.get(i), 40);
			System.out.println("POST TITLE = " + getPostTitle(porPage));
			List<String> writers = getWriters(porPage);
			List<String> dates = getPostDate(porPage);
			if (writers.size() > 0 && dates.size() > 0) {
				System.out.println("POST LAST WRITER = " + writers.get(writers.size()-1));
				System.out.println("POST LAST DATE = " + dates.get(dates.size()-1));
				if (Arrays.asList(MercadoLibreTeam).contains(writers.get(writers.size()-1))) {
					System.out.println("IS REPLYED = YES");
				} else {
					System.out.println("IS REPLYED = NO");
				}
			}
			System.out.println("=========================");
		}
		
		//outputPage(porPage);
		
		//for (int i=0; i<writers.size(); i++) {
		//	System.out.println(writers.get(i));
		//}
		
		//===
        
	}
	
	//==============================================================================
	// Take an URL and load the page, waiting for JavaScripts to be executed
	public static HtmlPage loadPage(String url, int waitTimeSec)
			throws FailingHttpStatusCodeException, IOException {
		WebClient webClient = new WebClient();
        HtmlPage page = webClient.getPage(url);
        webClient.waitForBackgroundJavaScript(waitTimeSec * 1000);
        webClient.close();
        return page;
	}
	// Take a HTML file and load its content into a Document object
	public static Document createDocFromPageFromFile(String fileName)
			throws IOException {
		String html = new String(Files.readAllBytes(Paths.get(fileName)));
        Document doc = Jsoup.parse(html);
        doc.select("script,.hidden,style").remove();
        return doc;
	}
	//==============================================================================
	
	//==============================================================================
	// Output the entire page sanitized
	public static void outputPage(HtmlPage page) {
        String pageAsXml = page.asXml();
        Document doc = Jsoup.parse(pageAsXml);
        doc.select("script,.hidden,style").remove();
        System.out.println(StringEscapeUtils.unescapeHtml3(doc.html()));
	}
	// Get the table of interest
	public static void outputPostTable(HtmlPage page) {
	    final List<DomElement> tbodys = page.getElementsByTagName("table");
	    for (DomElement element : tbodys) {
	        if (element.getAttribute("class").equals("IVILX2C-p-o")) {
	        	System.out.println(element.asXml());
	        }
	    }
	}
	//==============================================================================
	
	//==============================================================================
	// Get last date
	public static List<String> getPostDate(HtmlPage page) {
		List<String> postLinks = new ArrayList<>();
        List<DomElement> links = page.getElementsByTagName("span");
        for (DomElement element : links) {
            if (element.getAttribute("class").contains("IVILX2C-nb-Q")) {
            	postLinks.add(element.asText());
            }
        }
        return postLinks;
	}
	// Get title
	public static String getPostTitle(HtmlPage page) {
		List<DomElement> links = page.getElementsByTagName("span");
        for (DomElement element : links) {
            if (element.getAttribute("class").contains("IVILX2C-mb-Y")) {
            	return element.asText();
            }
        }
        return null;
	}
	// Get the links of the posts, from the first pagination
	public static List<String> getPostLinks(HtmlPage page) {
		List<String> postLinks = new ArrayList<>();
        List<DomElement> links = page.getElementsByTagName("a");
        for (DomElement element : links) {
            if (element.getAttribute("class").equals("IVILX2C-p-Q")) {
            	postLinks.add("https://groups.google.com/forum/" + element.getAttribute("href"));
            }
        }
        return postLinks;
	}
	// Get the link of the first post, from the first pagination
	public static String getFirstPostLink(HtmlPage page) {
        final List<DomElement> links = page.getElementsByTagName("a");
        for (DomElement element : links) {
            if (element.getAttribute("class").equals("IVILX2C-p-Q")) {
            	return "https://groups.google.com/forum/" + element.getAttribute("href");
            }
        }
        return null;
	}
	// Get all writers
	public static List<String> getWriters(HtmlPage page) {
		List<String> postLinks = new ArrayList<>();
        List<DomElement> links = page.getElementsByTagName("span");
        for (DomElement element : links) {
            if (element.getAttribute("class").contains("IVILX2C-D-a")) {
            	postLinks.add(element.asText());
            }
        }
        return postLinks;
	}
	//==============================================================================
	
	public static List<String> getPostLinksFromDoc(Document doc) throws IOException {
		List<String> postLinks = new ArrayList<>();
		Elements links = doc.getElementsByTag("a");
		for (Element element : links) {
            if (element.attr("class").equals("IVILX2C-p-Q")) {
            	postLinks.add(element.attr("href"));
            }
        }
        return postLinks;
	}

}
