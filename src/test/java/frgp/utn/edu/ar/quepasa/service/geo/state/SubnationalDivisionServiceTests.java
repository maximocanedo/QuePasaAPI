package frgp.utn.edu.ar.quepasa.service.geo.state;

import frgp.utn.edu.ar.quepasa.model.enums.SubnationalDivisionDenomination;
import frgp.utn.edu.ar.quepasa.model.geo.Country;
import frgp.utn.edu.ar.quepasa.model.geo.SubnationalDivision;
import frgp.utn.edu.ar.quepasa.repository.geo.CountryRepository;
import frgp.utn.edu.ar.quepasa.repository.geo.SubnationalDivisionRepository;
import frgp.utn.edu.ar.quepasa.service.geo.impl.SubnationalDivisionServiceImpl;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Servicio de entidades subnacionales")
public class SubnationalDivisionServiceTests {

    @MockBean
    private SubnationalDivisionRepository repository;
    @MockBean
    private CountryRepository countries;
    @Autowired
    private SubnationalDivisionServiceImpl service;

    public SubnationalDivisionServiceTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Creación: Datos correctos")
    public void create() {
        var argentina = new Country();
        argentina.setIso3("ARG");
        var file = new SubnationalDivision();
        file.setLabel("Provincia Cisplatina");
        file.setCountry(argentina);
        file.setIso3("AR-URU");
        file.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        when(repository.existsByIso3("AR-URU")).thenReturn(false);
        when(countries.existsByIso3("ARG")).thenReturn(true);
        doReturn(file).when(repository).save(file);
        assertDoesNotThrow(() -> {
            var saved = service.save(file);
            assertNotNull(saved);
        });

    }

    @Test
    @DisplayName("Creación: ISO 3166-2 Inválido")
    public void createWithInvalidIso31662Code() {
        var argentina = new Country();
        argentina.setIso3("ARG");
        var file = new SubnationalDivision();
        file.setLabel("Provincia Cisplatina");
        file.setCountry(argentina);
        file.setIso3("AR%%$RU");
        file.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        when(repository.existsByIso3("AR-URU")).thenReturn(false);
        when(countries.existsByIso3("ARG")).thenReturn(true);
        doReturn(file).when(repository).save(file);
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
           var saved = service.save(file);
        });

        file.setIso3("ARU");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            var saved = service.save(file);
        });

        file.setIso3(null);
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            var saved = service.save(file);
        });
    }

    @Test
    @DisplayName("Creación: Nombre inválido")
    public void createWithInvalidName() {
        var argentina = new Country();
        argentina.setIso3("ARG");
        var file = new SubnationalDivision();
        file.setLabel("Provincia Cisplatina");
        file.setCountry(argentina);
        file.setLabel("A$·%&ASDF");
        file.setIso3("AR-URU");
        file.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        when(repository.existsByIso3("AR-URU")).thenReturn(false);
        when(countries.existsByIso3("ARG")).thenReturn(true);
        doReturn(file).when(repository).save(file);
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            var saved = service.save(file);
        });

        file.setLabel("a");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            var saved = service.save(file);
        });

        file.setLabel(null);
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            var saved = service.save(file);
        });

    }

    @Test
    @DisplayName("Creación: País inexistente")
    public void createWithInvalidCountry() {
        var uruguay = new Country();
        uruguay.setIso3("UYU");
        var file = new SubnationalDivision();
        file.setLabel("Provincia Cisplatina");
        file.setCountry(uruguay);
        file.setLabel("A$·%&ASDF");
        file.setIso3("AR-URU");
        file.setDenomination(SubnationalDivisionDenomination.PROVINCE);
        when(repository.existsByIso3("AR-URU")).thenReturn(false);
        when(countries.existsByIso3("UYU")).thenReturn(false);
        doReturn(file).when(repository).save(file);
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            var saved = service.save(file);
        });

        file.setLabel("a");
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            var saved = service.save(file);
        });

        file.setLabel(null);
        assertThrows(ValidatorBuilder.ValidationError.class, () -> {
            var saved = service.save(file);
        });
    }

    public Country uruguay() {
        var uruguay = new Country();
        uruguay.setIso3("UYU");
        return uruguay;
    }

    public Country argentina() {
        var argentina = new Country();
        argentina.setIso3("ARG");
        return argentina;
    }

    public SubnationalDivision soriano() {
        var p1 = new SubnationalDivision();
        p1.setCountry(uruguay());
        p1.setLabel("Soriano");
        p1.setIso3("UY-SO");
        return p1;
    }

    public SubnationalDivision santaFe() {
        var p1 = new SubnationalDivision();
        p1.setCountry(argentina());
        p1.setLabel("Santa Fe");
        p1.setIso3("AR-S");
        return p1;
    }

    public SubnationalDivision chubut() {
        var p1 = new SubnationalDivision();
        p1.setCountry(argentina());
        p1.setLabel("Chubut");
        p1.setIso3("AR-U");
        return p1;
    }

    @Test
    @DisplayName("Listar provincias de un país. ")
    public void list() {
        when(repository.getAllFrom(argentina().getIso3())).thenReturn(List.of(santaFe(), chubut()));
        when(repository.getAllFrom(uruguay().getIso3())).thenReturn(List.of(soriano()));
        var list = service.listFrom(argentina().getIso3());
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(santaFe().getIso3(), list.getFirst().getIso3());

        var list2 = service.listFrom(uruguay().getIso3());
        assertNotNull(list2);
        assertEquals(1, list2.size());
        assertEquals(soriano().getIso3(), list2.getFirst().getIso3());
    }

}
