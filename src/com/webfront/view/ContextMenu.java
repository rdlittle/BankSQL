/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.view;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;

/**
 *
 * @author rlittle
 */
public class ContextMenu extends Popup {
    
    public ContextMenu() {
        this.setHeight(50.0);
        this.setWidth(50.0);
        Rectangle rect;
        rect = new Rectangle();
        rect.setWidth(50);
        rect.setHeight(50);
        rect.setFill(Color.WHITE);
        this.getContent().addAll(rect);
        this.getContent().add(new VBox());
        this.getContent().add(new Label("Click"));
    }
    
}
