/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webfront.model;

import java.io.Serializable;
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
 * select \
    c.id "Class", c.description "Class Description", \
    c2.id "Category", c2.description "Category Description" \
    from categories c \
    inner join categories c2 \
    on c2.parent = c.id \
    order by c.parent,c.description,c2.description;
 */
@Entity
@Table(name = "categories")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c"),
    @NamedQuery(name = "Category.findById", query = "SELECT c FROM Category c WHERE c.id = :id"),
    @NamedQuery(name = "Category.findByDescription", query = "SELECT c FROM Category c WHERE c.description = :description"),
    @NamedQuery(name = "Category.findAllParent", query = "SELECT c FROM Category c WHERE c.parent = 0 ORDER BY c.description"),
    @NamedQuery(name = "Category.findByParent", query = "SELECT c FROM Category c WHERE c.parent = :parent ORDER BY c.description"),
    @NamedQuery(name = "Category.tree", query = "SELECT c FROM Category c order by c.parent,c.description,c.id")})
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;

    @Column()
    private Integer parent;
    
    @Column
    private Character type;    
    
    public Category() {
    }

    public Category(Integer id) {
        this.id = id;
    }

    public Category(Integer id, String desc) {
        this.id = id;
        description=desc;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public Integer getParent() {
        return this.parent;
    }
    
    public void setParent(Integer parentId) {
        this.parent=parentId;
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
        if (!(object instanceof Category)) {
            return false;
        }
        Category other = (Category) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * @return the type
     */
    public Character getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Character type) {
        this.type = type;
    }

}
