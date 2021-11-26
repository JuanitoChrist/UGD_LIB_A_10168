package com.juanitochristian.ugd_lib_x_yyyyy.adapter;

import com.juanitochristian.ugd_lib_x_yyyyy.model.Game;

public interface OnCardClickListener {
    void onClick(Game game);

    void onChartClick(Game game);

    void onDescriptionClick(String desc);
}
