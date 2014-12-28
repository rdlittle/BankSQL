/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.view;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author rlittle
 */
public class StoreContextMenu extends ContextMenu {
    private MenuItem editItem;
    private MenuItem deleteItem;
    
    public StoreContextMenu() {
        this.setHeight(50.0);
        this.setWidth(50.0);
        
        editItem=new MenuItem("Edit");
        deleteItem=new MenuItem("Delete");
        
        editItem.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                editItem.fire();
                System.out.println("edit source "+event.getSource().toString());
                System.out.println("edit target "+event.getTarget().toString());
                System.out.println("edit eventType "+event.getEventType().getName());
            }
        });
        
        deleteItem.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                System.out.println("delete "+event.getSource());
                delete();
            }
        });        
        
        getItems().addAll(editItem, deleteItem);
    }
    
    public void delete() {
        System.out.println("delete store");
    }

    /**
     * @return the editItem
     */
    public MenuItem getEditItem() {
        return editItem;
    }

    /**
     * @param editItem the editItem to set
     */
    public void setEditItem(MenuItem editItem) {
        this.editItem = editItem;
    }
    
}
