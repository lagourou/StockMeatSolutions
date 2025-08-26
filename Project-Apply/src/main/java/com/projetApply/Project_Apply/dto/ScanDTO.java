package com.projetApply.Project_Apply.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanDTO {

    private int id;

    private int userId;

    private String userName;

    private String codeBarre;

    private Timestamp dateScan;

    private ProductDTO product;

}
