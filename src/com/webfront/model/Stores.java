/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import com.webfront.bean.StoresManager;
import java.io.Serializable;
import java.util.Comparator;
import javafx.util.StringConverter;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rlittle
 */
@Entity
@Table(name = "stores")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Stores.findAll", query = "SELECT s FROM Stores s ORDER BY s.storeName"),
    @NamedQuery(name = "Stores.findById", query = "SELECT s FROM Stores s WHERE s.id = :id"),
    @NamedQuery(name = "Stores.findByStoreName", query = "SELECT s FROM Stores s WHERE s.storeName = :storeName")})
public class Stores implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "storeName")
    private String storeName;

    public Stores() {
    }
    
    public Stores(String name) {
        storeName = name;
    }

    public Stores(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stores)) {
            return false;
        }
        Stores other = (Stores) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return storeName;
    }

    public static Comparator<Stores> storeComparator = new Comparator<Stores>() {
        @Override
        public int compare(Stores s1, Stores s2) {
            return s1.getStoreName().compareToIgnoreCase(s2.getStoreName());
        }

    };

    public static class StoreConverter extends StringConverter {

        @Override
        public String toString(Object object) {
            if(object==null) {
                return "";
            }
            Stores store = (Stores) object;
            return store.storeName;
        }

        @Override
        public Object fromString(String string) {
            for(Stores s : StoresManager.getInstance().getStoreList()) {
                if (s.storeName.equalsIgnoreCase(string)) {
                    return s;
                }
            }
            return new Stores(string);
        }
        
    }

}
