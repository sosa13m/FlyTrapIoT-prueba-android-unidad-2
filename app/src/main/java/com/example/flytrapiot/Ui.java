package com.example.flytrapiot;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Utilidades visuales compartidas por las pantallas.
 */
public final class Ui {

    private Ui() { }

    // Umbrales del semáforo de captura (referenciales para el prototipo)
    public static final int UMBRAL_MEDIO = 5;
    public static final int UMBRAL_ALTO = 10;

    public static int colorNivel(int nivel) {
        if (nivel >= UMBRAL_ALTO) return R.color.nivel_alto;
        if (nivel >= UMBRAL_MEDIO) return R.color.nivel_medio;
        return R.color.nivel_bajo;
    }

    public static String textoNivel(int nivel) {
        if (nivel >= UMBRAL_ALTO) return "ALTO";
        if (nivel >= UMBRAL_MEDIO) return "MEDIO";
        return "BAJO";
    }

    /**
     * El encabezado verde se extiende bajo la barra de estado y el contenido
     * respeta la barra de navegación y el teclado.
     */
    public static void ajustarBordes(View root, View header) {
        final int topBase = header.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.ime());
            header.setPadding(header.getPaddingLeft(), topBase + b.top,
                    header.getPaddingRight(), header.getPaddingBottom());
            v.setPadding(b.left, 0, b.right, b.bottom);
            return insets;
        });
    }

    /** Agrega la mosca animada por encima de toda la pantalla (no bloquea toques). */
    public static void agregarMosca(Activity activity) {
        ViewGroup contenido = activity.findViewById(android.R.id.content);
        contenido.addView(new MoscaView(activity), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
