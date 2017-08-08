package fr.insapp.insapp.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import fr.insapp.insapp.App;
import fr.insapp.insapp.activities.ProfileActivity;
import fr.insapp.insapp.http.ServiceGenerator;
import fr.insapp.insapp.models.NotificationUser;
import fr.insapp.insapp.models.User;
import fr.insapp.insapp.notifications.FirebaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {

    public static void convertToLinkSpan(Context context, TextView textView) {
        Spannable s = new SpannableString(textView.getText());

        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);

            s.removeSpan(span);
            span = new LinkSpan(context, span.getURL());
            s.setSpan(span, start, end, 0);
        }

        textView.setText(s);
    }

    public static Bitmap darkenBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Color.RED);
        ColorFilter filter = new LightingColorFilter(0xffbababa, 0x00000000);

        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, new Matrix(), paint);

        return bitmap;
    }
}