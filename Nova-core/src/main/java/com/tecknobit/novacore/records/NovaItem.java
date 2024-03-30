package com.tecknobit.novacore.records;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.JsonHelper;
import jakarta.persistence.*;
import org.json.JSONObject;

/**
 * The {@code NovaItem} class is useful to create a Nova's item giving the basis utils to work correctly
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Entity
@Structure
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class NovaItem {

    /**
     * {@code IDENTIFIER_KEY} the key for the <b>"id"</b> field
     */
    public static final String IDENTIFIER_KEY = "id";

    /**
     * {@code id} identifier of the item
     */
    @Id
    @Column(name = IDENTIFIER_KEY)
    protected final String id;

    /**
     * {@code hItem} helper to work with JSON values
     */
    protected final JsonHelper hItem;

    /**
     * Constructor to init the {@link NovaItem} class
     *
     * @param jItem: item formatted as JSON
     *
     */
    public NovaItem(JSONObject jItem) {
        hItem = new JsonHelper(jItem);
        id = hItem.getString(IDENTIFIER_KEY);
    }

    /**
     * Constructor to init the {@link NovaItem} class
     *
     * @param id: identifier of the item
     *
     */
    public NovaItem(String id) {
        hItem = null;
        this.id = id;
    }

    /**
     * Method to get {@link #id} instance <br>
     * No-any params required
     *
     * @return {@link #id} instance as {@link String}
     */
    public String getId() {
        return id;
    }

}
