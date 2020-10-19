package pl.milosz.mam_1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

class CustomView extends View {
    float x, y, z, azimut, pitch, roll;
    Double lat, lon;
    //Gmach główny
    float bearingGG;
    float [] vectorGG = new float[3];

    public CustomView(Context context) {
        super(context);
    }

    public void setData(float x, float y, float z, float azimut, float roll, float pitch){
        this.x=x;
        this.y=y;
        this.z=z;
        this.azimut=azimut;
        this.roll=roll;
        this.pitch=pitch;
    }

    public void setLocationData(Double lat, Double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public void setGGData(float bearing, float[] vector){
        this.bearingGG=bearing;
        this.vectorGG=vector;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        //paint.setARGB(255, 255, 0, 0);
        Paint rectPaint = new Paint();
        Rect r1 = new Rect(40, 40, 700, 400);
        Rect r2 = new Rect(580, 700, 1400, 1700);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(Color.LTGRAY);
        canvas.drawRect(r1, rectPaint);
        canvas.drawRect(r2, rectPaint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        // azimut, roll, pitch
            canvas.drawText("X: "+x, 200, 100, paint);
            canvas.drawText("Y: "+y, 200, 150, paint);
            canvas.drawText("Z: "+z, 200, 200, paint);

            // x,y,z
            canvas.drawText("Azimut: "+azimut, 200, 250, paint);
            canvas.drawText("Roll: "+roll, 200, 300, paint);
            canvas.drawText("Pitch: "+pitch, 200, 350, paint);

            canvas.drawText("Lat: "+lat, 600, 800, paint);
            canvas.drawText("Lon: "+lon, 600, 850, paint);

            //Gmach Główny
            canvas.drawText("Gmach Główny: ", 600, 950, paint);
            canvas.drawText("Bearing: "+bearingGG, 600, 1000, paint);
            canvas.drawText("Wektor: [0]:"+vectorGG[0]+" [1]:"+vectorGG[1]+" [2]:"+vectorGG[2], 600, 1050, paint);
            canvas.drawText("Kąt: "+0.0f, 600, 1100, paint);

            //WETI
            canvas.drawText("WETI: ", 600, 1200, paint);
            canvas.drawText("Bearing: "+0.0f, 600, 1250, paint);
            canvas.drawText("Wektor: "+0.0f, 600, 1300, paint);
            canvas.drawText("Kąt: "+0.0f, 600, 1350, paint);

    }
}
