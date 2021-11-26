package com.juanitochristian.ugd_lib_x_yyyyy.adapter;

import com.juanitochristian.ugd_lib_x_yyyyy.model.PurchasedGame;

public interface OnCartClickListener {
    void onTambah(PurchasedGame purchasedGame, int position);
    void onKurang(PurchasedGame purchasedGame, int position);
}
