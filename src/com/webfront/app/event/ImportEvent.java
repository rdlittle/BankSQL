/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.app.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author rlittle
 */
public class ImportEvent extends Event {

    public ImportEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

}
