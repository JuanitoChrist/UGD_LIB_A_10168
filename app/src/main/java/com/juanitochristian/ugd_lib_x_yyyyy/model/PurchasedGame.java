package com.juanitochristian.ugd_lib_x_yyyyy.model;

public class PurchasedGame {
    private Game game;
    private int jumlah;
    private double totalBayar;

    public PurchasedGame(Game game, int jumlah) {
        this.game = game;
        this.jumlah = jumlah;
        this.totalBayar = game.getPrice() * jumlah;
    }

    public Game getGame() {
        return game;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
        this.totalBayar = this.jumlah * this.game.getPrice();
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    public String getTotalHarga(){
        return game.getCurrency() + getTotalBayar();
    }

}


