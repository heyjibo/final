package com.example.demo.service;

import com.example.demo.entity.Indent;
import com.example.demo.repository.IndentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndentService {
    private final IndentRepository indentRepository;

    @Autowired
    public IndentService(IndentRepository indentRepository) {
        this.indentRepository = indentRepository;
    }

    public List<Indent> getAllIndents() {
        return indentRepository.findAll();
    }

    @Transactional
    public Indent getIndent(String id) {
        return indentRepository.findById(id).orElse(null);
    }

    public Indent saveIndent(Indent indent) {
        return indentRepository.save(indent);
    }

    public void deleteIndent(String id) {
        indentRepository.deleteById(id);
    }

    public Indent updateIndent(Indent indent) {
        return indentRepository.save(indent);
    }








}
