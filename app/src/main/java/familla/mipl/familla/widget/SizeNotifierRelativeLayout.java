package familla.mipl.familla.widget;

import android.content.Context;
import android.graphics.Rect;
import android.widget.RelativeLayout;


public class SizeNotifierRelativeLayout extends RelativeLayout {
    private Rect mRect = new Rect();
    public SizeNotifierRelativeLayoutListener listener;

    public interface SizeNotifierRelativeLayoutListener {
        void onSizeChanged(int keyboardHeight);
    }

    public SizeNotifierRelativeLayout(Context context) {
        super(context);
    }

    public SizeNotifierRelativeLayout(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeNotifierRelativeLayout(Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (listener != null) {
            int usableViewHeight = this.getRootView().getHeight();
            this.getWindowVisibleDisplayFrame(mRect);
            int keyboardHeight = usableViewHeight - (mRect.bottom - mRect.top);
           // listener.onSizeChanged(keyboardHeight);
        }
    }



}
