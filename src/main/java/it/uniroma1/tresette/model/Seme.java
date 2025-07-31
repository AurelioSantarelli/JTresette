package it.uniroma1.tresette.model;

import java.awt.Color;

/**
 * Enum che rappresenta i semi delle carte del Tresette.
 */
public enum Seme {
    COPPE("[coppe]", Color.RED), 
    DENARI("[denari]", Color.RED),
    SPADE("[spade]", Color.BLACK), 
    BASTONI("[bastoni]", Color.BLACK);

    private final String simbolo;
    private final Color colore;

    Seme(String simbolo, Color colore) {
        this.simbolo = simbolo;
        this.colore = colore;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public Color getColore() {
        return colore;
    }
}
