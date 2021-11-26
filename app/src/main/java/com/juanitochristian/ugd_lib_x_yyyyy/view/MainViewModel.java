package com.juanitochristian.ugd_lib_x_yyyyy.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.juanitochristian.ugd_lib_x_yyyyy.model.Game;
import com.juanitochristian.ugd_lib_x_yyyyy.model.PurchasedGame;
import com.juanitochristian.ugd_lib_x_yyyyy.model.Response;
import com.juanitochristian.ugd_lib_x_yyyyy.model.UserProfile;
import com.juanitochristian.ugd_lib_x_yyyyy.repositories.MainRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private MainRepository repo;

    private MutableLiveData<List<Game>> gameListLiveData;
    private MutableLiveData<String> errMsg;

    private MutableLiveData<List<PurchasedGame>> keranjangBelanja;

    private MutableLiveData<UserProfile> userProfileLiveData;

    private boolean scanning = false;

    {
        gameListLiveData = new MutableLiveData<>(new ArrayList<>());
        keranjangBelanja = new MutableLiveData<>(new ArrayList<>());
        userProfileLiveData = new MutableLiveData<>(null);
        errMsg = new MutableLiveData<>("");
    }

    @Inject
    public MainViewModel(MainRepository repo) {
        this.repo = repo;
    }

    public void loadResponse() {
        try {
            if (this.gameListLiveData.getValue().size() == 0) {
                LiveData<Response> temp = this.repo.getResponse();

                if (temp == null || temp.getValue() == null || temp.getValue().getData().isEmpty())
                    throw new Exception("Data gagal diambil");

                this.gameListLiveData.setValue(temp.getValue().getData());
                this.errMsg.setValue(temp.getValue().getMessage() + " size: " + temp.getValue().getData().size());
            }

        } catch (Exception e) {
            e.printStackTrace();
            errMsg.setValue(e.getMessage());
        }
    }

    /**
     * TODO 3.1 isi logic untuk method scanQRUserProfile()
     * @param jsonString
     * @throws Exception
     * @HINT: gunakan GSON untuk parse jsonString dengan kelas model UserProfile lalu tampung di
     * userProfileLiveData
     */
    public void scanQRUserProfile(String jsonString) throws Exception {
        Gson gson = new Gson();
        UserProfile profile = gson.fromJson(jsonString, UserProfile.class);

        userProfileLiveData.setValue(profile);
        setScan(true);
    }

    /**
     * TODO 1.12 isi logic untuk Method addToCart()
     * Method ini digunakan ketika mau menambahkan game diawal jadi saat milih game dari
     * StoreFragment buat dimasukin ke CartFragment
     *
     * @param game
     * @HINT: gunakan method cariGame() untuk cek apakah game yang dipilih dari storeFragment
     * sudah ada didalam Cart.
     */
    public void addToCart(Game game) {
        PurchasedGame newGame = cariGame(game.getId());
        List<PurchasedGame> list = keranjangBelanja.getValue();

        if(newGame != null)
        {
            //Gamenya sudah ada di cart
            int indexGame = list.indexOf(newGame);
            list.get(indexGame).setJumlah(list.get(indexGame).getJumlah() + 1);
        }
        else
        {
            newGame = new PurchasedGame(game, 1);
            list.add(newGame);
        }
        keranjangBelanja.setValue(list);
    }

    /**
     * TODO BONUS 2.0 isi logic untuk method clearCart
     * Method ini digunakan untuk clearing cart setelah pdf digeneraete
     */
    public void clearCart() {
        List<PurchasedGame> belanjagame = keranjangBelanja.getValue();
        belanjagame.clear();
        keranjangBelanja.setValue(belanjagame);
    }

    /**
     * TODO BONUS 2.3 isi logic untuk method addOneFromCart()
     * Method ini digunakan untuk button tambah di cartFragment
     * @param game
     * @HINT: gunakan method cariGame() untuk mendapat referensi game yang dimaksud
     */
    public void addOneFromCart(Game game) {
        PurchasedGame theGame = cariGame(game.getId());
        List<PurchasedGame> belanjagame = keranjangBelanja.getValue();
        int index = belanjagame.indexOf(theGame);

        belanjagame.get(index).setJumlah(belanjagame.get(index).getJumlah() + 1);
        keranjangBelanja.setValue(belanjagame);

    }

    /**
     * TODO BONUS 2.4 isi logic untuk method removeFromCart()
     * Method ini digunakan untuk button kurang di cartFragment
     * @param game
     */
    public void removeFromCart(Game game) {
        PurchasedGame theGame = cariGame(game.getId());
        List<PurchasedGame> belanjagame = keranjangBelanja.getValue();
        int index = belanjagame.indexOf(theGame);

        if(belanjagame.get(index).getJumlah() >1)
        {
            belanjagame.get(index).setJumlah(belanjagame.get(index).getJumlah() - 1);
        }
        else
        {
            belanjagame.remove(index);
        }
        keranjangBelanja.setValue(belanjagame);
    }

    private PurchasedGame cariGame(int id) {
        if (keranjangBelanja.getValue() != null) {
            List<PurchasedGame> belanjaan = keranjangBelanja.getValue();
            for (PurchasedGame game : belanjaan) {
                if (game.getGame().getId() == id)
                    return game;
            }
        }
        return null;
    }

    public LiveData<List<Game>> getGameListLiveData() {
        return gameListLiveData;
    }

    public LiveData<List<PurchasedGame>> getKeranjangBelanja() {
        return keranjangBelanja;
    }

    public LiveData<String> getErrMsg() {
        return errMsg;
    }

    public LiveData<UserProfile> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    public boolean getScan()
    {
        return scanning;
    }

    public void setScan(boolean scan)
    {
        this.scanning = scan;
    }
}
