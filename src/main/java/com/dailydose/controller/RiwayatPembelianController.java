package com.dailydose.controller;

import com.dailydose.dao.PembelianDAO;
import com.dailydose.model.DetailPembelian;
import com.dailydose.model.Pembelian;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RiwayatPembelianController implements Initializable {

    @FXML private TableView<Pembelian>               tabelPembelian;
    @FXML private TableColumn<Pembelian, Integer>    colIdPembelian;
    @FXML private TableColumn<Pembelian, String>     colTanggal;
    @FXML private TableColumn<Pembelian, Double>     colTotal;
    @FXML private TableColumn<Pembelian, String>     colNamaToko;
    @FXML private TableColumn<Pembelian, Integer>    colIdUser;

    @FXML private Label                               lblDetailHeader;
    @FXML private TableView<DetailPembelian>          tabelDetail;
    @FXML private TableColumn<DetailPembelian, String>  colDetailBarang;
    @FXML private TableColumn<DetailPembelian, String>  colDetailNama;
    @FXML private TableColumn<DetailPembelian, Integer> colDetailJumlah;
    @FXML private TableColumn<DetailPembelian, Double>  colDetailHarga;
    @FXML private TableColumn<DetailPembelian, Double>  colDetailSubtotal;

    private final PembelianDAO pembelianDAO = new PembelianDAO();

    private ObservableList<Pembelian>       dataPembelian = FXCollections.observableArrayList();
    private ObservableList<DetailPembelian> dataDetail    = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIdPembelian.setCellValueFactory(d ->
            new SimpleIntegerProperty(d.getValue().getIdPembelian()).asObject());
        colTanggal.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getTanggal().toString()));
        colTotal.setCellValueFactory(d ->
            new SimpleDoubleProperty(d.getValue().getTotalPembayaran()).asObject());
        colNamaToko.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getNamaToko()));
        colIdUser.setCellValueFactory(d ->
            new SimpleIntegerProperty(d.getValue().getIdUser()).asObject());

        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : "Rp " + String.format("%,.0f", val));
            }
        });

        tabelPembelian.setItems(dataPembelian);

        colDetailBarang.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getIdBarang()));
        colDetailNama.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getNamaBarang()));
        colDetailJumlah.setCellValueFactory(d ->
            new SimpleIntegerProperty(d.getValue().getJumlah()).asObject());
        colDetailHarga.setCellValueFactory(d ->
            new SimpleDoubleProperty(d.getValue().getHargaBeli()).asObject());
        colDetailSubtotal.setCellValueFactory(d ->
            new SimpleDoubleProperty(d.getValue().getSubtotal()).asObject());

        colDetailHarga.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : "Rp " + String.format("%,.0f", val));
            }
        });
        colDetailSubtotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : "Rp " + String.format("%,.0f", val));
            }
        });

        tabelDetail.setItems(dataDetail);
        muatRiwayat();
    }

    private void muatRiwayat() {
        dataPembelian.clear();
        dataPembelian.addAll(pembelianDAO.getAllPembelian());
    }

    @FXML
    private void handlePilihTransaksi() {
        Pembelian dipilih = tabelPembelian.getSelectionModel().getSelectedItem();
        if (dipilih == null) return;

        lblDetailHeader.setText("Detail Transaksi Pembelian — ID: " + dipilih.getIdPembelian());

        dataDetail.clear();
        List<DetailPembelian> details = pembelianDAO.getDetailByPembelian(dipilih.getIdPembelian());
        dataDetail.addAll(details);
    }

    @FXML
    private void kembaliDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/dailydose/view/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tabelPembelian.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("DailyDose Inventory — Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
