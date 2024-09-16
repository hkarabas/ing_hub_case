package com.ing_hub_case.models;

import com.ing_hub_case.enums.Currency;
import com.ing_hub_case.utils.EnumNamePattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AssetDto {

    private Integer id;

    @NotNull(message = "Asset Name Cannot Be Null")
    private String assetName;

    @NotNull(message ="Asset Size Cannot Be Null")
    @Min(1)
    private Long   size;

    @NotNull(message ="Asset usableSize  Cannot Be Null")
    @Min(1)
    private Long   usableSize;

    @EnumNamePattern(regexp = "TRY|USD|EU")
    @NotNull(message ="Asset Currency Cannot Be Null")
    private Currency currency;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getUsableSize() {
        return usableSize;
    }

    public void setUsableSize(Long usableSize) {
        this.usableSize = usableSize;
    }

    public @EnumNamePattern(regexp = "TRY|USD|EU") Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@EnumNamePattern(regexp = "TRY|USD|EU") Currency currency) {
        this.currency = currency;
    }
}
