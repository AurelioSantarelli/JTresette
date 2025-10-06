package it.uniroma1.tresette.model;


/**
 * Enum che rappresenta i semi delle carte del Tresette.
 */
public enum Seme {
    /** Seme delle coppe (rosso) */
    COPPE("[coppe]"), 
    /** Seme dei denari (rosso) */
    DENARI("[denari]"),
    /** Seme delle spade (nero) */
    SPADE("[spade]"), 
    /** Seme dei bastoni (nero) */
    BASTONI("[bastoni]");

    private final String simbolo;

    Seme(String simbolo) {
        this.simbolo = simbolo;
    }

    /** @return simbolo testuale del seme */
    public String getSimbolo() {
        return simbolo;
    }
}
