package pkg.android.skynet.yourcompanion.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ImpactTextView extends TextView {

    public ImpactTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ImpactTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImpactTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/impact.ttf");
        setTypeface(tf ,0);

    }
}
