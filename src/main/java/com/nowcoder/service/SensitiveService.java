package com.nowcoder.service;

import com.nowcoder.controller.HomeController;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        // when does this method be called, and how it works
        try {
            // load txt file
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTXT;
            while ((lineTXT = bufferedReader.readLine()) != null) {
                addKeyword(lineTXT.trim());
            }
            read.close();
        } catch (Exception e) {
            logger.error("filter sensitive words failed" + e.getMessage());
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }c

        StringBuilder result = new StringBuilder();
        /*
         begin
         |
         xyabcbfyx
         |
         position
         */

        String replacement = "***";
        TrieNode tempNode = rootNode;
        // two pointers on string
        int begin = 0;
        int position = 0;
        while(position < text.length()) {
            char c = text.charAt(position);

            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            if (tempNode == null) {
                // not in trie tree
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // find sensitive word
                result.append(replacement);
                position += 1;
                begin = position;
                tempNode = rootNode;
            } else {
                // haven't found but some part matches
                position += 1;
            }
        }
        result.append(text.substring(begin));
        return result.toString();
    }

    private TrieNode rootNode = new TrieNode();

    // add a new keyword
    private void addKeyword(String lineTXT) {
        TrieNode tempNode = rootNode;
        for (int i = 0;i < lineTXT.length(); ++i) {
            Character c = lineTXT.charAt(i);
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            // if not found some started character
            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }
            tempNode = node;
            if ( i == lineTXT.length() - 1)
                node.end = true;
        }
    }

    // filter fu***ck
    private boolean isSymbol(char c) {
        int ic = (int)c;
        // CharUtils.isAsciiAlphanumeric : return true if is in english alphabet
        // (ic < 0x2E80 || ic > 0x9fff) : East Asia words
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9fff);
    }

    // make a trie tree
    private class TrieNode {
        // mark the end of keyword
        private boolean end = false;

        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public void addSubNode(char key, TrieNode node) {
            subNodes.put(key, node);
        }

        // I don't think here should use String
        TrieNode getSubNode(Character key) {
            if (key == null)
                return null;
            return subNodes.get(key);
        }

        boolean isKeywordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end) {
            this.end = end;
        }
    }

}
