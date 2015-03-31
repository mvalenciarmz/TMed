package mvalenciarmz.com.tarjetamedica;

import android.app.Application;

/**
 * Created by admin on 16/03/2015.
 * Clase global para mantener variables globales
 */
public class TMed extends Application {

    private String idActual;
    private String nombreActual;

    private String idEventoActual;
    private String fechaEventoActual;
    private String horaEventoActual;

    public String getIdActual() {
        return idActual;
    }
    public void setIdActual( String idActual ) {
        this.idActual = idActual;
    }

    public String getNombreActual() {
        return nombreActual;
    }
    public void setNombreActual( String nombreActual ) {
        this.nombreActual = nombreActual;
    }

    public String getIdEventoActual() {
        return idEventoActual;
    }
    public void setIdEventoActual( String idEventoActual ) { this.idEventoActual = idEventoActual; }

    public String getFechaEventoActual() { return fechaEventoActual; }
    public void setFechaEventoActual( String fechaEventoActual ) { this.fechaEventoActual = fechaEventoActual; }

    public String getHoraEventoActual() { return horaEventoActual; }
    public void setHoraEventoActual( String horaEventoActual ) { this.horaEventoActual = horaEventoActual; }

}
