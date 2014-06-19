package com.alexvasilkov.android.commons.texts;

import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.widget.TextView;

public class TextViewUtils {

    public static void addLinksNoUnderline(TextView textView, int linkifyMode) {
        Linkify.addLinks(textView, linkifyMode);
        if (!(textView.getText() instanceof Spannable)) return;

        Spannable s = (Spannable) textView.getText();
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private static class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}
	}

	private TextViewUtils() {
	}

}
