package ppt.reshi.catjokes;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin Regulski on 28.05.2017.
 */

public class XMLCatExtractor {
    private final static String TAG = "XMLCatExtractor";
    private String xml;

    private XMLCatExtractor(String xml) {
        this.xml = xml;
    }

    public static List<String> extract(String xml) {
        try {
            return new XMLCatExtractor(xml).getCatUrls();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<String> getCatUrls() throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xml));
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "response");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "data");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "images");
        List<String> cats = new ArrayList<>();
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("image")) {
                cats.add(readImage(parser));
            } else {
                skip(parser);
            }
        }
        return cats;
    }

    private String readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "image");
        String url = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("url")) {
                url = readText(parser);
            } else {
                skip(parser);
            }
        }
        return url;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
