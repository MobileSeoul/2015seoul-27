package com.seoul.publicbooksearcher.data.open_api;

import android.text.Html;
import android.util.Log;

import com.seoul.publicbooksearcher.data.BaseBookRepository;
import com.seoul.publicbooksearcher.data.BookRepository;
import com.seoul.publicbooksearcher.domain.Book;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class NaverBookOpenApi extends BaseBookRepository {

    private final static String TAG = NaverBookOpenApi.class.getName();

    @Override
    public List<Book> selectByKeyword(String keyword) {
        try {
            keyword =  URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://openapi.naver.com/search?key=28c9d970595a4155958faa596c7b38c2&query="+keyword+"&display=20&start=1&target=book_adv&d_titl="+keyword;
        Log.i(TAG, "keyword : " + keyword + " and request Url : " + url);
        List<String> titles = new ArrayList();
        List<Book> books = new ArrayList();

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url);
            Element root = document.getDocumentElement();
            NodeList nodeList = root.getElementsByTagName("item");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    String title = Html.fromHtml(eElement.getElementsByTagName("title").item(0).getTextContent()).toString().replaceAll("\\(.*\\)", "").trim();
                    //String isbn = eElement.getElementsByTagName("isbn").item(0).getTextContent();
                    Log.i(TAG, i + "번째 책 : " + title);
                    if(!titles.contains(title)) {
                        titles.add(title);
                        books.add(new Book(title));
                    }
                }
            }
        } catch (SAXException e) {
            throw new RuntimeException(TAG+" : SAXException");
            //e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(TAG+" : IOException");
            //e.printStackTrace();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(TAG+": ParserConfigurationException");
            //e.printStackTrace();
        }

        return books;
    }

}
