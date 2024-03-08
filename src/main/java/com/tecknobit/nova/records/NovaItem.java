package com.tecknobit.nova.records;

import com.tecknobit.apimanager.annotations.Structure;
import jakarta.persistence.*;

@Entity
@Structure
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class NovaItem {

    public static final String IDENTIFIER_KEY = "id";

    @Id
    @Column(name = IDENTIFIER_KEY)
    protected final String id;

    public NovaItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
