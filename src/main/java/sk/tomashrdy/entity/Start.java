package sk.tomashrdy.entity;

import sk.tomashrdy.GUI.Frame;

public class Start {
    //Použijem na uloženie prihláseného používatela
    User user;

    //Vráti udaje používatela ktorého tu budem mať uloženeho ako prihlaseneho
    public User getUser() {
        return user;
    }
    // Nastavím si použivátela po prihlásení
    public void setUser(User user) {
        this.user = user;
    }
    //Konštruktor
    public Start() {
    }
    //Metóda pre spustenie programu
    public void spusti(){Frame frame = new Frame(this);}

}
