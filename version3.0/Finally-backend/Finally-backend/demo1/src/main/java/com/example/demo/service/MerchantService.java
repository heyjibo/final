package com.example.demo.service;

import com.example.demo.entity.Merchant;
import com.example.demo.repository.MerchantRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {
    private final MerchantRespository merchantRespository;

    @Autowired
    public MerchantService(MerchantRespository merchantRespository) {
        this.merchantRespository = merchantRespository;
    }
    public List<Merchant> getMerchants() {
        return merchantRespository.findAll();
    }

    public Merchant getMerchantByID(String merchantId) {
        return merchantRespository.findById(merchantId).orElse(null);
    }

    public Merchant saveMerchant(Merchant merchant) {
        if (merchant.getId() == null) {
            String maxId = merchantRespository.findMaxWorkId();
            int nextId = maxId == null ? 1:Integer.parseInt(maxId) + 1;
            merchant.setId(String.valueOf(nextId));
        }
        merchant.setNumOfIndent(0);
        merchant.setScore(0);
        return merchantRespository.save(merchant);
    }

    public Merchant updateMerchant(Merchant merchant) {
        return merchantRespository.save(merchant);
    }

    public void deleteMerchant(String merchantId) {
        merchantRespository.deleteById(merchantId);
    }

}
