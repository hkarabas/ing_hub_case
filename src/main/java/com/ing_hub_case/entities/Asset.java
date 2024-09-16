package com.ing_hub_case.entities;

import com.ing_hub_case.enums.Currency;
import com.ing_hub_case.models.AssetDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Table(name="asset")
@Entity
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Asset Name Cannot Be Null")
    @Column(name = "asset_name",unique = true, length = 100, nullable = false)
    private String assetName;

    private Long   size;

    @Column(name = "usable_size")
    private Long   usableSize;

    private String currency;

    public AssetDto converAssetDto() {
        AssetDto assetDto = new AssetDto();
        assetDto.setAssetName(this.assetName);
        assetDto.setCurrency(Currency.valueOf(this.currency));
        assetDto.setId(this.id);
        assetDto.setAssetName(this.assetName);
        return assetDto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull(message = "Asset Name Cannot Be Null") String getAssetName() {
        return assetName;
    }

    public void setAssetName(@NotNull(message = "Asset Name Cannot Be Null") String assetName) {
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
