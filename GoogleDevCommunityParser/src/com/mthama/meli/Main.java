package com.mthama.meli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

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
	
	private static String[] communities = {
			"magento.htm",
			"magentobr.htm",
			/*"prestashop.htm",
			"prestashopbr.htm",
			"opencart.htm",
			"opencartbr.htm",
			"woocommerce.htm",
			"woocommercebr.htm",
			"shopify.htm",
			"shopifybr.htm",
			"virtuemart.htm",
			"virtuemartbr.htm",
			"wpecommerce.htm",
			"wpecommercebr.htm",
			"zencart.htm",
			"zencartbr.htm",
			"oscommerce.htm",
			"oscommercebr.htm"*/
	};
	
	private static String[] MercadoLibreTeam = {
			"MagentoDev",
			"Henrique Goncalves Leite",
			"MercadoPago Developers Community",
			"Gabriel Matsuoka",
			"Ramiro MP",
			"Nicolás Roberts",
			"Micaela Greisoris",
			"Developers",
			"Developer",
			"Stefania Limardi (MercadoPago)",
			"Marcelo Hama",
			"Sebastián Gun (MercadoPago)",
			"Marcelo T. Hama",
			"Modulos Mercado Pago",
			"FLAVIO AUGUSTO TEIXEIRA",
			"Bruno de Oliva Bemfica",
			"Matias Gordon (MercadoPago)",
			"Horacio Casatti (MercadoPago)",
			"Ricardo Brito de Souza",
			"Victor Vasconcellos",
			"Fabio Vaccaro (MercadoPago)",
			"Rafael de Aquino Cunha",
			"Nathan Rodrigues"
	};

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, IOException {
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		
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
		
		for (int j=0; j<communities.length; j++) {
			// Output to file.
			System.setOut(new PrintStream(new File("output/" + communities[j] + "-output.txt")));
			// A manual approach, to cope with dynamic pagination for now.
			List<String> links = getPostLinksFromDoc(createDocFromPageFromFile(communities[j]));
			// Fill file with crawled information
			for (int i=0; i<links.size(); i++) {
				String OUT = "Google Dev Community";
				HtmlPage porPage = loadPage(links.get(i), 40);
				List<String> writers = getWriters(porPage);
				List<String> dates = getPostDate(porPage);
				if (writers.size() > 0 && dates.size() > 0) {
					// output writer of the first post
					OUT += " ***** " + writers.get(0);
					// output date of the first post
					OUT += " ***** " + getFormatedDate(dates.get(0));
					// output writer of the last post
					OUT += " ***** " + writers.get(writers.size()-1);
					// output date of the last post
					OUT += " ***** " + getFormatedDate(dates.get(dates.size()-1));
					// output post title
					OUT += " ***** " + getPostTitle(porPage);
					// check last post writer and, if he/she is on team, mark as replied
					OUT += " ***** " + (Arrays.asList(MercadoLibreTeam).contains(writers.get(writers.size()-1)) ?
							"Respondido" : "Pendente");
					// output url
					OUT += " ***** " + links.get(i);
				}
				System.out.println(OUT);
			}
		}
		
		/*
		// Output to file.
		System.setOut(new PrintStream(new File("output-file.html")));
		
		// A manual approach, to cope with dynamic pagination for now.
		List<String> links = getPostLinksFromDoc(createDocFromPageFromFile("magentobr.htm"));
		
		for (int i=0; i<links.size(); i++) {
			String OUT = "Google Dev Community";
			HtmlPage porPage = loadPage(links.get(i), 40);
			List<String> writers = getWriters(porPage);
			List<String> dates = getPostDate(porPage);
			if (writers.size() > 0 && dates.size() > 0) {
				OUT += " ***** " + writers.get(writers.size()-1);
				OUT += " ***** " + dates.get(dates.size()-1);
				OUT += " ***** " + getPostTitle(porPage);
				OUT += " ***** " + "Pendente";
				OUT += " ***** " + links.get(i);
			}
			System.out.println(OUT);
		}
		*/
		
		//===
        
	}
	
	public static String getFormatedDate(String date) {
		String formated = date;
		String[] months = {"jan","fev","mar","abr","mai","jun","jul","ago","set","out","nov","dez"};
		if (date.contains(" de ")) {
			String[] s = date.split(" de ");
			formated = s[0] + "/";
			for (int i=0; i<months.length; i++) {
				if (s[1].equals(months[i])) {
					DecimalFormat df = new DecimalFormat("00");
					formated += df.format(i+1);
				}
			}
			formated += "/17";
		}
		return formated;
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
