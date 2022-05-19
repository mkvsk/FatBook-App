package com.fatbook.fatbookapp.core;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient implements Serializable {

    private Long pid;

    private String name;

}
