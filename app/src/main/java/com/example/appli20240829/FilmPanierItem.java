
package com.example.appli20240829;

public class FilmPanierItem {
    public int filmId;
    public String titre;

    public FilmPanierItem(int filmId, String titre) {
        this.filmId = filmId;
        this.titre = titre;
    }

    @Override
    public int hashCode() {
        return titre.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FilmPanierItem)) return false;
        FilmPanierItem other = (FilmPanierItem) obj;
        return this.filmId == other.filmId;
    }

    @Override
    public String toString() {
        return titre;
    }
}
