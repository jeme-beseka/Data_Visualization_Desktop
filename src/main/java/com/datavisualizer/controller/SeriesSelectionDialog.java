package com.datavisualizer.controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;

public class SeriesSelectionDialog extends Dialog<List<String>> {
    
    private ListView<String> listView;
    private ObservableList<String> items;
    
    public SeriesSelectionDialog(List<String> availableInputs) {
        setTitle("Select Data Series");
        setHeaderText("Select which inputs to display on the chart");
        
        items = FXCollections.observableArrayList(availableInputs);
        listView = new ListView<>(items);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setPrefHeight(300);
        
        for (int i = 0; i < items.size(); i++) {
            listView.getSelectionModel().select(i);
        }
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.getChildren().addAll(
            new Label("Select the inputs you want to display:"),
            listView,
            createButtonBar()
        );
        
        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new ArrayList<>(listView.getSelectionModel().getSelectedItems());
            }
            return null;
        });
    }
    
    private HBox createButtonBar() {
        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));
        
        Button selectAllBtn = new Button("Select All");
        selectAllBtn.setOnAction(e -> {
            for (int i = 0; i < items.size(); i++) {
                listView.getSelectionModel().select(i);
            }
        });
        
        Button deselectAllBtn = new Button("Deselect All");
        deselectAllBtn.setOnAction(e -> {
            listView.getSelectionModel().clearSelection();
        });
        
        buttonBar.getChildren().addAll(selectAllBtn, deselectAllBtn);
        return buttonBar;
    }
}
