package com.photofall.rest.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class EANScraper {

    public static void main(String[] args) {
        EANScraper.getName("0846885004821");
    }
    public static void getName(String barcode) {
        try {
            Document doc = Jsoup.connect("http://www.ean-search.org/perl/ean-search.pl?q=" + barcode).get();
            doc.getElementsByTag("a");
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
