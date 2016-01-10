package com.example.android.navigationdrawerexample.util;

/**
 * Created by adven on 06.04.14.
 */
public class JSHelper {
    public static final String TEST = "javascript:(function() {" +
            "var body = document.getElementsByTagName('body')[0];\n" +
            "var main = document.getElementById('MainTxt');\n" +
            "if (main) {" +
            "while (body.firstChild) {\n" +
            "    body.removeChild(body.firstChild);\n" +
            "}" +
            "body.appendChild(main);" +
            "}})()";
    //TODO: another name for css-mark class?
    private static final String CONTENT_CSS_MARK = "js-cnt";
    public static final String NEXT_JS = "javascript:(function(){" +
            "$('." + CONTENT_CSS_MARK + "%s').css('outline','none');" +
            "var el=$('." + CONTENT_CSS_MARK + "%s');el.css({outline:'13px solid rgb(0, 255, 148)'});" +
            "var offset=el.offset();" +
            "console.log(offset.left + ' ' + offset.top);" +
            "Android.scrollTo(offset.left,offset.top);" +
            "})()";
    public static final String IMPORT_JS = "javascript:(function(){" +
            "alert($('." + CONTENT_CSS_MARK + "%s').visibleText());" +
            "})()";
    private static final String VISIBLE_TEXT_JS = "jQuery.fn.visibleText=function(){" +
            "return $.map(this.contents(), function(el) {" +
            "  if (el.nodeType === 3) {return $(el).text();}" +
            "  if ($(el).is(':visible')) {return $(el).visibleText();}" +
            "}).join('');" +
            "};";
    private static final String ONSUCCESS_JS = "function() {" +
            VISIBLE_TEXT_JS +
            "var arr=$('div,table,article,section,aside').filter(':visible').filter(function(index){return $(this).visibleText().length > 1000})" +
            ".map(function(_, o){return {elem: $(o),l: $(o).visibleText().length}; }).get();" +
            "arr.sort(function(o1, o2){return o1.l > o2.l ? -1 : o1.l < o2.l ? 1 : 0;});" +
            "$(arr).each(function(i){$(arr[i].elem).css({outline: ($(arr).size()-i)+'px solid rgb(194, 130, 148)'}).addClass('" + CONTENT_CSS_MARK + "' + i);});" +
            "Android.initContents($(arr).size());" +
            "});";
    private static final String ADD_JS = "function getScript(url,success){" +
            "var script=document.createElement('script');" +
            "script.src=url;" +
            "var head=document.getElementsByTagName('head')[0], done=false;" +
            "script.onload=script.onreadystatechange = function(){" +
            "if (!done && (!this.readyState || this.readyState == 'loaded' || this.readyState == 'complete')) {" +
            " done=true;" +
            " success();" +
            " script.onload = script.onreadystatechange = null;" +
            " head.removeChild(script);" +
            "}};" +
            "head.appendChild(script);}";
    public static final String WEB_CLIPPER_JS = "javascript:(function() {" + ADD_JS +
            "getScript('http://code.jquery.com/jquery-latest.min.js'," + ONSUCCESS_JS + "})()";
}
