package com.fatbook.fatbookapp.core;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum IngredientUnit {

    TABLE_SPOON("Table spoon", "Table spoons", "tbs"),
    TEA_SPOON("Tea spoon", "Tea spoons", "tsp"),
    GRAM("Gram", "Gram", "g"),
    PCS("Piece", "Pieces", "pcs");

    private final String singleNaming;
    private final String multiplyNaming;
    private final String displayName;

    public String getSingleNaming() {
        return singleNaming;
    }

    public String getMultiplyNaming() {
        return multiplyNaming;
    }

    public String getDisplayName() {
        return displayName;
    }

    //    public String getNaming(double value) {
//        if (value == 1.0) {
//            return singleNaming;
//        } else {
//            return multiplyNaming;
//        }
//    }
}
