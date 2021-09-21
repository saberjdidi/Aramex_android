package tn.bchat.aramex.Models;

public class Tarif {
    String expediteur, destinateur, poid;
    double unite;

    public Tarif() {
    }

    public Tarif(String expediteur, String destinateur, String poid, double unite) {
        this.expediteur = expediteur;
        this.destinateur = destinateur;
        this.poid = poid;
        this.unite = unite;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }

    public String getDestinateur() {
        return destinateur;
    }

    public void setDestinateur(String destinateur) {
        this.destinateur = destinateur;
    }

    public String getPoid() {
        return poid;
    }

    public void setPoid(String poid) {
        this.poid = poid;
    }

    public double getUnite() {
        return unite;
    }

    public void setUnite(double unite) {
        this.unite = unite;
    }
}
