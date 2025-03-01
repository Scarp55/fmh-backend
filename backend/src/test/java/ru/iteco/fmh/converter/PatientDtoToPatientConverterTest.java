package ru.iteco.fmh.converter;

import org.junit.jupiter.api.Test;
import ru.iteco.fmh.converter.patient.PatientDtoToPatientConverter;
import ru.iteco.fmh.dao.repository.AdmissionRepository;
import ru.iteco.fmh.dto.patient.PatientDto;
import ru.iteco.fmh.model.Patient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.iteco.fmh.TestUtils.getPatientDto;

class PatientDtoToPatientConverterTest {

    PatientDtoToPatientConverter dto = new PatientDtoToPatientConverter();

    @Test
    void convert() {
        // given
        PatientDto patientDto = getPatientDto();
        Patient patient = dto.convert(patientDto);

        assertAll(
                () -> assertEquals(patientDto.getId(), patient.getId()),
                () -> assertEquals(patientDto.getFirstName(), patient.getFirstName()),
                () -> assertEquals(patientDto.getLastName(), patient.getLastName()),
                () -> assertEquals(patientDto.getMiddleName(), patient.getMiddleName()),
                () -> assertEquals(patientDto.getBirthDate(), patient.getBirthDate().toEpochMilli())
        );
    }
}