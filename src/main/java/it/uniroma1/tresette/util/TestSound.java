package it.uniroma1.tresette.util;

public class TestSound {
    public static void main(String[] args) throws Exception {
        System.out.println("Invoco SoundManager.riproduciSuonoContinuaPartita()");
        it.uniroma1.tresette.view.sound.SoundManager.riproduciSuonoContinuaPartita();
        System.out.println("Chiamata inviata, attendo 4s per dare tempo al Timer/clip");
        Thread.sleep(4000);
        System.out.println("Fine test");
    }
}
