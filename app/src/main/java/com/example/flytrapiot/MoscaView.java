package com.example.flytrapiot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;

import java.util.Random;

/**
 * Mosca de la fruta (Ceratitis capitata) dibujada con Canvas que vuela por la pantalla.
 * Elige destinos al azar, gira de forma suave, aletea y a veces se posa unos segundos.
 * No intercepta toques: la interfaz de abajo sigue funcionando normal.
 */
public class MoscaView extends View {

    private final Paint pCuerpo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pAbdomen = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pBanda = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pOjo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pAla = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pAlaBorde = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pAlaMancha = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pSombra = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF r = new RectF();
    private final Random random = new Random();

    private final float d;              // densidad (px por dp)
    private float x, y, angulo;         // posición y dirección (radianes)
    private float destinoX, destinoY;
    private long pausaHasta = 0;
    private long ultimoFrame = 0;
    private boolean iniciada = false;
    private final boolean animacionesActivas;

    public MoscaView(Context context) {
        super(context);
        d = getResources().getDisplayMetrics().density;

        pCuerpo.setColor(0xFF141210);
        pAbdomen.setColor(0xFF4E3212);
        pBanda.setColor(0xFFE8B931);
        pBanda.setStyle(Paint.Style.STROKE);
        pBanda.setStrokeWidth(0.9f * d);
        pOjo.setColor(0xFF9E2B25);
        pAla.setColor(0x88FFFFFF);
        pAlaBorde.setColor(0x66141210);
        pAlaBorde.setStyle(Paint.Style.STROKE);
        pAlaBorde.setStrokeWidth(0.6f * d);
        pAlaMancha.setColor(0x66C98A1E);
        pSombra.setColor(0x22000000);

        // Si el usuario desactivó las animaciones del sistema, la mosca no vuela
        float escala = Settings.Global.getFloat(context.getContentResolver(),
                Settings.Global.ANIMATOR_DURATION_SCALE, 1f);
        animacionesActivas = escala > 0f;

        setClickable(false);
        setFocusable(false);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void nuevoDestino() {
        int w = getWidth(), h = getHeight();
        destinoX = w * (0.08f + random.nextFloat() * 0.84f);
        destinoY = h * (0.10f + random.nextFloat() * 0.80f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!animacionesActivas || getWidth() == 0) return;

        long ahora = SystemClock.uptimeMillis();
        if (!iniciada) {
            // Entra volando desde el borde izquierdo
            x = -30 * d;
            y = getHeight() * 0.3f;
            angulo = 0;
            nuevoDestino();
            ultimoFrame = ahora;
            iniciada = true;
        }
        float dt = Math.min(ahora - ultimoFrame, 50) / 16f; // independiente de los FPS
        ultimoFrame = ahora;

        boolean posada = ahora < pausaHasta;
        if (!posada) mover(dt, ahora);

        // Sombra (da sensación de altura cuando vuela)
        float alto = posada ? 2 * d : 9 * d;
        r.set(x - 5 * d + alto * 0.5f, y - 3 * d + alto, x + 5 * d + alto * 0.5f, y + 3 * d + alto);
        canvas.drawOval(r, pSombra);

        canvas.save();
        canvas.translate(x, y);
        canvas.rotate((float) Math.toDegrees(angulo) + 90f); // la mosca se dibuja mirando hacia arriba
        dibujarMosca(canvas, posada, ahora);
        canvas.restore();

        postInvalidateOnAnimation();
    }

    private void mover(float dt, long ahora) {
        float dx = destinoX - x, dy = destinoY - y;
        float dist = (float) Math.hypot(dx, dy);

        // Giro suave hacia el destino + un poco de zigzag típico de mosca
        float deseado = (float) Math.atan2(dy, dx);
        float diff = deseado - angulo;
        while (diff > Math.PI) diff -= (float) (2 * Math.PI);
        while (diff < -Math.PI) diff += (float) (2 * Math.PI);
        float giroMax = 0.11f * dt;
        angulo += Math.max(-giroMax, Math.min(giroMax, diff));
        angulo += (random.nextFloat() - 0.5f) * 0.18f * dt;

        float velocidad = 3.0f * d * dt;
        x += (float) Math.cos(angulo) * velocidad;
        y += (float) Math.sin(angulo) * velocidad;

        if (dist < 18 * d) {
            if (random.nextFloat() < 0.4f) {
                pausaHasta = ahora + 900 + random.nextInt(2200); // se posa un rato
            }
            nuevoDestino();
        }
    }

    private void dibujarMosca(Canvas c, boolean posada, long ahora) {
        float s = d * 1.15f;

        // Abdomen con bandas amarillas
        r.set(-2.3f * s, 0.8f * s, 2.3f * s, 8.2f * s);
        c.drawOval(r, pAbdomen);
        c.drawLine(-1.9f * s, 3.4f * s, 1.9f * s, 3.4f * s, pBanda);
        c.drawLine(-1.6f * s, 5.4f * s, 1.6f * s, 5.4f * s, pBanda);

        // Tórax y cabeza
        c.drawCircle(0, 0, 2.7f * s, pCuerpo);
        r.set(-2.5f * s, -4.8f * s, 2.5f * s, -1.4f * s);
        c.drawOval(r, pCuerpo);
        c.drawCircle(-1.6f * s, -3.4f * s, 1.25f * s, pOjo);
        c.drawCircle(1.6f * s, -3.4f * s, 1.25f * s, pOjo);

        // Alas: aletean rápido en vuelo, plegadas hacia atrás cuando está posada
        float abertura;
        if (posada) {
            abertura = 22f;
        } else {
            float aleteo = (float) Math.sin(ahora / 14.0);
            abertura = 48f + 26f * aleteo;
        }
        dibujarAla(c, s, -abertura);
        dibujarAla(c, s, abertura);
    }

    private void dibujarAla(Canvas c, float s, float grados) {
        c.save();
        c.rotate(grados);
        r.set(-2.6f * s, 0.5f * s, 2.6f * s, 11.5f * s);
        c.drawOval(r, pAla);
        c.drawOval(r, pAlaBorde);
        // Manchas ámbar características del ala de Ceratitis
        r.set(-1.6f * s, 4.0f * s, 1.6f * s, 6.2f * s);
        c.drawOval(r, pAlaMancha);
        r.set(-1.2f * s, 7.6f * s, 1.2f * s, 9.4f * s);
        c.drawOval(r, pAlaMancha);
        c.restore();
    }
}
