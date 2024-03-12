package freehold.market.controller;

import freehold.market.model.GoodsType;
import freehold.market.model.Shipment;
import freehold.market.services.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shipments")
public class ShipmentController {
    private final ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<List<Shipment>> findAllShipments(){
        return new ResponseEntity<>(shipmentService.getShipments(), HttpStatus.OK);
    }

    @PutMapping("/{type}/{amount}")
    public ResponseEntity<Optional<Shipment>> supply(@PathVariable GoodsType type, @PathVariable int amount){
        Optional<Shipment> shipment = shipmentService.supply(type,amount);
        if(shipment.isPresent()){
            return new ResponseEntity<>(shipment, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
