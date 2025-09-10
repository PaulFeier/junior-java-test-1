package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.CarHistoryEventDto;
import com.example.carins.web.dto.InsuranceClaimRequestDto;
import com.example.carins.web.dto.InsurancePolicyDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository claimRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, InsuranceClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) throws NoSuchElementException {
        if (carId == null || date == null) return false;
        // TODO: optionally throw NotFound if car does not exist
        carRepository.findById(carId).orElseThrow();
        ResponseEntity.notFound();
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public ResponseEntity<?> registerNewPolicy(Long carId, InsurancePolicy insurance) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + carId));
        insurance.setCar(car);
        insurance.setEndDate(insurance.getStartDate().plusYears(1));

        InsurancePolicy savedPolicy = policyRepository.saveAndFlush(insurance);

        return ResponseEntity.ok(new InsurancePolicyDto(
                savedPolicy.getId(),
                savedPolicy.getCar(),
                savedPolicy.getProvider(),
                savedPolicy.getStartDate(),
                savedPolicy.getEndDate()
        ));
    }

    public ResponseEntity<?> updateExistingPolicy(Long carId, InsurancePolicy insurance) {
        Car car = carRepository.findById(carId).orElseThrow();
        insurance.setCar(car);

        InsurancePolicy updatedPolicy = policyRepository.save(insurance);

        return ResponseEntity.ok(new InsurancePolicyDto(
                updatedPolicy.getId(),
                updatedPolicy.getCar(),
                updatedPolicy.getProvider(),
                updatedPolicy.getStartDate(),
                updatedPolicy.getEndDate()
        ));
    }

    public InsuranceClaim registerNewInsuranceClaim(Long carId, InsuranceClaimRequestDto claimRequest) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + carId));

        InsuranceClaim newClaim = new InsuranceClaim();
        newClaim.setClaimDate(claimRequest.getClaimDate());
        newClaim.setDescription(claimRequest.getDescription());
        newClaim.setAmount(claimRequest.getAmount());
        newClaim.setCar(car);

        return claimRepository.save(newClaim);
    }

    public List<CarHistoryEventDto> getCarHistory(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow();

        List<CarHistoryEventDto> events = new ArrayList<>();

        events.add(new CarHistoryEventDto(
                LocalDate.now(),
                "CAR_REGISTERED",
                "Car with ID " + car.getId() + " was registered."
        ));

        // Event: Owner (if exists)
        if (car.getOwner() != null) {
            events.add(new CarHistoryEventDto(
                    LocalDate.now(),
                    "OWNER_ASSIGNED",
                    "Owner " + car.getOwner().getName() + " assigned to car."
            ));
        }

        // Events: Insurance policies
        for (InsurancePolicy p : policyRepository.findByCarId(carId)) {
            events.add(new CarHistoryEventDto(
                    p.getStartDate(),
                    "INSURANCE_START",
                    "Insurance with " + p.getProvider() + " started."
            ));
            events.add(new CarHistoryEventDto(
                    p.getEndDate(),
                    "INSURANCE_END",
                    "Insurance with " + p.getProvider() + " ended."
            ));
        }

        // Sort chronologically
        events.sort(Comparator.comparing(CarHistoryEventDto::date));

        return events;
    }

}
