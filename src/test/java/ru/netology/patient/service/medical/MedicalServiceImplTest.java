package ru.netology.patient.service.medical;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicalServiceImplTest {
    PatientInfo patientInfo;

    @Mock
    PatientInfoFileRepository patientInfoFileRepository;

    @Mock
    SendAlertServiceImpl alertService;

    @InjectMocks
    MedicalServiceImpl medicalService;

    @BeforeEach
    void setUp() {
        patientInfo = new PatientInfo("Иван",
                "Петров",
                LocalDate.of(1982, 10, 20),
                new HealthInfo(new BigDecimal("36.6"),
                               new BloodPressure(120,80)));
    }

    @AfterEach
    void tearDown() {
        patientInfo = null;
    }

    @Test
    void testMessageCheckBloodPressure() {
        when(patientInfoFileRepository.getById(patientInfo.getId())).thenReturn(patientInfo);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        medicalService.checkBloodPressure(patientInfo.getId(), new BloodPressure(110, 70));

        String expectedMessage = String.format("Warning, patient with id: %s, need help", patientInfo.getId());

        verify(alertService).send(argumentCaptor.capture());
        assertEquals(expectedMessage, argumentCaptor.getValue());
    }

    @Test
    void testMessageCheckTemperature() {
        when(patientInfoFileRepository.getById(patientInfo.getId())).thenReturn(patientInfo);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        medicalService.checkTemperature(patientInfo.getId(), new BigDecimal("35"));

        String expectedMessage = String.format("Warning, patient with id: %s, need help", patientInfo.getId());

        verify(alertService).send(argumentCaptor.capture());
        assertEquals(expectedMessage, argumentCaptor.getValue());
    }

    @Test
    void testMessageNormalHealth() {
        when(patientInfoFileRepository.getById(patientInfo.getId())).thenReturn(patientInfo);

        medicalService.checkBloodPressure(patientInfo.getId(), new BloodPressure(120, 80));
        medicalService.checkTemperature(patientInfo.getId(), new BigDecimal("37"));

        verify(alertService, Mockito.times(0)).send(anyString());
    }
}