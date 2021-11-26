package com.juanitochristian.ugd_lib_x_yyyyy.view.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.juanitochristian.ugd_lib_x_yyyyy.adapter.AdapterCardCart;
import com.juanitochristian.ugd_lib_x_yyyyy.adapter.OnCartClickListener;
import com.juanitochristian.ugd_lib_x_yyyyy.databinding.FragmentCartBinding;
import com.juanitochristian.ugd_lib_x_yyyyy.model.PurchasedGame;
import com.juanitochristian.ugd_lib_x_yyyyy.model.UserProfile;
import com.juanitochristian.ugd_lib_x_yyyyy.view.MainViewModel;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CartFragment extends Fragment implements OnCartClickListener {

    private FragmentCartBinding binding;
    private MainViewModel viewModel;

    private AdapterCardCart adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * TODO 2.4 isi logic untuk method onViewCreate()
     * tambahkan onclicklistener untuk button cetakNota yang nanti akan memanggil method createPdf()
     * berikan pengecekan apakah sudah scan qr.
     *
     * @param view
     * @param savedInstanceState
     *
     * TODO BONUS 2.1 panggil method clearCart setelah pdf berhasil dibuat.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getKeranjangBelanja().observe(getViewLifecycleOwner(), data -> {
            setupAdapter(data);
            if (data.size() == 0) {
                binding.cetakNota.setVisibility(View.GONE);
                binding.textKosong.setVisibility(View.VISIBLE);
            } else {
                binding.cetakNota.setVisibility(View.VISIBLE);
                binding.textKosong.setVisibility(View.GONE);
            }
        });

        binding.cetakNota.setOnClickListener(v -> {
            try
            {
                if (viewModel.getScan()) {
                    cetakPdf();
                } else {
                    Toast.makeText(this.getActivity(), "Silahkan Scan QR Code dulu", Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e)
            {
                Toast.makeText(this.getActivity(), "Error Cetak PDF", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * TODO 2.2 isi logic untuk Method cetakPDF()
     * method ini digunakan untuk membuat pdf berdasarkan data game yang dibeli
     * (keranjangBelanja dikelas viewmodel)
     * jangan lupa pada bagian nama pembeli di set sesuai dengan nama pembeli di QR
     *
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    private void cetakPdf() throws FileNotFoundException, DocumentException {

        File folder = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        if(!folder.exists())
        {
            folder.mkdir();
        }

        Date currentTime = Calendar.getInstance().getTime();
        String pdfName = currentTime.getTime() + ".pdf";

        File pdfFile = new File(folder.getAbsolutePath(), pdfName);
        OutputStream outputStream = new FileOutputStream(pdfFile);

        com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Paragraph judul = new Paragraph("NOTA PEMBELIAN GAME STIM \n\n",
                new com.itextpdf.text.Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.BLACK));

        judul.setAlignment(Element.ALIGN_CENTER);
        document.add(judul);

        PdfPTable table = new PdfPTable((new float[]{16, 8}));

        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell cellSupplier = new PdfPCell();
        cellSupplier.setPaddingLeft(20);
        cellSupplier.setPaddingBottom(10);
        cellSupplier.setBorder(Rectangle.NO_BORDER);

        UserProfile profile = viewModel.getUserProfileLiveData().getValue();

        Paragraph kepada = new Paragraph("Kepada Yth: \n" + profile.getFullname() + "\n",
                new com.itextpdf.text.Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK));

        cellSupplier.addElement(kepada);
        table.addCell(cellSupplier);

        Paragraph nomorTanggal = new Paragraph("No: " + "123456789" + "\n\n" + "Tanggal: " +
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentTime) + "\n",
                new com.itextpdf.text.Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK));

        nomorTanggal.setPaddingTop(5);
        table.addCell(nomorTanggal);
        document.add(table);

        com.itextpdf.text.Font font = new com.itextpdf.text.Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
        Paragraph pembuka = new Paragraph("\nBerikut merupakan daftar pembelian game: \n\n", font);
        pembuka.setIndentationLeft(20);
        document.add(pembuka);
        PdfPTable tableHeader = new PdfPTable(new float[]{5, 5, 5, 5, 5});

        tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeader.getDefaultCell().setFixedHeight(30);
        tableHeader.setTotalWidth(PageSize.A4.getWidth());
        tableHeader.setWidthPercentage(100);

        PdfPCell h1 = new PdfPCell(new Phrase("No"));
        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
        h1.setPaddingBottom(5);

        PdfPCell h2 = new PdfPCell(new Phrase("Nama Game"));
        h2.setHorizontalAlignment(Element.ALIGN_CENTER);
        h2.setPaddingBottom(5);

        PdfPCell h3 = new PdfPCell(new Phrase("Jumlah"));
        h3.setHorizontalAlignment(Element.ALIGN_CENTER);
        h3.setPaddingBottom(5);

        PdfPCell h4 = new PdfPCell(new Phrase("Harga Satuan"));
        h4.setHorizontalAlignment(Element.ALIGN_CENTER);
        h4.setPaddingBottom(5);

        PdfPCell h5 = new PdfPCell(new Phrase("Total Harga"));
        h5.setHorizontalAlignment(Element.ALIGN_CENTER);
        h5.setPaddingBottom(5);

        tableHeader.addCell(h1);
        tableHeader.addCell(h2);
        tableHeader.addCell(h3);
        tableHeader.addCell(h4);
        tableHeader.addCell(h5);

        for(PdfPCell cell : tableHeader.getRow(0).getCells())
        {
            cell.setBackgroundColor(BaseColor.PINK);
        }

        document.add(tableHeader);
        PdfPTable tableData = new PdfPTable(new float[]{5, 5, 5, 5, 5});

        tableData.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableData.getDefaultCell().setFixedHeight(30);
        tableData.setTotalWidth(PageSize.A4.getWidth());
        tableData.setWidthPercentage(100);
        tableData.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        List<PurchasedGame> games = viewModel.getKeranjangBelanja().getValue();
        int i = 1;
        for(PurchasedGame game : games)
        {
            tableData.addCell(String.valueOf(i));
            tableData.addCell(game.getGame().getTitle());
            tableData.addCell(String.valueOf(game.getJumlah()));
            tableData.addCell(game.getGame().getFormattedPrice());
            tableData.addCell(game.getTotalHarga());
            i++;
        }

        document.add(tableData);


        String tglDicetak = currentTime.toLocaleString();
        Paragraph p = new Paragraph("\nDicetak tanggal " + tglDicetak + "\nOleh: Juanito Christian Tjandra / 190710168", font);
        p.setAlignment(Element.ALIGN_RIGHT);
        document.add(p);
        document.close();
        previewPdf(pdfFile);
        Toast.makeText(this.getActivity(), "PDF Berhasil dibuat", Toast.LENGTH_SHORT).show();

        viewModel.clearCart();

    }

    /**
     * TODO 2.3 isi logic untuk Method previewPDF()
     * method ini digunakan untuk menampilkan pdf setelah digenerate
     *
     * @param pdfFile
     * @HINT: apabila saat dijalankan method ini terlihat seperti tidak berjalan coba cek apakah
     * hp kalian memiliki aplikasi untuk membuka pdf.
     */
    private void previewPdf(File pdfFile) {
        PackageManager packageManager = getActivity().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List<ResolveInfo> list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if(list.size() > 0)
        {
            Uri uri;
            uri = FileProvider.getUriForFile(this.getActivity(), getActivity().getPackageName() + ".provider", pdfFile);

            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(uri, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            this.getActivity().grantUriPermission(getActivity().getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(pdfIntent);
        }
    }


    /**
     * TODO 2.1 isi logic untuk method setupAdapter()
     * method ini digunakan untuk membuat dan memasukan adapter ke recyclerview
     *
     * @param purchasedGameList
     */
    private void setupAdapter(List<PurchasedGame> purchasedGameList) {
        adapter = new AdapterCardCart(purchasedGameList, new OnCartClickListener() {
            @Override
            public void onTambah(PurchasedGame purchasedGame, int position) {
                CartFragment.this.onTambah(purchasedGame, position);
            }

            @Override
            public void onKurang(PurchasedGame purchasedGame, int position) {
                CartFragment.this.onKurang(purchasedGame, position);
            }
        });

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
    }

    @Override
    public void onTambah(PurchasedGame purchasedGame, int position) {
        viewModel.addOneFromCart(purchasedGame.getGame());
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onKurang(PurchasedGame purchasedGame, int position) {
        viewModel.removeFromCart(purchasedGame.getGame());
        adapter.notifyItemChanged(position);
    }

}