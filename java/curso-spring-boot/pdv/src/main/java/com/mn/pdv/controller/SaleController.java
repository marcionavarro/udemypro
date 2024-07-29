package com.mn.pdv.controller;

import com.mn.pdv.dto.ResponseDTO;
import com.mn.pdv.dto.SaleDTO;
import com.mn.pdv.exceptions.InvalidOperationException;
import com.mn.pdv.exceptions.NoItemException;
import com.mn.pdv.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sale")
public class SaleController {

    private SaleService saleService;

    public SaleController(@Autowired SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping()
    public ResponseEntity getAll() {
        return new ResponseEntity<>(saleService.findAll(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity getById(@PathVariable long id) {
        try {
            return new ResponseEntity<>(saleService.getById(id), HttpStatus.OK);
        } catch (Exception error) {
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody SaleDTO saleDTO) {
        try {
            long id = saleService.save(saleDTO);
            return new ResponseEntity<>(new ResponseDTO("Venda realizada com sucesso: " + id), HttpStatus.CREATED);
        } catch (NoItemException | InvalidOperationException error) {
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception error) {
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
