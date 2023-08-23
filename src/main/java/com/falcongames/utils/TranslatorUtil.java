package com.falcongames.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


public class TranslatorUtil {

    private static int maxLen = 2900;

    public static void main(String[] args) throws IOException {
        String text = "Hello world!";
        //Translated text: Hallo Welt!
        System.out.println("Translated text: " + translate("en", "vi", text));
    }

    private static String getAPItranslate(String langFrom, String langTo, String text) throws IOException {

        String script3 = "https://script.google.com/macros/s/AKfycbwl7j3GK5l0cjz5MWEwwtZ7eYghJ8mhEds477Q6-AUcq45CpauPBcOY7iTe-_U7TqkcGQ/exec";
        String script2 = "https://script.google.com/macros/s/AKfycbxktItuf5kgV6aedJbwcm0i3qDadhSUH7jIVsRQvL484nPnkSWuUdl18yfVyds4jbp_ng/exec";
        String script1 = "https://script.google.com/macros/s/AKfycbxry_fop_lYILXJcdUq9phHHLtcApec3pDDZwJYNAxf6hvCJRvlYKv7YPuNTZXp_oltVQ/exec";
        String urlStr = script3 +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }



    public static String translate(String langFrom ,String langTo, String sequence) throws IOException {

        String[] listParagrapg = sequence.split("\n");
        for(int i = 0 ; i < listParagrapg.length; i ++) {
            System.out.println(i + ": " + listParagrapg[i]);
        }

        String finalResults = "";

        for( int i = 0 ; i < listParagrapg.length; i++) {
            if(listParagrapg[i] != "") {
                String[] listSentence = listParagrapg[i].split("\\.");

                String partSequence = "";
                String result = "";

                for(String sentence : listSentence) {
                    //System.out.println(sentence);
                    partSequence += sentence + ".";
                    if(partSequence.length() >= maxLen) {
                        result += translate(langFrom, langTo, partSequence);
                        partSequence = "";
                    }
                }
                // System.out.println(partSequence);

                result +=  getAPItranslate(langFrom, langTo, partSequence);

                finalResults += "\n" + result;

            } else {
                finalResults += "\n";
            }

        }



        return finalResults.substring(1);
    }

}