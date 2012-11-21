package pl.itiner.view;

import android.content.Context;
import android.util.AttributeSet;

public class TextView extends android.widget.TextView {

	private String defaultText;

	public TextView(Context context) {
		super(context);

	}

	public TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(attrs);
	}

	public TextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(attrs);
	}

	private void setup(AttributeSet attrs) {
		String namespace = "http://itiner.pl/apk/res/android";
		String attrName = "default_text";
		int res = attrs.getAttributeResourceValue(namespace, attrName, -1);
		defaultText = res != -1 ? getResources().getString(res) : attrs
				.getAttributeValue(namespace, attrName);
		if(defaultText == null) {
			defaultText = "";
		}
	}

	@Override
	public void setText(CharSequence text, BufferType type) {

		if (text == null) {
			text = defaultText;
		}
		super.setText(text, type);
	}
}
