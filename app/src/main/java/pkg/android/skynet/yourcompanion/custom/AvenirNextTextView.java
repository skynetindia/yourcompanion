package pkg.android.skynet.yourcompanion.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AvenirNextTextView extends TextView {

    public AvenirNextTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AvenirNextTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvenirNextTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNext.ttc");
        setTypeface(tf ,0);

    }
}
