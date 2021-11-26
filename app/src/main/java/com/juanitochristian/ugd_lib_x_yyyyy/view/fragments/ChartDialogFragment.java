package com.juanitochristian.ugd_lib_x_yyyyy.view.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.juanitochristian.ugd_lib_x_yyyyy.databinding.FragmentChartDialogBinding;
import com.juanitochristian.ugd_lib_x_yyyyy.model.YearlyPeak;
import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ChartDialogFragment extends DialogFragment {

    private FragmentChartDialogBinding binding;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * TODO 1.11 isi logic untuk kelas onCreateDialog
     * di bagian ini kalian perlu ambil data peakYearlyOnlineUser dari bundle lalu gunaain GSON untuk menjadikan
     * data menjadi list kembali. Untuk jenis chartnya kalian bebas menggunakan segala jenis chart yang disediakan library.
     *
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentChartDialogBinding.inflate(LayoutInflater.from(requireContext()));

        APIlib.getInstance().setActiveAnyChartView(binding.myChart);

        Bundle bundle = getArguments();
        Gson gson = new Gson();

        List<YearlyPeak> list = gson.fromJson(bundle.getString("peakYearly"), new TypeToken<List<YearlyPeak>>() {
        }.getType());

        binding.myChart.setProgressBar(binding.progressBar);
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            YearlyPeak year = list.get(i);
            data.add(new ValueDataEntry(year.getYear(),year.getValue()));
        }

        Column column = cartesian.column(data);
        column.tooltip().titleFormat("{%X}").position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d).offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Jumlah Pemain Tertinggi Per Tahun");

        cartesian.yScale().minimum(0d);
        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Tahun");
        cartesian.yAxis(0).title("Jumlah Pemain");


        binding.myChart.setChart(cartesian);

        return new AlertDialog.Builder(requireActivity()).setView(binding.getRoot()).create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}