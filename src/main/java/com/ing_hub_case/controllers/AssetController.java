package com.ing_hub_case.controllers;


import com.ing_hub_case.models.AssetDto;
import com.ing_hub_case.services.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/asset")
@RestController
public class AssetController {

    private final AssetService assetService;
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/customerList")
    public ResponseEntity<List<AssetDto>> getListCustomerAsset(@RequestParam  Integer customerId) {
        return  ResponseEntity.ok(assetService.getCustomerAssetList(customerId));
    }

    @PostMapping("/create")
    public ResponseEntity<AssetDto> createAsset(@RequestBody AssetDto assetDto) {
       return new ResponseEntity<AssetDto>( assetService.createAsset(assetDto), HttpStatus.CREATED);
    }


}
