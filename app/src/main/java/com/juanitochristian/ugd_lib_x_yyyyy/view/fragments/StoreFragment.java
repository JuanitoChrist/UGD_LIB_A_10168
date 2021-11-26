package com.juanitochristian.ugd_lib_x_yyyyy.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.juanitochristian.ugd_lib_x_yyyyy.adapter.AdapterCardGame;
import com.juanitochristian.ugd_lib_x_yyyyy.adapter.OnCardClickListener;
import com.juanitochristian.ugd_lib_x_yyyyy.databinding.FragmentStoreBinding;
import com.juanitochristian.ugd_lib_x_yyyyy.model.Game;
import com.juanitochristian.ugd_lib_x_yyyyy.view.MainViewModel;
import com.google.gson.Gson;

import java.util.List;

public class StoreFragment extends Fragment implements OnCardClickListener {

    private FragmentStoreBinding binding;
    private MainViewModel viewModel;

    private AdapterCardGame adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * TODO 1.7 isi logic untuk method onViewCreated()
     * disini kalian hanya perlu mengobserve gameListLiveData dari viewmodel lalu panggil method
     * setupRecyclerview() didalamnya.
     *
     * @param view
     * @param savedInstanceState
     *
     * TODO BONUS 1.1 tambahkan queryTextListener untuk serchview
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.loadResponse();

        viewModel.getGameListLiveData().observe(getViewLifecycleOwner(), this::setupRecyclerView);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

    }

    /**
     * TODO 1.6 isi logic untuk Method setupRecyclerView()
     * method ini digunakan untuk membuat dan memasukan adapter ke recyclerview
     *
     * @param data
     * @HINT: panggil method ini sewaktu live data sedang diobserve
     */
    private void setupRecyclerView(List<Game> data) {


        adapter = new AdapterCardGame(data, new OnCardClickListener() {
            @Override
            public void onClick(Game game) { StoreFragment.this.onClick(game); }

            @Override
            public void onChartClick(Game game) {
            StoreFragment.this.onChartClick(game);}

            @Override
            public void onDescriptionClick(String desc) {
            StoreFragment.this.onDescriptionClick(desc); }
        });
        binding.rvStorePage.setAdapter(adapter);
        binding.rvStorePage.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(Game game) {
        viewModel.addToCart(game);
        Toast.makeText(getContext(), "Game Behasil ditambahkan", Toast.LENGTH_SHORT).show();
    }

    /**
     * TODO 1.9 isi logic untuk Method onChartClick()
     * Method ini digunakan untuk memanggil dialogfragment yang akan menampilkan chart dari
     * jumlah pemain tertinggi setiap tahun.
     *
     * @param game
     * @HINT: gunakan FragmentManager untuk berpindah fragment dengan menambahkan argument bundle yang
     * menyimpan String hasil keluaran GSON dari list peakYearlyOnlineUser.
     */
    @Override
    public void onChartClick(Game game) {
        FragmentManager fragment = getChildFragmentManager();
        ChartDialogFragment dialogFragment = new ChartDialogFragment();

        Gson gson = new Gson();
        String json = gson.toJson(game.getPeakYearlyOnlineUser());


        Bundle bundle = new Bundle();
        bundle.putString("peakYearly", json);

        dialogFragment.setArguments(bundle);

        dialogFragment.show(fragment, "dialogFragment");
    }

    /**
     * TODO 1.8 isi logic untuk Method onDescriptionCLick
     * Method ini digunakan untuk memanggil dialogfragment yang menampilkan deskripsi dari game
     *
     * @param desc
     * @HINT: gunakan FragmentManager untuk berpindah fragment dengan menambahkan argument bundle yang
     * menyimpan string deskripsi
     */
    @Override
    public void onDescriptionClick(String desc) {
        FragmentManager fragment = getChildFragmentManager();
        DescriptionDialogFragment dialogFragment = new DescriptionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("desc", desc);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fragment, "dialogFragment");
    }
}