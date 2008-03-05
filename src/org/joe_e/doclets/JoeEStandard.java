package org.joe_e.doclets;


import org.joe_e.doclets.html.*;

import com.sun.javadoc.*;


public class JoeEStandard {
    public static int optionLength(String option) {
        return HtmlDoclet.optionLength(option);
    }
    
    public static boolean start(RootDoc root) {
        return HtmlDoclet.start(root);
    }
    
    public static boolean validOptions(String[][] options,
                                   DocErrorReporter reporter) {
        return HtmlDoclet.validOptions(options, reporter);
    }

    public static LanguageVersion languageVersion() {
        return HtmlDoclet.languageVersion();
    }
	
}
