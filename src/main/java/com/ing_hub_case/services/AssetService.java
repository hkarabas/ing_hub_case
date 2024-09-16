package com.ing_hub_case.services;


import com.ing_hub_case.entities.Asset;
import com.ing_hub_case.entities.Order;
import com.ing_hub_case.models.AssetDto;
import com.ing_hub_case.repositories.AssetRepository;
import com.ing_hub_case.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    public AssetService(UserRepository userRepository, AssetRepository assetRepository) {
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
    }
    public List<AssetDto>  getCustomerAssetList(Integer customerId) {
       Set<Order> orderList = userRepository.findById(customerId).orElseThrow(()-> new IllegalArgumentException("Customer not found")).getOrderList();
      return orderList.stream().toList().stream().map(Order::getAsset).map(Asset::converAssetDto).collect(Collectors.toList());
    }

    public AssetDto createAsset(AssetDto assetDto) {
        Asset asset = new Asset();
        asset.setAssetName(assetDto.getAssetName());
        asset.setSize(assetDto.getSize());
        asset.setUsableSize(assetDto.getUsableSize());
        asset.setCurrency(assetDto.getCurrency().toString());
        return assetRepository.save(asset).converAssetDto();
    }



}
